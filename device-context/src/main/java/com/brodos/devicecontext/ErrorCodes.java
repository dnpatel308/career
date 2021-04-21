/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext;

/**
 *
 * @author padhaval
 */
public enum ErrorCodes {

    REQUEST_BODY_MISSING(400, "Required request body is missing."),
    DEVICE_NOT_FOUND(404, "Device not found."),
    DEVICE_EXIST_WITH_DEFFERENT_ARTICLE(400, "Device exist with different article number."),
    DEVICE_CONFIG_NOT_FOUND(404, "Device configuration not found."),
    DEVICE_CONFIG_INCOMPLETE(400, "Device configuration is incomplete."),
    DB_CONNECTION_ERROR(500, "Unable to connect database."),
    INVALID_DEVICE_FIELD_VALUE(400, "Invalid value %s."),
    INVALID_DEVICE_CONFIG_TYPE(400, "Invalid type %s."),
    INVALID_DEVICE_INFORMATION(400, "Invalid device information."),
    INVALID_DEVICE_CONFIGURATION(400, "Invalid device configuration"),
    VALUE_ALREADY_ASSOCIATED(400, "%s already associated with another device."),
    VALUE_NOT_UNIQUE(400, "values are not unique");

    private final int code;
    private final String message;

    ErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
