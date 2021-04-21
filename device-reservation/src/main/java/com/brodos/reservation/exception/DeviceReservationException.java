/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.exception;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.entity.VoucherEventsStatus;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author padhaval
 */
public class DeviceReservationException extends RuntimeException {

    private static final long serialVersionUID = -659071353901664839L;
    private final int code;
    private final Map<String, Object> metadata;

    public DeviceReservationException(ErrorCodes errorCodes, Object... msgArgs) {
        super(String.format(errorCodes.getMessage(), msgArgs));
        this.code = errorCodes.getCode();
        this.metadata = errorCodes.getMetadata();
    }

    public DeviceReservationException(int code, String message) {
        super(message);
        this.code = code;
        this.metadata = new HashMap<>();
    }

    public int getCode() {
        return code;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
