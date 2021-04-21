package com.brodos.reservation.filter;

import org.springframework.core.convert.converter.Converter;

import com.brodos.reservation.entity.DeviceReservationStatus;
import org.springframework.stereotype.Component;

@Component
public class StringToEnumConverter implements Converter<String, DeviceReservationStatus> {

    @Override
    public DeviceReservationStatus convert(String source) {
        return DeviceReservationStatus.valueOf(source.toUpperCase());
    }
}
