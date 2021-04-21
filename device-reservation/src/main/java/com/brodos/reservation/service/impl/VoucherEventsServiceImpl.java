/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brodos.reservation.entity.VoucherEvents;
import com.brodos.reservation.entity.VoucherEventsStatus;
import com.brodos.reservation.infrastructure.VoucherEventsRepository;
import com.brodos.reservation.service.VoucherEventsService;

/**
 *
 * @author padhaval
 */
@Service
@Transactional
public class VoucherEventsServiceImpl implements VoucherEventsService {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherEventsServiceImpl.class);

    @Autowired
    VoucherEventsRepository voucherEventsRepository;

    @Override
    public VoucherEvents createVoucherEvents(Integer eventId, String eventBody, VoucherEventsStatus status,
        String failureReason) {
        VoucherEvents voucherEvents = new VoucherEvents();
        voucherEvents.setId(eventId);
        voucherEvents.setEventBody(eventBody);
        voucherEvents.setStatus(status);
        voucherEvents.setCreatedDate(new Date());
        voucherEvents.setModifiedDate(new Date());
        voucherEvents.setFailureReason(failureReason);
        return voucherEventsRepository.save(voucherEvents);
    }
}
