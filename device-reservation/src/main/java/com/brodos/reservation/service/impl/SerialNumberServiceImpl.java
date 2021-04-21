/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service.impl;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.assembler.SerialNumberAssembler;
import com.brodos.reservation.component.DeviceContextComponent;
import com.brodos.reservation.dto.ProductCodeDTO;
import com.brodos.reservation.dto.request.SerialDTO;
import com.brodos.reservation.dto.request.SerialNumberActionDTO;
import com.brodos.reservation.dto.request.SerialNumberRequestDTO;
import com.brodos.reservation.entity.Article;
import com.brodos.reservation.entity.ArticleId;
import com.brodos.reservation.entity.EAN;
import com.brodos.reservation.entity.SerialNumber;
import com.brodos.reservation.entity.TenantId;
import com.brodos.reservation.exception.DeviceReservationException;
import com.brodos.reservation.infrastructure.ArticleRepository;
import com.brodos.reservation.infrastructure.EANRepository;
import com.brodos.reservation.infrastructure.ProductCodeRepository;
import com.brodos.reservation.infrastructure.SerialNumberRepository;
import com.brodos.reservation.service.ArticleService;
import com.brodos.reservation.service.DeviceReservationService;
import com.brodos.reservation.service.SerialNumberService;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author padhaval
 */
@Service
@Transactional
public class SerialNumberServiceImpl implements SerialNumberService {

    private static final Logger LOG = LoggerFactory.getLogger(SerialNumberServiceImpl.class);

    @Autowired
    private SerialNumberRepository serialNumberRepository;

    @Autowired
    private SerialNumberAssembler serialNumberAssembler;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private DeviceReservationService deviceReservationService;

    @Autowired
    private ProductCodeRepository productCodeRepository;

    @Autowired
    private EANRepository eanRepository;

    @Autowired
    private DeviceContextComponent deviceContextComponent;

    @Override
    public SerialNumber importSerialNoInPool(SerialNumberRequestDTO serialNumberRequestDTO) {
        LOG.debug("serialNumberRequestDTO={}", serialNumberRequestDTO);
        SerialNumber serialNumber =
            serialNumberRepository.findByNumberAndArticle(serialNumberRequestDTO.getSerialDTO().getNumber(),
                serialNumberRequestDTO.getSerialDTO().getArticleNumber());
        if (serialNumber != null) {
            throw new DeviceReservationException(ErrorCodes.DEVICE_ALREADY_EXIST);
        }

        validateDeviceContextInfo(serialNumberRequestDTO.getSerialDTO());

        Article article =
            getArticleForEanAndArticleId(serialNumberRequestDTO.getSerialDTO().getArticleNumber(),
                serialNumberRequestDTO.getSerialDTO().getTenantId());
        updateEans(article, serialNumberRequestDTO.getSerialDTO().getProductCodeDTOs());

        serialNumber = serialNumberAssembler.toSerialNumber(serialNumberRequestDTO, article, null);
        serialNumber = reserveOpencaseIfExist(serialNumber);
        return serialNumber;
    }

    private Article getArticleForEanAndArticleId(String articleNo, Long tenantId) {
        Optional<Article> article = null;
        article = articleRepository.findById(new ArticleId(articleNo, new TenantId(tenantId)));
        if (article.isPresent() && !article.get().getArticleEAN().isEmpty()) {
            LOG.info("Found existing article articleNo={}", articleNo);
            return article.get();
        }
        Article articleSync = articleService.syncBrodosArticlesByArticleNo(articleNo, tenantId, "DEU");
        LOG.debug("Article sync={} for articleNo={}", articleSync, articleNo);
        return articleSync;
    }

    private void updateEans(Article article, List<ProductCodeDTO> productCodeDTOs) {
        article.getArticleEAN().clear();
        for (ProductCodeDTO productCodeDTO : productCodeDTOs) {
            EAN ean =
                eanRepository.findByCodeAndArticleNumber(productCodeDTO.getValue(), article.getArticleId()
                    .getArticleNumber());
            if (ean == null) {
                ean = new EAN();
                ean.setCode(productCodeDTO.getValue());
                ean.setArticle(article);
                ean = productCodeRepository.save(ean);
            }
            article.getArticleEAN().add(ean);
        }
    }

    @Override
    public SerialNumber getSerialNumberById(Long id) {
        return serialNumberRepository.findById(id).orElseThrow(() -> new DeviceReservationException(ErrorCodes.DEVICE_NOT_FOUND, StringUtils.join("id=", id)));
    }

    @Override
    public SerialNumber updateSerialNo(Long id, SerialNumberRequestDTO serialNumberRequestDTO) {
        LOG.debug("serialNumberRequestDTO={}", serialNumberRequestDTO);
        SerialNumber serialNumber = serialNumberRepository.findById(id).orElseThrow(() -> new DeviceReservationException(ErrorCodes.DEVICE_NOT_FOUND, StringUtils.join("id=", id)));

        if (!serialNumber.getReservable()) {
            throw new DeviceReservationException(ErrorCodes.STATE_CHANGE_NOT_ALLOWED);
        }

        Article article
                = getArticleForEanAndArticleId(serialNumberRequestDTO.getSerialDTO().getArticleNumber(),
                        serialNumberRequestDTO.getSerialDTO().getTenantId());
        updateEans(article, serialNumberRequestDTO.getSerialDTO().getProductCodeDTOs());

        serialNumber = serialNumberAssembler.toSerialNumber(serialNumberRequestDTO, article, serialNumber);
        serialNumber = reserveOpencaseIfExist(serialNumber);
        return serialNumber;
    }

    private SerialNumber reserveOpencaseIfExist(SerialNumber serialNumber) {
        if (deviceReservationService.reserveOpencaseIfExist(serialNumber)) {
            LOG.info("Handled opencase with serialNo={} for warehousePool={}", serialNumber.getNumber(),
                serialNumber.getWarehouseNo());
        } else {
            serialNumber = serialNumberRepository.save(serialNumber);
            LOG.info("Imported serialNo={} into warehousePool={}", serialNumber.getNumber(),
                serialNumber.getWarehouseNo());
        }

        return serialNumber;
    }

    @Override
    public SerialNumber doSerialNumberAction(Long id, SerialNumberActionDTO serialNumberActionDTO) {
        switch (serialNumberActionDTO.getType()) {
            case "archive": {
                SerialNumber serialNumber = serialNumberRepository.findById(id).orElseThrow(() -> new DeviceReservationException(ErrorCodes.DEVICE_NOT_FOUND, StringUtils.join("id=", id)));

                if (!serialNumber.getReservable()) {
                    throw new DeviceReservationException(ErrorCodes.STATE_CHANGE_NOT_ALLOWED);
                }

                serialNumber.setArchived(Boolean.TRUE);
                serialNumber.setReservable(Boolean.FALSE);
                return serialNumberRepository.save(serialNumber);
            }

            case "unarchive": {
                SerialNumber serialNumber = serialNumberRepository.findById(id).orElseThrow(() -> new DeviceReservationException(ErrorCodes.DEVICE_NOT_FOUND, StringUtils.join("id=", id)));
                serialNumber.setArchived(Boolean.FALSE);
                serialNumber.setReservable(Boolean.TRUE);
                return reserveOpencaseIfExist(serialNumber);
            }

            default: {
                throw new DeviceReservationException(ErrorCodes.INVALID_ACTION_REQUESTED);
            }
        }
    }

    private void validateDeviceContextInfo(SerialDTO serialDTO) {
        try {
            JsonNode deviceContextJsonNode =
                deviceContextComponent.getDeviceContextInfoByArticleNoAndSerialNo(serialDTO.getArticleNumber(),
                    serialDTO.getNumber());
            if (deviceContextComponent.isDeviceInfoValid(deviceContextJsonNode)) {
                serialDTO.setEmbedded(deviceContextComponent.getExtractedDeviceInfo(deviceContextJsonNode));
            } else if (serialDTO.getWarehouseId() == 2) {
                serialDTO.setEmbedded(deviceContextComponent.createDeviceContextInfo(serialDTO.getNumber()));
            } else {
                throw new DeviceReservationException(ErrorCodes.DEVICE_CONTEXT_ERROR);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new DeviceReservationException(ErrorCodes.DEVICE_CONTEXT_ERROR);
        }
    }

    @Override
    public SerialNumber getSerialNumberByArticleNumberAndImei(String articleNo, String serialNo) {
        SerialNumber serialNumber = serialNumberRepository.findByNumberAndArticle(serialNo, articleNo);
        if (serialNumber == null) {
            throw new DeviceReservationException(ErrorCodes.DEVICE_NOT_FOUND, StringUtils.join("articlenumber=",
                articleNo, ", serialnumber=", serialNo));
        }

        return serialNumber;
    }
}
