/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.utility;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author padhaval
 */
public class Utils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static synchronized String findUserFromVoucherImportJSONData(JsonNode eventObject) {
        String user = "SYNC";
        if (eventObject.has("clerk") && eventObject.get("clerk").asText() != null) {
            user = eventObject.get("clerk").asText();
        } else if (eventObject.has("officer") && eventObject.get("officer").asText() != null) {
            user = eventObject.get("officer").asText();
        }
        return user;
    }

    public static synchronized String writeValueAsString(Object object, JsonInclude.Include include)
        throws JsonProcessingException {
        return OBJECT_MAPPER.setSerializationInclusion(include).writeValueAsString(object);
    }

    public static synchronized <T> T readValue(String jsonString, Class<T> type) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonString, type);
    }
}
