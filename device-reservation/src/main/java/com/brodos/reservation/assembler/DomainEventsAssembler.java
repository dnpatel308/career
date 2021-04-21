/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.assembler;

import com.brodos.reservation.Constants;
import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.dto.request.DeviceReservationActionDTO;
import com.brodos.reservation.dto.request.DeviceReservationRequestDTO;
import com.brodos.reservation.entity.Customer;
import com.brodos.reservation.entity.DeviceReservationDomainevents;
import com.brodos.reservation.entity.DeviceReservationStatus;
import com.brodos.reservation.entity.SerialNumberImportTicketReference;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.SerialNumberReservationTicketReference;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.events.Cancelled;
import com.brodos.reservation.events.DomainEventAbstract;
import com.brodos.reservation.events.Imported;
import com.brodos.reservation.events.Pended;
import com.brodos.reservation.events.Opened;
import com.brodos.reservation.events.RequestedForSendout;
import com.brodos.reservation.events.Reserved;
import com.brodos.reservation.events.Sentout;
import com.brodos.reservation.exception.DeviceReservationException;
import com.brodos.reservation.infrastructure.DeviceReservationDomaineventsRepository;
import com.brodos.reservation.infrastructure.TicketReferenceRepository;
import com.brodos.reservation.utility.Utils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author padhaval
 */
public class DomainEventsAssembler {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DomainEventsAssembler.class);

    @Autowired
    TicketReferenceRepository ticketReferenceRepository;

    @Autowired
    ReservationReferencesAssembler reservationReferencesAssembler;

    @Autowired
    DeviceReservationDomaineventsRepository deviceReservationDomaineventsRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private void enrichDomainEvent(DomainEventAbstract domainEventAbstract) {
        DeviceReservationDomainevents openedDomainevent =
            deviceReservationDomaineventsRepository.findByReservationIdAndStatus(
                domainEventAbstract.getReservationId(), DeviceReservationStatus.OPEN.name());
        if (openedDomainevent != null) {
            Opened opened = (Opened) openedDomainevent.extractEventBodyObject();
            domainEventAbstract.setCancelIfUnavailable(opened.getCancelIfUnavailable());
        }

        DeviceReservationDomainevents pendedDomainevent =
            deviceReservationDomaineventsRepository.findByReservationIdAndStatus(
                domainEventAbstract.getReservationId(), DeviceReservationStatus.PENDING.name());
        if (pendedDomainevent != null) {
            Pended pended = (Pended) pendedDomainevent.extractEventBodyObject();
            domainEventAbstract.setPendedAt(pended.getPendedAt());
        }
    }

    private void enrichDomainEvent(SerialNumberReservation serialNumberReservation,
        DomainEventAbstract domainEventAbstract) {
        domainEventAbstract.setReservationId(serialNumberReservation.getId());
        domainEventAbstract.setGroup(serialNumberReservation.getSerialNumber().getWarehouseNo());

        if (serialNumberReservation.getOwner() != null) {
            domainEventAbstract.setCustomerno(((Customer) serialNumberReservation.getOwner()).getCustomerNumber());
        }

        domainEventAbstract.setArticleNo(serialNumberReservation.getSerialNumber().getArticle().getArticleId()
            .getArticleNumber());
        domainEventAbstract.setCreatedBy(Objects.nonNull(serialNumberReservation.getModifiedBy())
            ? serialNumberReservation.getModifiedBy() : serialNumberReservation.getReservedBy());

        if (serialNumberReservation.getSerialNumber() != null
            && serialNumberReservation.getSerialNumber().getEmbedded() != null) {
            domainEventAbstract.setDevice(serialNumberReservation.getSerialNumber().getEmbedded().get("device"));
        }

        domainEventAbstract.setReferences(reservationReferencesAssembler
            .toReservationReferences(serialNumberReservation.getReservationReferences()));

        domainEventAbstract.setEmail(serialNumberReservation.getTicketReference().getEmail());
        domainEventAbstract.setComment(serialNumberReservation.getTicketReference().getReservationComment());
        domainEventAbstract.setConsignment(serialNumberReservation.getConsignment());
    }

    private void enrichDomainEvent(SerialNumberImportTicketReference serialNumberImportTicketReference,
        DomainEventAbstract domainEventAbstract) {
        SerialNumberReservation serialNumberReservation =
            serialNumberImportTicketReference.getSerialNumberReservation();
        domainEventAbstract.setReservationId(serialNumberReservation.getId());

        if (serialNumberImportTicketReference.getOwner() != null) {
            domainEventAbstract.setCustomerno(((Customer) serialNumberImportTicketReference.getOwner())
                .getCustomerNumber());
        }

        domainEventAbstract.setReferences(reservationReferencesAssembler
            .toReservationReferences(serialNumberReservation.getReservationReferences()));

        domainEventAbstract.setGroup(serialNumberImportTicketReference.getWarehouseNo());
        domainEventAbstract.setArticleNo(serialNumberImportTicketReference.getArticle().getArticleId()
            .getArticleNumber());
        domainEventAbstract.setCreatedBy(serialNumberImportTicketReference.getModifiedBy());

        if (StringUtils.isBlank(domainEventAbstract.getCreatedBy())) {
            domainEventAbstract.setCreatedBy(serialNumberImportTicketReference.getCreatedBy());
        }

        domainEventAbstract.setPendedAt(serialNumberImportTicketReference.getCreatedDate());
        domainEventAbstract.setEmail(serialNumberImportTicketReference.getEmail());
        domainEventAbstract.setComment(serialNumberImportTicketReference.getReservationComment());
        domainEventAbstract.setConsignment(serialNumberReservation.getConsignment());
    }

    public DeviceReservationDomainevents toImportedEvent(SerialNumberReservation serialNumberReservation) {
        Imported imported = new Imported();
        enrichDomainEvent(serialNumberReservation, imported);
        return toDeviceReservationDomainevents(imported);
    }

    public DeviceReservationDomainevents toRequestedForSendoutEvent(SerialNumberReservation serialNumberReservation,
        DeviceReservationActionDTO deviceReservationActionDTO) {
        RequestedForSendout requestedForSendout = new RequestedForSendout();
        enrichDomainEvent(serialNumberReservation, requestedForSendout);
        requestedForSendout.setReservedAt(serialNumberReservation.getReservationTime());
        requestedForSendout.setRequestedForSendoutAt(serialNumberReservation.getModifiedDate());
        requestedForSendout.setDeliveryDetails(objectMapper.convertValue(deviceReservationActionDTO.getArguments(),
            JsonNode.class));
        return toDeviceReservationDomainevents(requestedForSendout);
    }

    public DeviceReservationDomainevents toSentoutEvent(SerialNumberReservation serialNumberReservation) {
        Sentout sentout = new Sentout();
        enrichDomainEvent(serialNumberReservation, sentout);
        sentout.setReservedAt(serialNumberReservation.getReservationTime());
        sentout.setSentoutAt(serialNumberReservation.getModifiedDate());
        return toDeviceReservationDomainevents(sentout);
    }

    public DeviceReservationDomainevents toCancelledEvent(TicketReference ticketReference) {
        Cancelled cancelled = new Cancelled();

        if (ticketReference instanceof SerialNumberReservationTicketReference) {
            enrichDomainEvent(ticketReference.getSerialNumberReservation(), cancelled);
        } else {
            SerialNumberImportTicketReference serialNumberImportTicketReference =
                (SerialNumberImportTicketReference) ticketReference;
            enrichDomainEvent(serialNumberImportTicketReference, cancelled);
        }

        cancelled.setComment(ticketReference.getCancellationComment());
        cancelled.setCancelledAt(ticketReference.getModifiedDate() != null ? ticketReference.getModifiedDate()
            : ticketReference.getCreatedDate());

        return toDeviceReservationDomainevents(cancelled);
    }

    public DeviceReservationDomainevents
        toOpenedEvent(long id, DeviceReservationRequestDTO deviceReservationRequestDTO) {
        Opened opened = new Opened();
        opened.setReservationId(id);
        opened.setGroup(Integer.valueOf(deviceReservationRequestDTO.getGroup()));
        opened.setCustomerno(deviceReservationRequestDTO.getCustomerNo());
        opened.setCreatedBy(Constants.USER_NAME);
        opened.setEmail(deviceReservationRequestDTO.getEmail());
        opened.setCustomerno(deviceReservationRequestDTO.getCustomerNo());
        opened.setComment(deviceReservationRequestDTO.getComment());
        opened.setCancelIfUnavailable(deviceReservationRequestDTO.isDeviceRequired());
        opened.setArticleNo(deviceReservationRequestDTO.getArticleNo());
        opened.setConsignment(deviceReservationRequestDTO.getConsignment());

        opened.setReferences(deviceReservationRequestDTO.getReferences());

        return toDeviceReservationDomainevents(opened);
    }

    public DeviceReservationDomainevents toReservedEvent(
        SerialNumberReservationTicketReference serialNumberReservationTicketReference) {
        Reserved reserved = new Reserved();
        enrichDomainEvent(serialNumberReservationTicketReference.getSerialNumberReservation(), reserved);
        reserved.setReservedAt(serialNumberReservationTicketReference.getSerialNumberReservation().getModifiedDate());

        return toDeviceReservationDomainevents(reserved);
    }

    public DeviceReservationDomainevents toPendingEvent(
        SerialNumberImportTicketReference serialNumberImportTicketReference) {

        Pended pending = new Pended();
        enrichDomainEvent(serialNumberImportTicketReference, pending);
        pending.setEmail(serialNumberImportTicketReference.getEmail());
        pending.setComment(serialNumberImportTicketReference.getReservationComment());
        pending.setPendedAt(serialNumberImportTicketReference.getCreatedDate());

        return toDeviceReservationDomainevents(pending);
    }

    public DeviceReservationDomainevents toDeviceReservationDomainevents(DomainEventAbstract domainEventAbstract) {
        try {
            enrichDomainEvent(domainEventAbstract);
            DeviceReservationDomainevents deviceReservationDomainevents = new DeviceReservationDomainevents();
            deviceReservationDomainevents.setTypeName(domainEventAbstract.getClass().getName());
            deviceReservationDomainevents.setStatus(domainEventAbstract.getStatus().toString());
            deviceReservationDomainevents.setReservationId(domainEventAbstract.getReservationId());
            deviceReservationDomainevents.setGroup(domainEventAbstract.getGroup());
            deviceReservationDomainevents.setEventBody(Utils.writeValueAsString(domainEventAbstract,
                JsonInclude.Include.USE_DEFAULTS));
            return deviceReservationDomainevents;
        } catch (JsonProcessingException ex) {
            LOG.error(ex.getMessage());
            throw new DeviceReservationException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
