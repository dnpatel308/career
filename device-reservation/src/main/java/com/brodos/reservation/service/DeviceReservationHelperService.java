/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service;

import com.brodos.reservation.dto.request.DeviceReservationActionDTO;
import com.brodos.reservation.entity.SerialNumber;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.events.Imported;
import com.brodos.reservation.events.Opened;
import java.util.List;

/**
 *
 * @author padhaval
 */
public interface DeviceReservationHelperService {

    public void updateCustomerDetail(SerialNumberReservation serialNumberReservation, String customerNo);

    public SerialNumberReservation handleOpenedEvent(Opened openedEvent, SerialNumber serialNumber);

    public SerialNumber findSerialNumberWithValidDeviceContextInfo(List<SerialNumber> serialNumbers);

    public SerialNumberReservation cancelDeviceReservation(SerialNumberReservation serialNumberReservation,
        String reason);

    public SerialNumberReservation requestForSentoutDeviceReservation(TicketReference ticketReference,
        DeviceReservationActionDTO deviceReservationActionDTO);

    public SerialNumberReservation sentoutDeviceReservation(TicketReference ticketReference);

    public boolean reserveOpencaseIfExist(SerialNumber serialNumber);

    public void enrichSerialNuberReservationDetail(SerialNumberReservation serialNumberReservation);

    public boolean handleImportedEvent(Imported importedEvent);

    public Long getIncrementalBulkReservationId();
}
