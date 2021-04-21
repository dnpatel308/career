/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.service.exception;

import com.brodos.devicecontext.ErrorCodes;

/**
 *
 * @author padhaval
 */
public class DeviceContextException extends RuntimeException {

    private static final long serialVersionUID = -659071353901664839L;
    private final int code;

    public DeviceContextException(ErrorCodes errorCodes, Object... msgArgs) {
        super(String.format(errorCodes.getMessage(), msgArgs));
        this.code = errorCodes.getCode();
    }

    public DeviceContextException(int code, String message, Object... msgArgs) {
        super(String.format(message, msgArgs));
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
