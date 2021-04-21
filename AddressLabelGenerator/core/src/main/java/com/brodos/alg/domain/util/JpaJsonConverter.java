/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.domain.util;

import javax.persistence.AttributeConverter;
import org.json.JSONObject;

/**
 *
 * @author padhaval
 */
public class JpaJsonConverter implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object meta) {
        if (meta != null) {
            return meta.toString();
        } else {
            return null;
        }
    }

    @Override
    public JSONObject convertToEntityAttribute(String dbData) {
        if (dbData != null) {
            return new JSONObject(dbData);
        } else {
            return null;
        }
    }
}
