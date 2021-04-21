/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service;

import com.brodos.reservation.entity.VoucherEvents;
import com.brodos.reservation.entity.VoucherEventsStatus;

/**
 *
 * @author padhaval
 */
public interface VoucherEventsService {

    public VoucherEvents createVoucherEvents(Integer eventId, String eventBody, VoucherEventsStatus status,
        String failureReason);

}
