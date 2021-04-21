package com.brodos.devicecontext.service;

import com.brodos.devicecontext.model.dto.request.CreateOrUpdateDeviceConfigDTO;
import com.brodos.devicecontext.model.entity.DeviceConfig;

public interface DeviceConfigService {

    public DeviceConfig createDeviceConfig(CreateOrUpdateDeviceConfigDTO createOrUpdateDeviceDTO);

    public DeviceConfig getDeviceConfig(Long id);

    public DeviceConfig getByArticlenumber(String articlenumber);
}
