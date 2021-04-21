/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.component;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.adapter.DeviceContextAdapter;
import com.brodos.reservation.exception.DeviceReservationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author padhaval
 */
@Component
public class DeviceContextComponent {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DeviceContextComponent.class);

    @Autowired
    DeviceContextAdapter deviceContextAdapter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode getDeviceContextInfoByArticleNoAndSerialNo(String articleNo, String serialNo) throws Exception {
        return objectMapper.readTree(deviceContextAdapter.getDeviceContextInfoByArticleNoAndSerialNo(articleNo,
            serialNo));
    }

    public boolean isDeviceInfoValid(JsonNode deviceContextJsonNode) {
        if (!deviceContextJsonNode.get("_embedded").get("devices").has(0)) {
            return false;
        }

        JsonNode devicesNode = deviceContextJsonNode.get("_embedded").get("devices").get(0);
        JsonNode jsonNode = devicesNode.get("device").deepCopy();
        return jsonNode.get("status").asText().equalsIgnoreCase("complete");
    }

    public JsonNode getExtractedDeviceInfo(JsonNode deviceContextJsonNode) {
        JsonNode devicesNode = deviceContextJsonNode.get("_embedded").get("devices").get(0);
        JsonNode jsonNode = devicesNode.get("device").deepCopy();
        ObjectNode deviceNode = new ObjectNode(JsonNodeFactory.instance);
        deviceNode.put("id", jsonNode.get("id").asText());
        ArrayNode fieldsNode = new ArrayNode(JsonNodeFactory.instance);
        for (int i = 0; i < jsonNode.get("fields").size(); i++) {
            ObjectNode fieldNode = objectMapper.convertValue(jsonNode.get("fields").get(i), ObjectNode.class);
            fieldNode.remove("type");
            fieldsNode.add(fieldNode);
        }

        deviceNode.put("fields", fieldsNode);

        ObjectNode _embeddedNode = new ObjectNode(JsonNodeFactory.instance);
        _embeddedNode.put("device", deviceNode);
        _embeddedNode.put("_links", devicesNode.get("_links"));

        return objectMapper.convertValue(_embeddedNode, JsonNode.class);
    }

    public String getImei1FromDeviceInfo(JsonNode deviceContextJsonNode) {
        if (deviceContextJsonNode.has("device")) {
            deviceContextJsonNode = deviceContextJsonNode.get("device");
        }

        for (int i = 0; i < deviceContextJsonNode.get("fields").size(); i++) {
            if (deviceContextJsonNode.get("fields").get(i).get("name").asText().equalsIgnoreCase("imei1")) {
                return deviceContextJsonNode.get("fields").get(i).get("value").asText();
            }
        }

        throw new DeviceReservationException(ErrorCodes.DEVICE_CONTEXT_ERROR);
    }

    public JsonNode createDeviceContextInfo(String imei1) {
        try {
            return objectMapper.readTree(String.format(
                "{\"device\":{\"id\":\"DUMMY\",\"fields\":[{\"name\":\"imei1\",\"value\":\"%s\"}]}}", imei1));
        } catch (JsonProcessingException ex) {
            LOG.error(ex.getMessage());
            throw new DeviceReservationException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }
}
