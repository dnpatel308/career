/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service;

import com.brodos.reservation.dto.request.DeviceReservationActionDTO;
import com.brodos.reservation.dto.request.DeviceReservationRequestDTO;
import com.brodos.reservation.entity.SerialNumberImportTicketReference;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.SerialNumberReservationTicketReference;
import com.brodos.reservation.entity.TicketReference;

/**
 *
 * @author padhaval
 */
public interface DomainEventsHelperService {

    public void createAndStoreOpenedEvent(SerialNumberReservation serialNumberReservation,
        DeviceReservationRequestDTO deviceReservationRequestDTO);

    public void createAndStoreReservedEvent(
        SerialNumberReservationTicketReference serialNumberReservationTicketReference);

    public void createAndStorePendingEvent(SerialNumberImportTicketReference serialNumberImportTicketReference);

    public void createAndStoreSentoutEvent(SerialNumberReservation serialNumberReservation);

    public void createAndStoreCancelledEvent(TicketReference ticketReference);

    public void createAndStoreImportedEvent(SerialNumberReservation serialNumberReservation);

    public void createAndStoreRequestForSentoutEvent(SerialNumberReservation serialNumberReservation,
        DeviceReservationActionDTO deviceReservationActionDTO);
}
