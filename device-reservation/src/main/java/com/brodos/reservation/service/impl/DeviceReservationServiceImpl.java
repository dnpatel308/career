package com.brodos.reservation.service.impl;

import static com.brodos.reservation.Constants.CLIENT;
import static com.brodos.reservation.Constants.USER_NAME;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.assembler.ReservationReferencesAssembler;
import com.brodos.reservation.dto.request.DeviceReservationActionDTO;
import com.brodos.reservation.dto.request.DeviceReservationRequestDTO;
import com.brodos.reservation.entity.DeviceReservationStatus;
import com.brodos.reservation.entity.ReservationReference;
import com.brodos.reservation.entity.SerialNumber;
import com.brodos.reservation.entity.SerialNumberImportTicketReference;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.SerialNumberReservationTicketReference;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.entity.VoucherEventsStatus;
import com.brodos.reservation.events.SerialNumberSentoutEvent;
import com.brodos.reservation.exception.DeviceReservationException;
import com.brodos.reservation.infrastructure.SerialNumberReservationRepository;
import com.brodos.reservation.infrastructure.TicketReferenceRepository;
import com.brodos.reservation.service.DeviceReservationHelperService;
import com.brodos.reservation.service.DeviceReservationService;
import com.brodos.reservation.service.DomainEventsHelperService;

@Service
@Transactional
public class DeviceReservationServiceImpl implements DeviceReservationService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceReservationServiceImpl.class);

    @Autowired
    SerialNumberReservationRepository serialNumberReservationRepository;

    @Autowired
    DeviceReservationHelperService deviceReservationHelperService;

    @Autowired
    TicketReferenceRepository ticketReferenceRepository;

    @Autowired
    DomainEventsHelperService domainEventsHelperService;

    @Autowired
    ReservationReferencesAssembler reservationReferencesAssembler;

    @Override
    public SerialNumberReservation getDeviceReservationById(Long id) {
        LOG.info("Getting device reservation by reservationId={}", id);
        SerialNumberReservation serialNumberReservation = serialNumberReservationRepository.findById(id)
            .orElseThrow(() -> new DeviceReservationException(ErrorCodes.RESERVATION_NOT_FOUND));
        deviceReservationHelperService.enrichSerialNuberReservationDetail(serialNumberReservation);
        return serialNumberReservation;
    }

    @Override
    public SerialNumberReservation cancelDeviceReservation(Long id, String reason, boolean isArchive) {
        LOG.info("Cancelling device reservation by reservationId={} and archive serial={}", id, isArchive);
        SerialNumberReservation serialNumberReservation = serialNumberReservationRepository.findById(id)
            .orElseThrow(() -> new DeviceReservationException(ErrorCodes.RESERVATION_NOT_FOUND));
        if (isArchive) {
            if (serialNumberReservation.getSerialNumber() == null) {
                throw new DeviceReservationException(ErrorCodes.DEVICE_NOT_ASSOCIATED_WITH_RESERVATION);
            }
            serialNumberReservation.getSerialNumber().setArchived(isArchive);
        }
        TicketReference ticketReference =
            ticketReferenceRepository.findFirstBySerialNumberReservationOrderByIdDesc(serialNumberReservation);

        if (ticketReference instanceof SerialNumberImportTicketReference && StringUtils.isBlank(reason)) {
            throw new DeviceReservationException(ErrorCodes.CANCELLATION_REASON_MISSING);
        }

        if (serialNumberReservation.getStatus() != DeviceReservationStatus.PENDING
            && serialNumberReservation.getStatus() != DeviceReservationStatus.RESERVED
            && serialNumberReservation.getStatus() != DeviceReservationStatus.CANCELLED) {
            throw new DeviceReservationException(ErrorCodes.STATE_CHANGE_NOT_ALLOWED);
        }

        if (serialNumberReservation.getStatus() != DeviceReservationStatus.CANCELLED) {
            serialNumberReservation =
                deviceReservationHelperService.cancelDeviceReservation(serialNumberReservation, reason);
        }

        return serialNumberReservation;
    }

    @Override
    public SerialNumberReservation sentoutDeviceReservation(SerialNumberSentoutEvent serialNumberSentoutEvent) {
        SerialNumberReservation serialNumberReservation = serialNumberReservationRepository
            .findBySerialNumberAndReservedOrRequestForSentout(serialNumberSentoutEvent.getSerialNo(),
                serialNumberSentoutEvent.getArticleNo(), serialNumberSentoutEvent.getTenantId());

        if (serialNumberReservation == null) {
            throw new DeviceReservationException(
                ErrorCodes.RESERVATION_NOT_FOUND.putMetadata(VoucherEventsStatus.IGNORED.name(), Boolean.TRUE));
        }

        LOG.info("Sentout device, reservationId={}", serialNumberReservation.getId());

        TicketReference ticketReference =
            ticketReferenceRepository.findFirstBySerialNumberReservationOrderByIdDesc(serialNumberReservation);
        ticketReference.setModifiedBy(serialNumberSentoutEvent.getUser());

        if (serialNumberReservation.getStatus() != DeviceReservationStatus.RESERVED
            && serialNumberReservation.getStatus() != DeviceReservationStatus.REQUESTFORSENTOUT
            && serialNumberReservation.getStatus() != DeviceReservationStatus.SENTOUT) {
            throw new DeviceReservationException(ErrorCodes.STATE_CHANGE_NOT_ALLOWED);
        }

        if (serialNumberReservation.getStatus() != DeviceReservationStatus.SENTOUT) {
            serialNumberReservation = deviceReservationHelperService.sentoutDeviceReservation(ticketReference);
        }

        return serialNumberReservation;
    }

    @Override
    public SerialNumberReservation requestForSentoutDeviceReservation(Long id,
        DeviceReservationActionDTO deviceReservationActionDTO) {
        LOG.info("Request for sentout device reservation by reservationId={}, deviceReservationActionDTO={}", id,
            deviceReservationActionDTO);
        SerialNumberReservation serialNumberReservation = serialNumberReservationRepository.findById(id)
            .orElseThrow(() -> new DeviceReservationException(ErrorCodes.RESERVATION_NOT_FOUND));
        TicketReference ticketReference =
            ticketReferenceRepository.findFirstBySerialNumberReservationOrderByIdDesc(serialNumberReservation);

        if (serialNumberReservation.getStatus() != DeviceReservationStatus.RESERVED
            && serialNumberReservation.getStatus() != DeviceReservationStatus.REQUESTFORSENTOUT) {
            throw new DeviceReservationException(ErrorCodes.STATE_CHANGE_NOT_ALLOWED);
        }

        if (serialNumberReservation.getStatus() != DeviceReservationStatus.REQUESTFORSENTOUT) {
            serialNumberReservation = deviceReservationHelperService.requestForSentoutDeviceReservation(ticketReference,
                deviceReservationActionDTO);
        }

        return serialNumberReservation;
    }

    @Override
    public Page<TicketReference> getDeviceReservationBySearchCriteria(DeviceReservationStatus status, String articleNo,
        String customerNo, String reference, String imei1, int pageNo, int size) {
        LOG.info("Finding reservation by search criteria status={} and articleNo={} and customerNumber={}", status,
            articleNo, customerNo);
        Page<TicketReference> ticketReferences =
            ticketReferenceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
                ArrayList<Predicate> predicates = new ArrayList<>();
                if (status != null) {
                    LOG.debug("Preparing criteria for IMEIReservation status={}", status);
                    predicates.add(criteriaBuilder.equal(root.get("serialNumberReservation").get("status"), status));
                }
                if (customerNo != null) {
                    LOG.debug("Finding Owner for customerNumber={}", customerNo.trim());
                    if (status != DeviceReservationStatus.PENDING) {
                        predicates.add(criteriaBuilder.equal(
                            root.get("serialNumberReservation").get("owner").get("customerNo"), customerNo.trim()));
                    } else {
                        predicates.add(criteriaBuilder.equal(root.get("owner").get("customerNo"), customerNo.trim()));
                    }
                }
                if (articleNo != null) {
                    LOG.debug("Preparing criteria for IMEIReservation articleNumber={}", articleNo.trim());
                    if (status != DeviceReservationStatus.PENDING) {
                        predicates.add(criteriaBuilder.equal(root.get("serialNumberReservation").get("serialNumber")
                            .get("article").get("articleId").get("articleNumber"), articleNo.trim()));
                    } else {
                        predicates.add(criteriaBuilder.equal(root.get("article").get("articleId").get("articleNumber"),
                            articleNo.trim()));
                    }
                }
                if (reference != null) {
                    LOG.debug("Preparing criteria for IMEIReservation reference={}", reference.trim());
                    Join<SerialNumberReservation, ReservationReference> join = root
                        .join("serialNumberReservation", JoinType.LEFT).join("reservationReferences", JoinType.LEFT);
                    predicates.add(criteriaBuilder.equal(join.get("value"), reference.trim()));
                }
                if (imei1 != null) {
                    LOG.debug("Preparing criteria for IMEIReservation imei1={}", imei1.trim());
                    predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("serialNumberReservation").get("serialNumber").get("number"),
                            imei1.trim()),
                        criteriaBuilder.equal(root.type(),
                            criteriaBuilder.literal(SerialNumberReservationTicketReference.class))));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }, PageRequest.of(pageNo, size, Direction.DESC, "id"));

        for (TicketReference ticketReference : ticketReferences) {
            if (ticketReference.getSerialNumberReservation() != null) {
                deviceReservationHelperService
                    .enrichSerialNuberReservationDetail(ticketReference.getSerialNumberReservation());
            }
        }

        return ticketReferences;
    }

    @Override
    public SerialNumberReservation doDeviceReservationAction(Long id,
        DeviceReservationActionDTO deviceReservationActionDTO) {
        switch (deviceReservationActionDTO.getType().toLowerCase()) {
            case "request-sendout": {
                return requestForSentoutDeviceReservation(id, deviceReservationActionDTO);
            }

            case "cancel": {
                String reason = deviceReservationActionDTO.getArguments() != null
                    && deviceReservationActionDTO.getArguments().getReason() != null
                        ? deviceReservationActionDTO.getArguments().getReason() : "";
                return cancelDeviceReservation(id, reason, false);
            }

            case "cancel-and-archive": {
                String reason = deviceReservationActionDTO.getArguments() != null
                    && deviceReservationActionDTO.getArguments().getReason() != null
                        ? deviceReservationActionDTO.getArguments().getReason() : "";
                return cancelDeviceReservation(id, reason, true);
            }

            default: {
                throw new DeviceReservationException(ErrorCodes.INVALID_ACTION_REQUESTED);
            }
        }
    }

    @Override
    public SerialNumberReservation createDeviceReservationRequest(
        DeviceReservationRequestDTO deviceReservationRequestDTO, Long bulkReservationId, String requestId) {
        LOG.info("Creating device reservation request={}, bulkReservationId={}, requestId={}",
            deviceReservationRequestDTO, bulkReservationId, requestId);
        SerialNumberReservation serialNumberReservation = null;
        if (!StringUtils.isBlank(requestId)) {
            serialNumberReservation = serialNumberReservationRepository.findByRequestId(requestId);
            if (serialNumberReservation != null) {
                LOG.info("Found device reservation for requestId={}, {}", requestId, serialNumberReservation.getId());
                deviceReservationHelperService.enrichSerialNuberReservationDetail(serialNumberReservation);
                return serialNumberReservation;
            }
        }

        if (serialNumberReservation == null) {
            serialNumberReservation = new SerialNumberReservation();
        }

        serialNumberReservation.setClient(CLIENT);
        deviceReservationHelperService.updateCustomerDetail(serialNumberReservation,
            deviceReservationRequestDTO.getCustomerNo());
        serialNumberReservation.setStatus(DeviceReservationStatus.OPEN);
        serialNumberReservation.setReservedBy(USER_NAME);
        serialNumberReservation.setReservationReferences(reservationReferencesAssembler
            .toReservationReferences(serialNumberReservation, deviceReservationRequestDTO.getReferences()));
        serialNumberReservation.setBulkReservationId(bulkReservationId);
        serialNumberReservation.setConsignment(deviceReservationRequestDTO.getConsignment());
        serialNumberReservation.setRequestId(requestId);
        serialNumberReservation = serialNumberReservationRepository.save(serialNumberReservation);
        domainEventsHelperService.createAndStoreOpenedEvent(serialNumberReservation, deviceReservationRequestDTO);
        return serialNumberReservation;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    synchronized public boolean reserveOpencaseIfExist(SerialNumber serialNumber) {
        LOG.info("Handing open case for serial number if exists={}", serialNumber.getNumber());
        return deviceReservationHelperService.reserveOpencaseIfExist(serialNumber);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    synchronized public List<SerialNumberReservation>
        createDeviceReservationRequests(List<DeviceReservationRequestDTO> deviceReservationRequestDTOs) {
        LOG.info("Processing {} bulk requests", deviceReservationRequestDTOs.size());
        LOG.debug("Creating device reservation requests={}", deviceReservationRequestDTOs);
        Long bulkReservationId = deviceReservationHelperService.getIncrementalBulkReservationId();

        List<SerialNumberReservation> serialNumberReservations = new ArrayList<>();
        for (DeviceReservationRequestDTO deviceReservationRequestDTO : deviceReservationRequestDTOs) {
            serialNumberReservations
                .add(createDeviceReservationRequest(deviceReservationRequestDTO, bulkReservationId, null));
        }

        return serialNumberReservations;
    }

    @Override
    public List<SerialNumberReservation> getDeviceReservationByBulkReservationId(Long bulkid) {
        List<SerialNumberReservation> serialNumberReservations =
            serialNumberReservationRepository.findByBulkReservationId(bulkid);

        if (serialNumberReservations == null || serialNumberReservations.isEmpty()) {
            throw new DeviceReservationException(ErrorCodes.BULK_RESERVATION_NOT_FOUND);
        }

        for (SerialNumberReservation serialNumberReservation : serialNumberReservations) {
            deviceReservationHelperService.enrichSerialNuberReservationDetail(serialNumberReservation);
        }

        return serialNumberReservations;
    }
}
