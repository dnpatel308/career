/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service.impl;

import com.brodos.reservation.component.BrodosVoucherComponent;
import com.brodos.reservation.component.OrderMapper;
import com.brodos.reservation.entity.SerialNumberReservationTicketReference;
import com.brodos.reservation.events.RequestedForSendout;
import com.brodos.reservation.infrastructure.TicketReferenceRepository;
import com.brodos.reservation.service.EmailService;
import com.brodos.reservation.service.VoucherService;
import com.brodos.xmlserver.voucher.entity.Data;
import com.brodos.xmlserver.voucher.entity.VoucherReturnContainer;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author padhaval
 */
@Service
public class VoucherServiceImpl implements VoucherService {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(VoucherServiceImpl.class);

    @Autowired
    private BrodosVoucherComponent brodosVoucherComponent;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    TicketReferenceRepository ticketReferenceRepository;

    @Override
    public String createVoucher(RequestedForSendout requestedForSendout) throws Exception {
        if (!StringUtils.isBlank(requestedForSendout.getCustomerno())) {
            Data data = orderMapper.map(requestedForSendout);
            LOG.debug("Username={} and password={}", brodosVoucherComponent.getUserName(),
                brodosVoucherComponent.getPassword());

            SerialNumberReservationTicketReference ticketReference =
                (SerialNumberReservationTicketReference) requestedForSendout.getTicketReference();
            VoucherReturnContainer voucherReturnContainer =
                createVoucherWithRetry(ticketReference.getTicketNumber(), data);
            if (Objects.nonNull(voucherReturnContainer.getResult())
                && !StringUtils.isEmpty(voucherReturnContainer.getResult().getVoucherno())) {
                LOG.info("Created voucherNo={} for reserved SerialNumber={}", voucherReturnContainer.getResult()
                    .getVoucherno(), ticketReference.getSerialNumberReservation().getSerialNumber());
                sendRequestSentoutMail(requestedForSendout, true, voucherReturnContainer.getResult().getVoucherno());
                ticketReference.getSerialNumberReservation().setBrodosVoucherNo(
                    voucherReturnContainer.getResult().getVoucherno());
                ticketReference.getSerialNumberReservation().setBrodosVoucherMailSuccess(true);
                ticketReferenceRepository.save(ticketReference);
                return voucherReturnContainer.getResult().getVoucherno();
            }

            LOG.info("Voucher creation failed for reservationId={}", requestedForSendout.getReservationId());
            LOG.debug("Voucher creation failure, Response from server VoucherReturnContainer={}",
                voucherReturnContainer.toString());
            sendRequestSentoutMail(requestedForSendout, false, null);
            ticketReference.getSerialNumberReservation().setBrodosVoucherMailSuccess(true);
            ticketReferenceRepository.save(ticketReference);
            return null;
        } else {
            LOG.info("Unable to create voucher due to missing customer number in request. Ticket number={}",
                requestedForSendout.getTicketReference().getTicketNumber());
            sendRequestSentoutMail(requestedForSendout, false, null);
            return null;
        }
    }

    private VoucherReturnContainer createVoucherWithRetry(String ticketNumber, Data data) {
        VoucherReturnContainer voucherReturnContainer = null;
        int maxRetryCount = 3;
        for (int i = 0; i < maxRetryCount
            && (voucherReturnContainer == null || voucherReturnContainer.getResult() == null); i++) {
            LOG.info("Trying to create voucher for ticketNumber={} with retryCount={}", ticketNumber, i + 1);
            voucherReturnContainer =
                brodosVoucherComponent.getVoucherService().createVoucher(data, brodosVoucherComponent.getUserName(),
                    brodosVoucherComponent.getPassword());
        }

        return voucherReturnContainer;
    }

    private void sendRequestSentoutMail(RequestedForSendout requestedForSendout, boolean isVoucherCreated,
        String voucherno) {
        SerialNumberReservationTicketReference ticketReference =
            (SerialNumberReservationTicketReference) requestedForSendout.getTicketReference();
        try {
            if (isVoucherCreated) {
                emailService.sendRequestSentoutSuccessMail(ticketReference, voucherno);
            } else {
                emailService.sendRequestSentoutFailureMail(requestedForSendout);
            }
        } catch (Exception ex) {
            ticketReference.getSerialNumberReservation().setBrodosVoucherMailSuccess(false);
            LOG.error("Failure in sending mail for REQUESTFORSENTOUT={}", ex);
        }
    }
}
