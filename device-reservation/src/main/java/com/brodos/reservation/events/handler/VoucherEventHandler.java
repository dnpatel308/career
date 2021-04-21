/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.events.handler;

import com.brodos.reservation.Constants;
import com.brodos.reservation.entity.VoucherEventsStatus;
import com.brodos.reservation.exception.DeviceReservationException;
import com.brodos.reservation.infrastructure.VoucherEventsRepository;
import com.brodos.reservation.service.VoucherEventsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.hibernate.exception.LockAcquisitionException;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Component;

/**
 *
 * @author padhaval
 */
@Component
public class VoucherEventHandler {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(VoucherEventHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    VoucherEventsRepository voucherEventsRepository;

    @Autowired
    VoucherEventsService voucherEventsService;

    @Autowired
    SentoutEventHandler sentoutEventHandler;

    @Autowired
    ImportOrRelocationEventHandler importOrRelocationEventHandler;

    public void handleMessage(ActiveMQTextMessage message) {
        Integer eventId = null;
        String content = null;
        try {
            eventId = message.getIntProperty("eventId");
            content = message.getText();
            boolean duplicateEvent = voucherEventsRepository.existsById(eventId);
            LOG.info("VoucherEventHandler handling event={}, isDuplicate={}", eventId, duplicateEvent);
            if (!duplicateEvent) {
                JsonNode eventNode = objectMapper.readTree(content);
                JsonNode eventBodyNode = objectMapper.readTree(eventNode.get("eventBody").asText());
                int tenantid = eventBodyNode.get("tenantid").asInt();
                if (tenantid != Constants.TENANT_ID) {
                    LOG.info("Skipping event={} due to invalid system id={}", eventId, tenantid);
                    return;
                }

                LOG.debug("event={}, event body={}", eventId, eventBodyNode);
                boolean isValidVoucherType = false;
                try {
                    switch (eventBodyNode.get("vouchertype").asText()) {
                        case "D": {
                            importOrRelocationEventHandler.handle(eventBodyNode);
                            isValidVoucherType = true;
                            break;
                        }

                        case "L": {
                            sentoutEventHandler.handle(eventBodyNode);
                            isValidVoucherType = true;
                            break;
                        }

                        default: {
                            break;
                        }
                    }

                    if (isValidVoucherType) {
                        voucherEventsService.createVoucherEvents(eventId, content, VoucherEventsStatus.SUCCESS, null);
                    }
                } catch (CannotAcquireLockException | LockAcquisitionException deadLockException) {
                    LOG.error(deadLockException.getMessage(), deadLockException);
                    handleMessage(message);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    String failureReason = "UNIDENTIFIED";
                    VoucherEventsStatus status = VoucherEventsStatus.FAILED;
                    Throwable t = ex;
                    while (t != null) {
                        if (t instanceof DeviceReservationException) {
                            DeviceReservationException dre = (DeviceReservationException) t;
                            failureReason = dre.getMessage();
                            if (dre.getMetadata().containsKey(VoucherEventsStatus.IGNORED.name())
                                    && Boolean.valueOf(dre.getMetadata().get(VoucherEventsStatus.IGNORED.name()).toString())) {
                                status = VoucherEventsStatus.IGNORED;
                            }
                            
                            break;
                        }

                        if (t instanceof JSONException) {
                            failureReason = "INVALID_EVENT_DATA";
                            break;
                        }

                        t = t.getCause();
                    }

                    voucherEventsService.createVoucherEvents(eventId, content, status, failureReason);
                }
            } else {
                LOG.info("VoucherEventHandler duplicate eventId={}", eventId);
            }
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
            voucherEventsService.createVoucherEvents(eventId, content, VoucherEventsStatus.FAILED, "INVALID_EVENT_DATA");
        }
    }
}
