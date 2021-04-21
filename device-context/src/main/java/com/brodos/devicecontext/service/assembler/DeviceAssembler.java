/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.service.assembler;

import java.util.HashSet;

import com.brodos.devicecontext.model.dto.DeviceConfigFieldDTO;
import com.brodos.devicecontext.model.dto.DeviceFieldDTO;
import com.brodos.devicecontext.model.dto.request.CreateOrUpdateDeviceConfigDTO;
import com.brodos.devicecontext.model.dto.request.CreateOrUpdateDeviceDTO;
import com.brodos.devicecontext.model.entity.Device;
import com.brodos.devicecontext.model.entity.DeviceConfig;
import com.brodos.devicecontext.model.entity.DeviceField;
import com.brodos.devicecontext.model.entity.DeviceFieldConfig;
import com.brodos.devicecontext.model.entity.DeviceFieldPK;

/**
 *
 * @author padhaval
 */
public class DeviceAssembler {

    public Device toDevice(CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO) {
        Device device = new Device();
        device.setArticlenumber(createOrUpdateDeviceDTO.getArticleNo());
        device.setFields(new HashSet<>());

        for (DeviceFieldDTO deviceFieldDTO : createOrUpdateDeviceDTO.getFields()) {
            device.getFields().add(toDeviceField(device, deviceFieldDTO));
        }

        return device;
    }

    public DeviceField toDeviceField(Device device, DeviceFieldDTO deviceFieldDTO) {
        DeviceField deviceField = new DeviceField();
        deviceField.setDeviceFieldPK(new DeviceFieldPK(deviceFieldDTO.getName(), device));
        deviceField.setValue(deviceFieldDTO.getValue());
        return deviceField;
    }

    public DeviceConfig toDeviceConfigs(CreateOrUpdateDeviceConfigDTO createOrUpdateDeviceConfigDTO) {
        DeviceConfig deviceConfig = new DeviceConfig();
        deviceConfig.setArticlenumber(createOrUpdateDeviceConfigDTO.getArticleNo());
        deviceConfig.setDeviceFieldConfigs(new HashSet<>());

        for (DeviceConfigFieldDTO deviceConfigFieldDTO : createOrUpdateDeviceConfigDTO.getFields()) {
            deviceConfig.getDeviceFieldConfigs().add(toDeviceConfigField(deviceConfig, deviceConfigFieldDTO));
        }

        return deviceConfig;
    }

    public DeviceFieldConfig toDeviceConfigField(DeviceConfig device, DeviceConfigFieldDTO deviceConfigFieldDTO) {
        DeviceFieldConfig deviceFieldConfig = new DeviceFieldConfig();
        deviceFieldConfig.setDeviceConfig(device);
        deviceFieldConfig.setName(deviceConfigFieldDTO.getName());
        deviceFieldConfig.setType(deviceConfigFieldDTO.getType());
        return deviceFieldConfig;
    }
}
