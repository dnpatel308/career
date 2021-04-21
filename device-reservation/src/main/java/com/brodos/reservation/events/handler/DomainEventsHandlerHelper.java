/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.events.handler;

import com.brodos.reservation.entity.SerialNumberReservationTicketReference;
import com.brodos.reservation.events.Cancelled;
import com.brodos.reservation.events.Imported;
import com.brodos.reservation.events.Opened;
import com.brodos.reservation.events.Pended;
import com.brodos.reservation.events.RequestedForSendout;
import com.brodos.reservation.events.Reserved;
import com.brodos.reservation.events.Sentout;
import com.brodos.reservation.infrastructure.ConfigurationRepository;
import com.brodos.reservation.service.DeviceReservationHelperService;
import com.brodos.reservation.service.DeviceReservationInternalEventHandlerService;
import com.brodos.reservation.service.EmailService;
import com.brodos.reservation.service.TicketService;
import com.brodos.reservation.service.VoucherService;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author padhaval
 */
@Component
public class DomainEventsHandlerHelper {

    @Autowired
    TicketService ticketService;

    @Autowired
    DeviceReservationInternalEventHandlerService deviceReservationInternalEventHandlerService;

    @Autowired
    EmailService emailService;

    @Autowired
    VoucherService voucherService;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    DeviceReservationHelperService deviceReservationHelperService;

    public void handleOpenedEvent(Opened openedEvent) throws Exception {
        deviceReservationInternalEventHandlerService.handleOpenedEvent(openedEvent);
    }

    public void handlePendingEvent(Pended pendingEvent) throws Exception {
        ticketService.createSerialNumberUnavailableTicketAndUpdate(pendingEvent);
    }

    public void handleReservedEvent(Reserved reservedEvent) throws Exception {
        ticketService.createSerialNumberReservationTicketAndUpdate(reservedEvent);
        if (!StringUtils.isBlank(reservedEvent.getEmail())) {
            emailService.sendReservationMail(reservedEvent.getTicketReference());
        }
        ticketService.updateReservationCount(reservedEvent.getTicketReference());
    }

    public void handleCancelledEvent(Cancelled cancelledEvent) throws Exception {
        if (cancelledEvent.getTicketReference() instanceof SerialNumberReservationTicketReference) {
            ticketService.updateSerialNumberReservationTicket(cancelledEvent);
            if (!cancelledEvent.getTicketReference().getSerialNumberReservation().getSerialNumber().getArchived()) {
                deviceReservationHelperService.reserveOpencaseIfExist(cancelledEvent.getTicketReference()
                    .getSerialNumberReservation().getSerialNumber());
            }
        } else {
            ticketService.updateSerialNumberImportIMEITicket(cancelledEvent);
        }
    }

    public void handleSentoutEvent(Sentout sentoutEvent) throws Exception {
        ticketService.updateSerialNumberReservationTicket(sentoutEvent);
    }

    public void handleImportedEvent(Imported importedEvent) throws Exception {
        ticketService.updateSerialNumberImportIMEITicket(importedEvent);
        deviceReservationInternalEventHandlerService.handleImportedEvent(importedEvent);
    }

    public void handleRequestedForSendoutEvent(RequestedForSendout requestedForSendout) throws Exception {
        if (Objects.equals(requestedForSendout.getGroup(),
            Integer.valueOf(configurationRepository.findById("default.pool").get().getValue()))) {
            voucherService.createVoucher(requestedForSendout);
        }
    }
}
