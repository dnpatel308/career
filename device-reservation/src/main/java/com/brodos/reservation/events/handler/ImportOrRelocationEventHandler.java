package com.brodos.reservation.events.handler;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.assembler.VoucherEventsAssembler;
import com.brodos.reservation.entity.SerialNumber;
import com.brodos.reservation.entity.VoucherEventsStatus;
import com.brodos.reservation.events.SerialNumberImportEvent;
import com.brodos.reservation.events.SerialNumberRelocationEvent;
import com.brodos.reservation.exception.DeviceReservationException;
import com.brodos.reservation.infrastructure.ConfigurationRepository;
import com.brodos.reservation.infrastructure.SerialNumberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.brodos.reservation.service.SerialNumberEventHandlerService;

@Component
public class ImportOrRelocationEventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ImportOrRelocationEventHandler.class);

    @Autowired
    SerialNumberEventHandlerService serialNumberEventHandlerService;

    @Autowired
    SerialNumberRepository serialNumberRepository;

    @Autowired
    VoucherEventsAssembler voucherEventsAssembler;

    @Autowired
    ConfigurationRepository configurationRepository;

    private List<Integer> warehousePools;

    public Boolean handle(JsonNode eventObject) throws JSONException, Exception {
        warehousePools = Arrays.asList(configurationRepository.findById("warehouse.pools").get().getValue().split(","))
                .stream().map(String::trim).mapToInt(Integer::parseInt)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        ArrayNode lineitemsArray = (ArrayNode) eventObject.get("lineitems");
        int count = 0;
        int failed = 0;
        Exception ex = null;
        boolean gotCommonError = false;
        for (int i = 0; i < lineitemsArray.size() && !gotCommonError; i++) {
            ArrayNode serialsArray = (ArrayNode) lineitemsArray.get(i).get("serials");
            for (int j = 0; j < serialsArray.size() && !gotCommonError; j++) {
                try {
                    JsonNode serialObject = serialsArray.get(j);
                    count++;
                    LOG.info("Handing ImportOrRelocationEvent, Voucher No={}, Voucher Type={}, serialObject={}",
                            eventObject.get("voucherno"), eventObject.get("vouchertype"), serialObject);
                    if (eventObject.hasNonNull("towarehouse") && !StringUtils.isBlank(eventObject.get("towarehouse").asText())) {
                        if (!StringUtils.isBlank(serialObject.get("value").asText())) {
                            int toWarehouse = eventObject.get("towarehouse").asInt();
                            if (warehousePools.contains(toWarehouse)) {
                                SerialNumber serialNumber
                                        = serialNumberRepository.findByNumberAndArticle(serialObject.get("value").asText(), lineitemsArray.get(i).get("articleno").asText());
                                LOG.info("Got request for import serialNo={} into pool={}", serialObject.get("value"),
                                        toWarehouse);
                                SerialNumberImportEvent serialNumberImportEvent
                                        = voucherEventsAssembler.toSerialNumberImportEvent(i, eventObject, serialObject,
                                                serialNumber != null);
                                serialNumberEventHandlerService.importSerialNoInPool(serialNumberImportEvent);
                            } else {
                                LOG.info("Got request for ReLocation serialNo={}  ", serialObject.get("value"));
                                SerialNumberRelocationEvent serialNumberRelocationEvent
                                        = voucherEventsAssembler.toSerialNumberRelocationEvent(i, eventObject, serialObject);
                                serialNumberEventHandlerService.relocateSerialNoFromPool(serialNumberRelocationEvent);
                            }
                        }
                    } else {
                        throw new DeviceReservationException(ErrorCodes.INVALID_TO_WAREHOUSE.putMetadata(VoucherEventsStatus.IGNORED.name(), Boolean.TRUE));
                    }
                } catch (Exception exception) {
                    LOG.error(exception.getMessage(), exception);
                    ex = exception;
                    failed++;
                }
            }
        }

        if (count == 0) {
            throw new DeviceReservationException(ErrorCodes.SERIAL_NO_MISSING_REQUEST.putMetadata(VoucherEventsStatus.IGNORED.name(), Boolean.TRUE));
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
