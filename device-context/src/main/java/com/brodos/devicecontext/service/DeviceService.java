/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.service;

import org.springframework.data.domain.Page;

import com.brodos.devicecontext.model.dto.request.CreateOrUpdateDeviceDTO;
import com.brodos.devicecontext.model.dto.request.SearchDevicesDTO;
import com.brodos.devicecontext.model.entity.Device;

/**
 *
 * @author padhaval
 */
public interface DeviceService {

    public Device createDevice(CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO);

    public Device getDevice(Long id);

    public Device updateDevice(Long id, CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO);

    public Page<Device> searchDevices(SearchDevicesDTO searchDevicesDTO);
}
