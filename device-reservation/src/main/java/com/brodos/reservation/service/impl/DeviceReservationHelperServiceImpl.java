/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service.impl;

import static com.brodos.reservation.Constants.BULK_RESERVATION_ID_INCREMENTAL_PROPERTY_NAME;
import static com.brodos.reservation.Constants.TENANT_ID;
import static com.brodos.reservation.Constants.USER_NAME;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.component.DeviceContextComponent;
import com.brodos.reservation.dto.request.DeviceReservationActionDTO;
import com.brodos.reservation.entity.ArticleId;
import com.brodos.reservation.entity.Customer;
import com.brodos.reservation.entity.DeviceReservationDomainevents;
import com.brodos.reservation.entity.DeviceReservationStatus;
import com.brodos.reservation.entity.IncrementalProperty;
import com.brodos.reservation.entity.SerialNumber;
import com.brodos.reservation.entity.SerialNumberImportTicketReference;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.SerialNumberReservationTicketReference;
import com.brodos.reservation.entity.TenantId;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.entity.TicketStatus;
import com.brodos.reservation.events.Imported;
import com.brodos.reservation.events.Opened;
import com.brodos.reservation.exception.DeviceReservationException;
import com.brodos.reservation.infrastructure.ArticleRepository;
import com.brodos.reservation.infrastructure.CustomerRepository;
import com.brodos.reservation.infrastructure.DeviceReservationDomaineventsRepository;
import com.brodos.reservation.infrastructure.IncrementalPropertyRepository;
import com.brodos.reservation.infrastructure.SerialNumberImportTicketReferenceRepository;
import com.brodos.reservation.infrastructure.SerialNumberReservationRepository;
import com.brodos.reservation.infrastructure.SerialNumberReservationTicketReferenceRepository;
import com.brodos.reservation.infrastructure.TicketReferenceRepository;
import com.brodos.reservation.service.DeviceReservationHelperService;
import com.brodos.reservation.service.DomainEventsHelperService;
import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author padhaval
 */
@Service
public class DeviceReservationHelperServiceImpl implements DeviceReservationHelperService {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DeviceReservationHelperServiceImpl.class);

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    SerialNumberReservationRepository serialNumberReservationRepository;

    @Autowired
    SerialNumberReservationTicketReferenceRepository serialNumberReservationTicketReferenceRepository;

    @Autowired
    SerialNumberImportTicketReferenceRepository serialNumberImportTicketReferenceRepository;

    @Autowired
    DeviceContextComponent deviceContextComponent;

    @Autowired
    TicketReferenceRepository ticketReferenceRepository;

    @Autowired
    DomainEventsHelperService domainEventsHelperService;

    @Autowired
    DeviceReservationDomaineventsRepository deviceReservationDomaineventsRepository;

    @Autowired
    IncrementalPropertyRepository incrementalPropertyRepository;

    @Override
    public void updateCustomerDetail(SerialNumberReservation serialNumberReservation, String customerNo) {
        if (customerNo != null) {
            Customer customer = customerRepository.findByCustomerNumber(customerNo);
            if (customer == null) {
                customer = new Customer();
                customer.setCustomerNumber(customerNo);
                customer.setCreatedBy(USER_NAME);
                customer.setCreatedDate(new Date());
                customer = customerRepository.save(customer);
            }

            serialNumberReservation.setOwner(customer);
        }
    }

    private void checkAndUpdateWarehouseNo(SerialNumber serialNumber, Integer group) {
        if (serialNumber.getWarehouseNo() == null) {
            serialNumber.setWarehouseNo(group);
        }
    }

    @Override
    public SerialNumberReservation handleOpenedEvent(Opened openedEvent, SerialNumber serialNumber) {
        SerialNumberReservation serialNumberReservation =
            serialNumberReservationRepository.findById(openedEvent.getReservationId()).get();

        serialNumberReservation.setModifiedDate(new Date());

        if (serialNumber != null) {
            reserveDevice(openedEvent, serialNumber, serialNumberReservation);
        } else {
            checkAndCreatePendingOrCancelledReservation(openedEvent, serialNumberReservation);
        }

        return serialNumberReservation;
    }

    private void reserveDevice(Opened openedEvent, SerialNumber serialNumber,
        SerialNumberReservation serialNumberReservation) {
        checkAndUpdateWarehouseNo(serialNumber, openedEvent.getGroup());
        serialNumber.setReservable(Boolean.FALSE);
        String user = serialNumber.getModifiedBy() != null ? serialNumber.getModifiedBy() : serialNumber.getCreatedBy();
        serialNumberReservation.setModifiedBy(user);
        serialNumberReservation.setSerialNumber(serialNumber);
        serialNumberReservation.setStatus(DeviceReservationStatus.RESERVED);
        TicketReference ticketReference =
            createSerialNumberReservationTicketReferenceRecord(openedEvent, serialNumberReservation);
        domainEventsHelperService.createAndStoreReservedEvent((SerialNumberReservationTicketReference) ticketReference);
    }

    private void checkAndCreatePendingOrCancelledReservation(Opened openedEvent,
        SerialNumberReservation serialNumberReservation) {
        serialNumberReservation.setModifiedBy(USER_NAME);
        if (openedEvent.getCancelIfUnavailable()) {
            serialNumberReservation.setStatus(DeviceReservationStatus.CANCELLED);
            TicketReference ticketReference =
                createSerialNumberImportTicketReferenceRecord(openedEvent, serialNumberReservation);
            domainEventsHelperService.createAndStoreCancelledEvent(ticketReference);
        } else {
            serialNumberReservation.setStatus(DeviceReservationStatus.PENDING);
            TicketReference ticketReference =
                createSerialNumberImportTicketReferenceRecord(openedEvent, serialNumberReservation);
            domainEventsHelperService.createAndStorePendingEvent((SerialNumberImportTicketReference) ticketReference);
        }
    }

    private TicketReference createSerialNumberReservationTicketReferenceRecord(Opened openedEvent,
        SerialNumberReservation serialNumberReservation) {
        SerialNumberReservationTicketReference serialNumberReservationTicketReference =
            new SerialNumberReservationTicketReference();
        serialNumberReservationTicketReference.setSerialNumberReservation(serialNumberReservation);
        updateTicketReference(openedEvent, serialNumberReservationTicketReference);
        serialNumberReservationTicketReference.setTenantId(new TenantId(TENANT_ID));
        return serialNumberReservationTicketReferenceRepository.save(serialNumberReservationTicketReference);
    }

    private TicketReference createSerialNumberImportTicketReferenceRecord(Opened openedEvent,
        SerialNumberReservation serialNumberReservation) {
        SerialNumberImportTicketReference serialNumberImportTicketReference =
            new SerialNumberImportTicketReference(serialNumberReservation.getOwner(),
                articleRepository.findById(new ArticleId(openedEvent.getArticleNo(), new TenantId(TENANT_ID)))
                    .orElseThrow(() -> new DeviceReservationException(ErrorCodes.ARTICLE_NOT_FOUND)));
        serialNumberImportTicketReference.setOwner(serialNumberReservation.getOwner());
        serialNumberImportTicketReference.setWarehouseNo(openedEvent.getGroup());
        serialNumberImportTicketReference.setSerialNumberReservation(serialNumberReservation);
        serialNumberImportTicketReference.setTenantId(new TenantId(TENANT_ID));
        updateTicketReference(openedEvent, serialNumberImportTicketReference);
        return serialNumberImportTicketReferenceRepository.save(serialNumberImportTicketReference);
    }

    private TicketReference updateTicketReference(Opened openedEvent, TicketReference ticketReference) {
        ticketReference.setReservationComment(openedEvent.getComment());
        ticketReference.setEmail(openedEvent.getEmail());
        ticketReference.setCreatedBy(USER_NAME);
        ticketReference.setCreatedDate(new Date());
        ticketReference.setWarehouseNo(openedEvent.getGroup());

        if (ticketReference.getSerialNumberReservation().getStatus() == DeviceReservationStatus.CANCELLED) {
            ticketReference.setStatus(TicketStatus.CANCELLED);
        } else {
            ticketReference.setStatus(TicketStatus.PENDING);
        }

        return ticketReference;
    }

    @Override
    public SerialNumber findSerialNumberWithValidDeviceContextInfo(List<SerialNumber> serialNumbers) {
        if (!serialNumbers.isEmpty()) {
            for (SerialNumber serialNumber : serialNumbers) {
                if (fetchAndSetDeviceContextInfo(serialNumber)) {
                    return serialNumber;
                }
            }
        }

        return null;
    }

    private boolean fetchAndSetDeviceContextInfo(SerialNumber serialNumber) {
        try {
            JsonNode deviceContextJsonNode =
                deviceContextComponent.getDeviceContextInfoByArticleNoAndSerialNo(serialNumber.getArticle()
                    .getArticleId().getArticleNumber(), serialNumber.getNumber());
            if (deviceContextComponent.isDeviceInfoValid(deviceContextJsonNode)) {
                // to save api call
                serialNumber.setEmbedded(deviceContextComponent.getExtractedDeviceInfo(deviceContextJsonNode));
                return true;
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new DeviceReservationException(ErrorCodes.DEVICE_CONTEXT_ERROR);
        }

        return false;
    }

    @Override
    public SerialNumberReservation requestForSentoutDeviceReservation(TicketReference ticketReference,
        DeviceReservationActionDTO deviceReservationActionDTO) {
        SerialNumberReservation serialNumberReservation = ticketReference.getSerialNumberReservation();
        serialNumberReservation.setStatus(DeviceReservationStatus.REQUESTFORSENTOUT);
        serialNumberReservation.setSentoutRequestedBy(USER_NAME);
        serialNumberReservation.setSentoutRequestedTime(new Date());
        serialNumberReservation.setModifiedBy(USER_NAME);
        serialNumberReservation.setModifiedDate(new Date());
        serialNumberReservation = serialNumberReservationRepository.save(serialNumberReservation);
        domainEventsHelperService.createAndStoreRequestForSentoutEvent(serialNumberReservation,
            deviceReservationActionDTO);
        return serialNumberReservation;
    }

    @Override
    public SerialNumberReservation sentoutDeviceReservation(TicketReference ticketReference) {
        SerialNumberReservation serialNumberReservation = ticketReference.getSerialNumberReservation();
        serialNumberReservation.setStatus(DeviceReservationStatus.SENTOUT);
        serialNumberReservation.setSentBy(ticketReference.getModifiedBy());
        serialNumberReservation.setSentOutTime(new Date());
        serialNumberReservation.setModifiedBy(ticketReference.getModifiedBy());
        serialNumberReservation.setModifiedDate(new Date());
        serialNumberReservation = serialNumberReservationRepository.save(serialNumberReservation);
        domainEventsHelperService.createAndStoreSentoutEvent(serialNumberReservation);
        return serialNumberReservation;
    }

    @Override
    public SerialNumberReservation cancelDeviceReservation(SerialNumberReservation serialNumberReservation,
        String reason) {
        serialNumberReservation.setStatus(DeviceReservationStatus.CANCELLED);
        if (serialNumberReservation.getSerialNumber() != null) {
            serialNumberReservation.getSerialNumber().setReservable(Boolean.TRUE);
        }
        serialNumberReservation.setCancelledBy(USER_NAME);
        serialNumberReservation.setCancellationTime(new Date());
        serialNumberReservation.setModifiedBy(USER_NAME);
        serialNumberReservation.setModifiedDate(new Date());
        serialNumberReservation = serialNumberReservationRepository.save(serialNumberReservation);
        TicketReference ticketReference = serialNumberReservation.getTicketReference();
        ticketReference.setCancellationComment(reason);
        domainEventsHelperService.createAndStoreCancelledEvent(ticketReference);
        return serialNumberReservation;
    }

    @Override
    public boolean reserveOpencaseIfExist(SerialNumber serialNumber) {
        if (!serialNumber.getReservable()) {
            LOG.debug("serialNumber={} is not reservable", serialNumber.getNumber());
            throw new DeviceReservationException(ErrorCodes.DEVICE_ALREADY_RESERVERD);
        }

        SerialNumberImportTicketReference serialNumberImportTicketReference =
            ticketReferenceRepository.findFirstPendingDeviceReservation(serialNumber.getArticle().getArticleId()
                .getArticleNumber(), serialNumber.getWarehouseNo());

        if (serialNumberImportTicketReference == null) {
            LOG.info("no open case found for serialNumber={}", serialNumber.getNumber());
            return false;
        }

        try {
            LOG.info("Handing open case for serial number={}, serialNumberImportTicketReference={}",
                serialNumber.getNumber(), serialNumberImportTicketReference.getId());
            String user =
                serialNumber.getModifiedBy() != null ? serialNumber.getModifiedBy() : serialNumber.getCreatedBy();

            if (serialNumberImportTicketReference.getSerialNumberReservation() == null) {
                SerialNumberReservation serialNumberReservation = new SerialNumberReservation();
                serialNumberReservation.setReservedBy(user);
                serialNumberReservation.setReservationTime(new Date());
                serialNumberImportTicketReference.setSerialNumberReservation(serialNumberReservation);
            } else {
                serialNumberImportTicketReference.getSerialNumberReservation().setModifiedBy(user);
                serialNumberImportTicketReference.getSerialNumberReservation().setModifiedDate(new Date());
            }

            serialNumberImportTicketReference.getSerialNumberReservation().setStatus(DeviceReservationStatus.RESERVED);
            serialNumberImportTicketReference.getSerialNumberReservation().setSerialNumber(serialNumber);
            serialNumberImportTicketReference.getSerialNumberReservation().setTicketNo(
                serialNumberImportTicketReference.getTicketNumber());
            serialNumber.setTicketNumber(serialNumberImportTicketReference.getTicketNumber());
            serialNumber.setReservable(Boolean.FALSE);

            serialNumberImportTicketReference.setModifiedBy(user);
            serialNumberImportTicketReference.setModifiedDate(new Date());

            serialNumberReservationRepository.save(serialNumberImportTicketReference.getSerialNumberReservation());

            domainEventsHelperService.createAndStoreImportedEvent(serialNumberImportTicketReference
                .getSerialNumberReservation());

            serialNumber
                .setId(serialNumberImportTicketReference.getSerialNumberReservation().getSerialNumber().getId());
            return true;
        } catch (Exception exception) {
            LOG.error(exception.getMessage());
            LOG.trace(exception.getMessage(), exception);
            throw new DeviceReservationException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void enrichSerialNuberReservationDetail(SerialNumberReservation serialNumberReservation) {
        serialNumberReservation.setTicketReference(ticketReferenceRepository
            .findFirstBySerialNumberReservationOrderByIdDesc(serialNumberReservation));
        if (serialNumberReservation.getTicketReference() == null) {
            DeviceReservationDomainevents deviceReservationDomainevents =
                deviceReservationDomaineventsRepository.findByReservationId(serialNumberReservation.getId());
            if (deviceReservationDomainevents != null) {
                serialNumberReservation.setDomainEvent((Opened) deviceReservationDomainevents.extractEventBodyObject());
            }
        }
    }

    private void fetchAndSetDeviceContextInfoOrCreateAndSetDummy(SerialNumber serialNumber) {
        try {
            JsonNode deviceContextJsonNode =
                deviceContextComponent.getDeviceContextInfoByArticleNoAndSerialNo(serialNumber.getArticle()
                    .getArticleId().getArticleNumber(), serialNumber.getNumber());
            if (deviceContextComponent.isDeviceInfoValid(deviceContextJsonNode)) {
                serialNumber.setEmbedded(deviceContextComponent.getExtractedDeviceInfo(deviceContextJsonNode));
            } else {
                serialNumber.setEmbedded(deviceContextComponent.createDeviceContextInfo(serialNumber.getNumber()));
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            serialNumber.setEmbedded(deviceContextComponent.createDeviceContextInfo(serialNumber.getNumber()));
        }
    }

    @Override
    public boolean handleImportedEvent(Imported importedEvent) {
        try {
            SerialNumberReservation serialNumberReservation =
                serialNumberReservationRepository.findById(importedEvent.getReservationId()).get();
            SerialNumberImportTicketReference serialNumberImportTicketReference =
                (SerialNumberImportTicketReference) ticketReferenceRepository
                    .findFirstBySerialNumberReservationOrderByIdDesc(serialNumberReservation);

            SerialNumberReservationTicketReference serialNumberReservationTicketReference =
                new SerialNumberReservationTicketReference();
            serialNumberReservationTicketReference.setTenantId(serialNumberImportTicketReference.getTenantId());
            serialNumberReservationTicketReference.setSerialNumberReservation(serialNumberImportTicketReference
                .getSerialNumberReservation());
            serialNumberReservationTicketReference.setReservationComment(serialNumberImportTicketReference
                .getReservationComment());
            serialNumberReservationTicketReference.setEmail(serialNumberImportTicketReference.getEmail());
            serialNumberReservationTicketReference.setCreatedBy(serialNumberImportTicketReference.getModifiedBy());
            serialNumberReservationTicketReference.setCreatedDate(new Date());
            serialNumberReservationTicketReference.setStatus(TicketStatus.PENDING);
            serialNumberReservationTicketReference.setWarehouseNo(serialNumberImportTicketReference.getWarehouseNo());
            serialNumberReservationTicketReference.setOwner(serialNumberImportTicketReference.getOwner());
            serialNumberReservationTicketReference.getSerialNumberReservation().setOwner(
                serialNumberImportTicketReference.getOwner());
            serialNumberReservationTicketReferenceRepository.save(serialNumberReservationTicketReference);

            SerialNumber serialNumber =
                serialNumberReservationTicketReference.getSerialNumberReservation().getSerialNumber();
            fetchAndSetDeviceContextInfoOrCreateAndSetDummy(serialNumber);

            domainEventsHelperService.createAndStoreReservedEvent(serialNumberReservationTicketReference);
            return true;
        } catch (Exception exception) {
            LOG.error(exception.getMessage());
            LOG.trace(exception.getMessage(), exception);
            throw new DeviceReservationException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Long getIncrementalBulkReservationId() {
        IncrementalProperty incrementalProperty =
            incrementalPropertyRepository.findByNameAndTenantId(BULK_RESERVATION_ID_INCREMENTAL_PROPERTY_NAME,
                TENANT_ID);
        incrementalProperty.updateIncrementalValue();
        incrementalProperty = incrementalPropertyRepository.save(incrementalProperty);
        return incrementalProperty.getValue();
    }
}
