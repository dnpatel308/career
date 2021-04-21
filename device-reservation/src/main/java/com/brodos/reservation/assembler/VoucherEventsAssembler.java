/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.assembler;

import com.brodos.reservation.events.SerialNumberImportEvent;
import com.brodos.reservation.events.SerialNumberRelocationEvent;
import com.brodos.reservation.events.SerialNumberSentoutEvent;
import com.brodos.reservation.utility.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 *
 * @author padhaval
 */
public class VoucherEventsAssembler {

    public SerialNumberImportEvent toSerialNumberImportEvent(int lineItemIndex, JsonNode eventObject,
        JsonNode serialObject, boolean isRestoreRequest) {
        ArrayNode lineitemsArray = (ArrayNode) eventObject.get("lineitems");
        SerialNumberImportEvent serialNumberImportEvent = new SerialNumberImportEvent();
        serialNumberImportEvent.setArticleNo(lineitemsArray.get(lineItemIndex).get("articleno").asText());
        serialNumberImportEvent.setSerialNo(serialObject.get("value").asText());
        serialNumberImportEvent.setTenantId(eventObject.get("tenantid").asLong());
        serialNumberImportEvent.setUser(Utils.findUserFromVoucherImportJSONData(eventObject));
        serialNumberImportEvent.setRestoreRequest(isRestoreRequest);
        serialNumberImportEvent.setToWarehouseNo(eventObject.get("towarehouse").asInt());
        return serialNumberImportEvent;
    }

    public SerialNumberRelocationEvent toSerialNumberRelocationEvent(int lineItemIndex, JsonNode eventObject,
        JsonNode serialObject) {
        ArrayNode lineitemsArray = (ArrayNode) eventObject.get("lineitems");
        SerialNumberRelocationEvent serialNumberRelocationEvent = new SerialNumberRelocationEvent();
        serialNumberRelocationEvent.setArticleNo(lineitemsArray.get(lineItemIndex).get("articleno").asText());
        serialNumberRelocationEvent.setSerialNo(serialObject.get("value").asText());
        serialNumberRelocationEvent.setTenantId(eventObject.get("tenantid").asLong());
        serialNumberRelocationEvent.setUser(Utils.findUserFromVoucherImportJSONData(eventObject));
        serialNumberRelocationEvent.setToWarehouseNo(eventObject.get("towarehouse").asInt());
        return serialNumberRelocationEvent;
    }

    public SerialNumberSentoutEvent toSerialNumberSentoutEvent(int lineItemIndex, JsonNode eventObject,
        JsonNode serialObject) {
        ArrayNode lineitemsArray = (ArrayNode) eventObject.get("lineitems");
        SerialNumberSentoutEvent serialNumberSentoutEvent = new SerialNumberSentoutEvent();
        serialNumberSentoutEvent.setArticleNo(lineitemsArray.get(lineItemIndex).get("articleno").asText());
        serialNumberSentoutEvent.setSerialNo(serialObject.get("value").asText());
        serialNumberSentoutEvent.setTenantId(eventObject.get("tenantid").asLong());
        serialNumberSentoutEvent.setUser(Utils.findUserFromVoucherImportJSONData(eventObject));
        return serialNumberSentoutEvent;
    }
}
