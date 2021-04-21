/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service;

import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.events.Cancelled;
import com.brodos.reservation.events.Imported;
import com.brodos.reservation.events.Pended;
import com.brodos.reservation.events.Reserved;
import com.brodos.reservation.events.Sentout;

/**
 *
 * @author padhaval
 */
public interface TicketService {

    public void createSerialNumberReservationTicketAndUpdate(Reserved reservedEvent) throws Exception;

    public void createSerialNumberUnavailableTicketAndUpdate(Pended pendingEvent) throws Exception;

    public void updateSerialNumberReservationTicket(Sentout sentoutEvent) throws Exception;

    public void updateSerialNumberReservationTicket(Cancelled cancelledEvent) throws Exception;

    public void updateSerialNumberImportIMEITicket(Imported importedEvent) throws Exception;

    public void updateSerialNumberImportIMEITicket(Cancelled cancelledEvent) throws Exception;

    public void setTicketReferenceAsFailed(TicketReference ticketReference, Exception exception);

    public void updateReservationCount(TicketReference ticketReference);
}
