/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.model.dto.response;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class DevicesDTO {
    List<DeviceDTO> devices;

    public List<DeviceDTO> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceDTO> devices) {
        this.devices = devices;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
