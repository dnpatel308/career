/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service;

import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.events.Imported;
import com.brodos.reservation.events.Opened;

/**
 *
 * @author padhaval
 */
public interface DeviceReservationInternalEventHandlerService {

    public SerialNumberReservation handleOpenedEvent(Opened openedEvent);

    public boolean handleImportedEvent(Imported importedEvent);
}
