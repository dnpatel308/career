package com.brodos.reservation.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.entity.Article;
import com.brodos.reservation.entity.ArticleId;
import com.brodos.reservation.entity.DeviceReservationStatus;
import com.brodos.reservation.entity.EAN;
import com.brodos.reservation.entity.SerialNumber;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.TenantId;
import com.brodos.reservation.entity.VoucherEventsStatus;
import com.brodos.reservation.events.SerialNumberImportEvent;
import com.brodos.reservation.events.SerialNumberRelocationEvent;
import com.brodos.reservation.exception.DeviceReservationException;
import com.brodos.reservation.infrastructure.ArticleRepository;
import com.brodos.reservation.infrastructure.SerialNumberRepository;
import com.brodos.reservation.infrastructure.SerialNumberReservationRepository;
import com.brodos.reservation.service.ArticleService;
import com.brodos.reservation.service.DeviceReservationService;
import org.springframework.transaction.annotation.Transactional;
import com.brodos.reservation.service.SerialNumberEventHandlerService;

@Service
@Transactional
public class SerialNumberEventHandlerServiceImpl implements SerialNumberEventHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(SerialNumberEventHandlerServiceImpl.class);

    @Autowired
    SerialNumberRepository serialNumberRepository;

    @Autowired
    SerialNumberReservationRepository serialNumberReservationRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleService articleService;

    @Autowired
    DeviceReservationService deviceReservationService;

    @Override
    public boolean importSerialNoInPool(SerialNumberImportEvent serialNumberImportEvent) {
        SerialNumber serialNumber = getSerilNumberObjectForIMEI(serialNumberImportEvent);
        if (deviceReservationService.reserveOpencaseIfExist(serialNumber)) {
            LOG.info("Handled opencase with serialNo={} for warehousePool={}", serialNumberImportEvent.getSerialNo(),
                serialNumberImportEvent.getToWarehouseNo());
            return true;
        }
        serialNumberRepository.save(serialNumber);
        LOG.info("Imported serialNo={} into warehousePool={}", serialNumberImportEvent.getSerialNo(),
            serialNumberImportEvent.getToWarehouseNo());
        return true;
    }

    @Override
    public SerialNumber getSerilNumberObjectForIMEI(SerialNumberImportEvent serialNumberImportEvent) {
        Article article = getArticleForEanAndArticleId(serialNumberImportEvent);
        SerialNumber serialNumber =
            serialNumberRepository.findByNumberAndArticle(serialNumberImportEvent.getSerialNo(),
                serialNumberImportEvent.getArticleNo());
        if (serialNumber != null) {
            LOG.info("Existing serialNumber={}", serialNumber.getNumber());
            return reImportSerialNumber(serialNumberImportEvent, serialNumber, article);
        }
        LOG.info("Creating new serialNumber for serialNo={}", serialNumberImportEvent.getSerialNo());
        serialNumber = new SerialNumber();
        serialNumber.setArticle(article);
        serialNumber.setReservable(true);
        serialNumber.setNumber(serialNumberImportEvent.getSerialNo());
        serialNumber.setCreatedBy(serialNumberImportEvent.getUser());
        serialNumber.setArchived(false);
        serialNumber.setWarehouseNo(serialNumberImportEvent.getToWarehouseNo());
        if (article.getArticleEAN() != null) {
            for (EAN ean : article.getArticleEAN()) {
                if (ean.getCode() != null) {
                    serialNumber.setEan(ean.getCode());
                    break;
                }
            }
        }
        return serialNumber;
    }

    private Article getArticleForEanAndArticleId(SerialNumberImportEvent serialNumberImportEvent) {
        Optional<Article> article = null;
        article =
            articleRepository.findById(new ArticleId(serialNumberImportEvent.getArticleNo(), new TenantId(
                serialNumberImportEvent.getTenantId())));
        if (article.isPresent() && !article.get().getArticleEAN().isEmpty()) {
            LOG.info("Found existing article articleNo={}", serialNumberImportEvent.getArticleNo());
            return article.get();
        }
        Article articleSync =
            articleService.syncBrodosArticlesByArticleNo(serialNumberImportEvent.getArticleNo(),
                serialNumberImportEvent.getTenantId(), "DEU");
        LOG.debug("Article sync={} for articleNo={}", articleSync, serialNumberImportEvent.getArticleNo());
        return articleSync;

    }

    private SerialNumber reImportSerialNumber(SerialNumberImportEvent serialNumberImportEvent,
        SerialNumber serialNumber, Article article) {
        if (article.getArticleEAN() != null) {
            for (EAN ean : article.getArticleEAN()) {
                if (ean.getCode() != null) {
                    serialNumber.setEan(ean.getCode());
                    break;
                }
            }
        }
        if (Boolean.TRUE.equals(serialNumber.getArchived())) {
            LOG.debug("IsArchieved={}", serialNumber.getArchived());
            return unArchiveSerialNumber(serialNumber, serialNumberImportEvent.getUser(),
                serialNumberImportEvent.getToWarehouseNo());
        }
        if (serialNumberImportEvent.isRestoreRequest()) {
            if (!serialNumber.getArticle().getArticleId().getArticleNumber()
                .equalsIgnoreCase(serialNumberImportEvent.getArticleNo())) {
                throw new DeviceReservationException(ErrorCodes.DEVICE_ALREADY_AVAILABLE_INPOOL_WITH_DIFF_ARTICLE);
            }

            if (Boolean.FALSE.equals(serialNumber.getReservable()) && Boolean.FALSE.equals(serialNumber.getRelocated())) {
                checkIMEIAlreadyReserved(serialNumber);
            }

            if (Boolean.TRUE.equals(serialNumber.getReservable()) && Boolean.FALSE.equals(serialNumber.getRelocated())
                && serialNumber.getWarehouseNo().equals(serialNumberImportEvent.getToWarehouseNo())) {
                throw new DeviceReservationException(ErrorCodes.DEVICE_ALREADY_AVAILABLE_INPOOL.putMetadata(
                    VoucherEventsStatus.IGNORED.name(), Boolean.TRUE));
            }

            return makeSerialNumberReservable(serialNumber, serialNumberImportEvent.getUser(),
                serialNumberImportEvent.getToWarehouseNo());
        }

        serialNumber.setArticle(article);
        return makeSerialNumberReservable(serialNumber, serialNumberImportEvent.getUser(),
            serialNumberImportEvent.getToWarehouseNo());
    }

    private void checkIMEIAlreadyReserved(SerialNumber serialNumber) {
        SerialNumberReservation serialNumberReservationRequest = new SerialNumberReservation();
        serialNumberReservationRequest.setSerialNumber(serialNumber);
        List<SerialNumberReservation> serialNumberReservations
                = serialNumberReservationRepository.findAll(Example.of(serialNumberReservationRequest));
        serialNumberReservations.forEach(serialNumberReservation -> {
            if (serialNumberReservation.getStatus().equals(DeviceReservationStatus.RESERVED)) {
                throw new DeviceReservationException(ErrorCodes.DEVICE_ALREADY_RESERVERD);
            } else if (serialNumberReservation.getStatus().equals(DeviceReservationStatus.REQUESTFORSENTOUT)) {
                throw new DeviceReservationException(ErrorCodes.DEVICE_ALREADY_REQUESTEDFORSENTOUT);
            }
        });
    }

    private SerialNumber makeSerialNumberReservable(SerialNumber serialNumber, String user, int toWarehouseNo) {
        serialNumber.setReservable(true);
        serialNumber.setModifiedBy(user);
        serialNumber.setModifiedDate(new Date());
        serialNumber.setRelocated(false);
        serialNumber.setArchived(false);
        serialNumber.setTicketNumber(null);
        serialNumber.setWarehouseNo(toWarehouseNo);
        return serialNumber;
    }

    private SerialNumber unArchiveSerialNumber(SerialNumber serialNumber, String user, int toWarehouseNo) {
        serialNumber.setArchived(false);
        serialNumber.setReservable(true);
        serialNumber.setRelocated(false);
        serialNumber.setModifiedBy(user);
        serialNumber.setModifiedDate(new Date());
        serialNumber.setTicketNumber(null);
        serialNumber.setWarehouseNo(toWarehouseNo);
        return serialNumber;

    }

    @Override
    public boolean relocateSerialNoFromPool(SerialNumberRelocationEvent serialNumberRelocationEvent) {
        LOG.info("Relocating serialNo={}", serialNumberRelocationEvent.getSerialNo());
        SerialNumber serialNumber =
            serialNumberRepository.findByNumberAndArticle(serialNumberRelocationEvent.getSerialNo(),
                serialNumberRelocationEvent.getArticleNo());
        validatSerialNoForRelocation(serialNumber, serialNumberRelocationEvent);
        serialNumber.setWarehouseNo(serialNumberRelocationEvent.getToWarehouseNo());
        updateToRelocated(serialNumber, serialNumberRelocationEvent.getUser());
        return true;
    }

    private void validatSerialNoForRelocation(SerialNumber serialNumber, SerialNumberRelocationEvent serialNumberRelocationEvent) {
        if (serialNumber == null || serialNumber.getArchived()) {
            throw new DeviceReservationException(ErrorCodes.SERIAL_NO_MISSING_REQUEST.putMetadata(VoucherEventsStatus.IGNORED.name(), Boolean.TRUE));
        } else if (Boolean.TRUE.equals(serialNumber.getRelocated()) && serialNumber.getWarehouseNo() == serialNumberRelocationEvent.getToWarehouseNo()) {
            throw new DeviceReservationException(ErrorCodes.SERIAL_NO_RELOCATED.putMetadata(VoucherEventsStatus.IGNORED.name(), Boolean.TRUE));
        } else if (Boolean.FALSE.equals(serialNumber.getReservable())) {
            List<SerialNumberReservation> serialNumberReservations = getSerialNumberReservationList(serialNumber);
            serialNumberReservations.forEach(serialNumberReservation -> {
                switch (serialNumberReservation.getStatus()) {
                    case RESERVED: {
                        throw new DeviceReservationException(ErrorCodes.DEVICE_ALREADY_RESERVERD);
                    }

                    case REQUESTFORSENTOUT: {
                        throw new DeviceReservationException(ErrorCodes.DEVICE_ALREADY_REQUESTEDFORSENTOUT);
                    }

                    case SENTOUT: {
                        throw new DeviceReservationException(ErrorCodes.DEVICE_ALREADY_SENTOUT);
                    }
                }
            });

        }
    }

    public List<SerialNumberReservation> getSerialNumberReservationList(SerialNumber serialNumber) {
        SerialNumberReservation serialNumberReservationRequest = new SerialNumberReservation();
        serialNumberReservationRequest.setSerialNumber(serialNumber);
        return serialNumberReservationRepository.findAll(Example.of(serialNumberReservationRequest));
    }

    private void updateToRelocated(SerialNumber serialNumber, String user) {
        serialNumber.setRelocationRequested(true);
        serialNumber.setRelocated(true);
        serialNumber.setReservable(false);
        serialNumber.setModifiedBy(user);
        serialNumber.setModifiedDate(new Date());
        serialNumber.setTicketNumber(null);
        serialNumberRepository.save(serialNumber);
    }
}
