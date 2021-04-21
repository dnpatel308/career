package com.brodos.reservation.service.impl;

import com.brodos.reservation.Constants;
import com.brodos.reservation.component.DeviceContextComponent;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brodos.reservation.component.TicketComponent;
import com.brodos.reservation.entity.OpenCaseStatus;
import com.brodos.reservation.entity.Owner;
import com.brodos.reservation.entity.SerialNumberImportTicketReference;
import com.brodos.reservation.entity.SerialNumberReservationTicketReference;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.entity.TicketStatus;
import com.brodos.reservation.events.Cancelled;
import com.brodos.reservation.events.DomainEventAbstract;
import com.brodos.reservation.events.Imported;
import com.brodos.reservation.events.Pended;
import com.brodos.reservation.events.Reserved;
import com.brodos.reservation.events.Sentout;
import com.brodos.reservation.infrastructure.ConfigurationRepository;
import com.brodos.reservation.infrastructure.OwnerRepository;
import com.brodos.reservation.infrastructure.TicketReferenceRepository;
import com.brodos.reservation.service.TicketHelperService;
import com.brodos.reservation.service.TicketService;
import com.brodos.ticket.domain.dto.TicketRequestDTO;
import com.brodos.ticket.domain.exception.TicketException;
import com.brodos.ticket.domain.model.Ticket;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketServiceImpl.class);

    @Autowired
    TicketComponent ticketComponent;

    @Autowired
    TicketHelperService ticketHelperService;

    @Autowired
    TicketReferenceRepository ticketReferenceRepository;

    @Autowired
    DeviceContextComponent deviceContextComponent;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    OwnerRepository ownerRepository;

    @Override
    public void createSerialNumberReservationTicketAndUpdate(Reserved reservedEvent) throws Exception {
        TicketRequestDTO ticketRequestDTO =
            ticketHelperService.prepareTicketDTO(
                ticketComponent.geEnvironment().getProperty("ticket.reservation.responsibility"), Integer
                    .valueOf(ticketComponent.geEnvironment().getProperty("ticket.sendmail")), Constants.TENANT_ID,
                reservedEvent.getCustomerno(), ticketComponent.geEnvironment()
                    .getProperty("ticket.reservation.subject"), ticketHelperService.getTicketXMLContentForReservation(
                    reservedEvent.getArticleNo(), reservedEvent.getEmail(), reservedEvent.getComment(),
                    reservedEvent.getCreatedBy(),
                    deviceContextComponent.getImei1FromDeviceInfo(reservedEvent.getDevice())));
        createTicketAndUpdateTicketReference(ticketRequestDTO, reservedEvent.getTicketReference());
    }

    @Override
    public void createSerialNumberUnavailableTicketAndUpdate(Pended pendingEvent) throws Exception {
        LOG.info("Creating ticket for Pending event, reservationId={}", pendingEvent.getReservationId());
        TicketRequestDTO ticketRequestDTO =
            ticketHelperService.prepareTicketDTO(
                ticketComponent.geEnvironment().getProperty("ticket.reservation.responsibility.warehouse"), Integer
                    .valueOf(ticketComponent.geEnvironment().getProperty("ticket.sendmail.warehouse")),
                Constants.TENANT_ID, pendingEvent.getCustomerno(),
                ticketComponent.geEnvironment().getProperty("ticket.opencase.subject"), ticketHelperService
                    .getTicketXMLContentForOpenCase(pendingEvent.getGroup(), pendingEvent.getArticleNo(),
                        pendingEvent.getEmail(), pendingEvent.getCreatedBy()));
        createTicketAndUpdateTicketReference(ticketRequestDTO, pendingEvent.getTicketReference());
        updateReservationCount(pendingEvent.getTicketReference());
    }

    private void
        createTicketAndUpdateTicketReference(TicketRequestDTO ticketRequestDTO, TicketReference ticketReference)
            throws TicketException {
        Ticket ticket = ticketComponent.getTicketService().createTicket(ticketRequestDTO);
        updateTicketReference(ticketReference, ticket);
    }

    @Override
    public void updateSerialNumberReservationTicket(Sentout sentoutEvent) throws Exception {
        SerialNumberReservationTicketReference serialNumberReservationTicketReference =
            (SerialNumberReservationTicketReference) sentoutEvent.getTicketReference();
        serialNumberReservationTicketReference.setModifiedBy(sentoutEvent.getCreatedBy());
        String user = sentoutEvent.getCreatedBy();
        LOG.info("Updating Reservation Ticket, ticketNo={}", serialNumberReservationTicketReference.getTicketNumber());
        String comment =
            ticketComponent.geEnvironment().getProperty("ticket.imei.sentout.comment") + " Sent By : " + user;

        updateTicketAndTicketReference(sentoutEvent, comment, serialNumberReservationTicketReference);
    }

    @Override
    public void updateSerialNumberReservationTicket(Cancelled cancelledEvent) throws Exception {
        SerialNumberReservationTicketReference serialNumberReservationTicketReference =
            (SerialNumberReservationTicketReference) cancelledEvent.getTicketReference();
        serialNumberReservationTicketReference.setModifiedBy(cancelledEvent.getCreatedBy());
        String user = cancelledEvent.getCreatedBy();
        LOG.info("Updating Reservation Ticket, ticketNo={}", serialNumberReservationTicketReference.getTicketNumber());
        String comment =
            ticketComponent.geEnvironment().getProperty("ticket.imei.cancel.comment") + "." + " Cancelled By : " + user;

        updateTicketAndTicketReference(cancelledEvent, comment, serialNumberReservationTicketReference);
    }

    private void updateTicketAndTicketReference(DomainEventAbstract domainEventAbstract, String comment,
        TicketReference ticketReference) throws TicketException {
        TicketRequestDTO ticketRequestDTO =
            ticketHelperService.getTicketDTOForUpdateRequest(
                domainEventAbstract.getTicketReference().getTicketNumber(), ticketComponent.geEnvironment()
                    .getProperty("ticket.reservation.responsibility"), comment);
        ticketComponent.getTicketService().updateTicket(ticketRequestDTO);
        updateTicketReference(ticketReference);
        updateReservationCount(ticketReference);
    }

    @Override
    public void updateSerialNumberImportIMEITicket(Imported importedEvent) throws Exception {
        SerialNumberImportTicketReference serialNumberImportTicketReference =
            (SerialNumberImportTicketReference) importedEvent.getTicketReference();
        serialNumberImportTicketReference.setModifiedBy(importedEvent.getCreatedBy());
        String user = importedEvent.getCreatedBy();
        serialNumberImportTicketReference.setOpenCaseStatus(OpenCaseStatus.IMPORTED);
        LOG.info("Updating serialNumberTicketReference to IMPORTED, id={} ", serialNumberImportTicketReference.getId());
        updateSerialNumberImportIMEITicket(importedEvent, user, null);
    }

    @Override
    public void updateSerialNumberImportIMEITicket(Cancelled cancelledEvent) throws Exception {
        SerialNumberImportTicketReference serialNumberImportTicketReference =
            (SerialNumberImportTicketReference) cancelledEvent.getTicketReference();
        serialNumberImportTicketReference.setModifiedBy(cancelledEvent.getCreatedBy());
        String user = cancelledEvent.getCreatedBy();
        String comment = cancelledEvent.getComment();
        serialNumberImportTicketReference.setOpenCaseStatus(OpenCaseStatus.CANCELLED);
        LOG.info("Updating serialNumberTicketReference to CANCELLED,id={} ", serialNumberImportTicketReference.getId());
        updateSerialNumberImportIMEITicket(cancelledEvent, user, comment);
    }

    private void
        updateSerialNumberImportIMEITicket(DomainEventAbstract domainEventAbstract, String user, String comment)
            throws Exception {
        if (!StringUtils.isBlank(domainEventAbstract.getTicketReference().getTicketNumber())) {
            String updateTicketComment =
                ticketHelperService.getCommentForSerialNumberImportTicketUpdate(
                    (SerialNumberImportTicketReference) domainEventAbstract.getTicketReference(), user, comment);
            TicketRequestDTO ticketRequestDTO =
                ticketHelperService.getTicketDTOForUpdateRequest(domainEventAbstract.getTicketReference()
                    .getTicketNumber(),
                    ticketComponent.geEnvironment().getProperty("ticket.reservation.responsibility.warehouse"),
                    updateTicketComment);
            ticketComponent.getTicketService().updateTicket(ticketRequestDTO);
        }

        updateTicketReference(domainEventAbstract.getTicketReference());
        updateReservationCount(domainEventAbstract.getTicketReference());
    }

    private void updateTicketReference(TicketReference ticketReference, Ticket ticket) {
        ticketReference.setTicketNumber(ticket.getTicketNumber());
        ticketReference.setStatus(TicketStatus.OPEN);
        ticketReference.setModifiedDate(new Date());
        ticketReferenceRepository.save(ticketReference);
    }

    private void updateTicketReference(TicketReference ticketReference) {
        if (ticketReference.getStatus() == TicketStatus.FAILED) {
            return;
        }

        ticketReference.setStatus(TicketStatus.CLOSED);
        ticketReference.setModifiedDate(new Date());
        ticketReferenceRepository.save(ticketReference);
    }

    @Override
    public void setTicketReferenceAsFailed(TicketReference ticketReference, Exception exception) {
        String message = ExceptionUtils.getRootCauseMessage(exception);
        LOG.info("Updating TicketReference={} status as failed. With Reason={}", ticketReference.getId(), message);
        ticketReference.setStatus(TicketStatus.FAILED);
        ticketReference.setTicketFailedReason(message);
        ticketReference.setModifiedBy(Constants.USER_NAME);
        ticketReference.setModifiedDate(new Date());
        ticketReferenceRepository.save(ticketReference);
    }

    @Override
    public void updateReservationCount(TicketReference ticketReference) {
        if (ticketReference != null) {
            Long ownerId = null;
            if (ticketReference.getOwner() != null && ticketReference.getOwner().getId() != null) {
                ownerId = ticketReference.getOwner().getId();
            }

            if (ownerId == null) {
                if (ticketReference.getSerialNumberReservation() != null
                    && ticketReference.getSerialNumberReservation().getOwner() != null
                    && ticketReference.getSerialNumberReservation().getOwner().getId() != null) {
                    ownerId = ticketReference.getSerialNumberReservation().getOwner().getId();
                }
            }

            if (ownerId != null) {
                LOG.info("Updating reservation count for owner id={} and warehouse no=2", ownerId);
                ownerRepository.updateReservationCount(ownerId);
            }
        }
    }
}
