/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.model.dto.response;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class DeviceConfigsResponseDTO {

    private DeviceConfigResponseDTO configuration;

    public DeviceConfigResponseDTO getConfiguration() {
        return configuration;
    }

    public void setConfiguration(DeviceConfigResponseDTO configuration) {
        this.configuration = configuration;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
