/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.events.handler;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.assembler.VoucherEventsAssembler;
import com.brodos.reservation.entity.VoucherEventsStatus;
import com.brodos.reservation.events.SerialNumberSentoutEvent;
import com.brodos.reservation.exception.DeviceReservationException;
import com.brodos.reservation.infrastructure.SerialNumberReservationRepository;
import com.brodos.reservation.service.DeviceReservationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author padhaval
 */
@Component
public class SentoutEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SentoutEventHandler.class);

    @Autowired
    DeviceReservationService deviceReservationService;

    @Autowired
    SerialNumberReservationRepository serialNumberReservationRepository;

    @Autowired
    VoucherEventsAssembler voucherEventsAssembler;

    protected Boolean handle(JsonNode eventObject) throws Exception {
        ArrayNode lineitemsArray = (ArrayNode) eventObject.get("lineitems");
        int count = 0;
        int failed = 0;
        Exception ex = null;
        for (int i = 0; i < lineitemsArray.size(); i++) {
            ArrayNode serialsArray = (ArrayNode) lineitemsArray.get(i).get("serials");
            for (int j = 0; j < serialsArray.size(); j++) {
                try {
                    JsonNode serialObject = serialsArray.get(j);
                    count++;
                    LOG.debug("Handing SentoutEvent, Voucher No={}, Voucher Type={}, serialObject={}",
                        eventObject.get("voucherno"), eventObject.get("vouchertype"), serialObject);
                    if (!StringUtils.isBlank(serialObject.get("value").asText())) {
                        SerialNumberSentoutEvent serialNumberSentoutEvent =
                            voucherEventsAssembler.toSerialNumberSentoutEvent(i, eventObject, serialObject);
                        deviceReservationService.sentoutDeviceReservation(serialNumberSentoutEvent);
                    }
                } catch (Exception exception) {
                    LOG.error(exception.getMessage(), exception);
                    ex = exception;
                    failed++;
                }
            }
        }

        if (count == 0) {
            throw new DeviceReservationException(ErrorCodes.SERIAL_NO_MISSING_REQUEST.putMetadata(
                VoucherEventsStatus.IGNORED.name(), Boolean.TRUE));
        }

        if (failed > 0) {
            if (count == 1) {
                throw ex;
            } else {
                throw new DeviceReservationException(ErrorCodes.SERIAL_NO_FAILED_TO_PROCESS, failed, count);
            }
        }

        return true;
    }
}
