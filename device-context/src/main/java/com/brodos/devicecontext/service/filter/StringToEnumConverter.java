package com.brodos.devicecontext.service.filter;

import com.brodos.devicecontext.model.entity.DeviceStatus;
import org.springframework.core.convert.converter.Converter;

import org.springframework.stereotype.Component;

@Component
public class StringToEnumConverter implements Converter<String, DeviceStatus> {

    @Override
    public DeviceStatus convert(String source) {
        return DeviceStatus.valueOf(source.toUpperCase());
    }
}
