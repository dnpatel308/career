package com.brodos.reservation.service;

import com.brodos.reservation.entity.SerialNumber;
import com.brodos.reservation.events.SerialNumberImportEvent;
import com.brodos.reservation.events.SerialNumberRelocationEvent;

public interface SerialNumberEventHandlerService {

    public SerialNumber getSerilNumberObjectForIMEI(SerialNumberImportEvent serialNumberImportEvent);

    public boolean importSerialNoInPool(SerialNumberImportEvent serialNumberImportEvent);

    public boolean relocateSerialNoFromPool(SerialNumberRelocationEvent serialNumberRelocationEvent);
}
