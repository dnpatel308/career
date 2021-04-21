/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brodos.reservation.entity.SerialNumber;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.events.Imported;
import com.brodos.reservation.events.Opened;
import com.brodos.reservation.infrastructure.SerialNumberRepository;
import com.brodos.reservation.service.DeviceReservationHelperService;
import com.brodos.reservation.service.DeviceReservationInternalEventHandlerService;

/**
 *
 * @author padhaval
 */
@Service
public class DeviceReservationInternalEventHandlerServiceImpl implements DeviceReservationInternalEventHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceReservationInternalEventHandlerServiceImpl.class);

    @Autowired
    SerialNumberRepository serialNumberRepository;

    @Autowired
    DeviceReservationHelperService deviceReservationHelperService;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public synchronized SerialNumberReservation handleOpenedEvent(Opened openedEvent) {
        LOG.info("Handling opened event for reservationId={}", openedEvent.getReservationId());
        LOG.debug("Start code transaction");
        List<SerialNumber> serialNumbers =
            serialNumberRepository.findByArticleAndWarehouseNoAndReservable(openedEvent.getArticleNo(),
                openedEvent.getGroup(), true);
        SerialNumber serialNumber =
            deviceReservationHelperService.findSerialNumberWithValidDeviceContextInfo(serialNumbers);
        return deviceReservationHelperService.handleOpenedEvent(openedEvent, serialNumber);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
    public synchronized boolean handleImportedEvent(Imported importedEvent) {
        LOG.info("Handling imported event for reservationId={}", importedEvent.getReservationId());
        return deviceReservationHelperService.handleImportedEvent(importedEvent);
    }
}