/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.service.assembler;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import com.brodos.devicecontext.DeviceContextApplication;
import com.brodos.devicecontext.model.dto.DeviceConfigFieldDTO;
import com.brodos.devicecontext.model.dto.DeviceFieldDTO;
import com.brodos.devicecontext.model.dto.request.SearchDevicesDTO;
import com.brodos.devicecontext.model.dto.response.DeviceConfigResponseDTO;
import com.brodos.devicecontext.model.dto.response.DeviceConfigsResponseDTO;
import com.brodos.devicecontext.model.dto.response.DeviceDTO;
import com.brodos.devicecontext.model.dto.response.DeviceResponseDTO;
import com.brodos.devicecontext.model.dto.response.DeviceSearchResponseDTO;
import com.brodos.devicecontext.model.dto.response.DevicesDTO;
import com.brodos.devicecontext.model.dto.response.EmbeddedDeviceConfigsResponseDTO;
import com.brodos.devicecontext.model.dto.response.EmbeddedDevicesDTO;
import com.brodos.devicecontext.model.dto.response.HealthResponseDTO;
import com.brodos.devicecontext.model.dto.response.LinkDTO;
import com.brodos.devicecontext.model.dto.response.PageDTO;
import com.brodos.devicecontext.model.dto.response.QueryString;
import com.brodos.devicecontext.model.entity.Device;
import com.brodos.devicecontext.model.entity.DeviceConfig;
import com.brodos.devicecontext.model.entity.DeviceField;
import com.brodos.devicecontext.model.entity.DeviceFieldConfig;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author padhaval
 */
public class ResponseAssembler {

    public HealthResponseDTO toHealthResponseDTO(boolean success) {
        HealthResponseDTO healthResponseDTO = new HealthResponseDTO();
        healthResponseDTO.setServiceName("device-context-service");
        healthResponseDTO.setTimestamp(System.currentTimeMillis());
        healthResponseDTO.setIsSuccessful(success);
        return healthResponseDTO;
    }

    public EmbeddedDevicesDTO toEmbeddedDeviceResponseDTOs(String requestUrl, SearchDevicesDTO searchDevicesDTO,
            Page<Device> devices) {
        EmbeddedDevicesDTO embeddedDevicesDTO = new EmbeddedDevicesDTO();
        embeddedDevicesDTO.setEmbedded(new DevicesDTO());
        embeddedDevicesDTO.getEmbedded().setDevices(new ArrayList<>());
        for (Device device2 : devices) {
            DeviceDTO device = new DeviceDTO();
            device.setDevice(toDeviceResponseSearchDTO(device2));
            device.getLinks().setSelf(updateSelfSearchLink(requestUrl, device.getDevice()));
            embeddedDevicesDTO.getEmbedded().getDevices().add(device);
        }
        PageDTO pageDTO = new PageDTO();
        pageDTO.setNumber(devices.getNumber());
        pageDTO.setSize(devices.getSize());
        pageDTO.setTotalpages(devices.getTotalPages());
        pageDTO.setTotalelements(devices.getTotalElements());
        embeddedDevicesDTO.setPage(pageDTO);
        if (pageDTO.getTotalpages() > searchDevicesDTO.getPage() + 1) {
            updatePageLink(requestUrl, embeddedDevicesDTO, searchDevicesDTO, pageDTO);
        }
        return embeddedDevicesDTO;
    }

    private LinkDTO updateSelfSearchLink(String requestUrl, DeviceSearchResponseDTO deviceSearchResponseDTO) {
        StringBuilder selfLinkBuilder = new StringBuilder();
        selfLinkBuilder.append(StringUtils.substringBefore(requestUrl, DeviceContextApplication.CONTEXT_PATH));
        selfLinkBuilder.append(DeviceContextApplication.CONTEXT_PATH);
        selfLinkBuilder.append("/devices");
        selfLinkBuilder.append("/");
        selfLinkBuilder.append(deviceSearchResponseDTO.getId());
        return new LinkDTO(selfLinkBuilder.toString());
    }

    public DeviceSearchResponseDTO toDeviceResponseSearchDTO(Device device) {
        DeviceSearchResponseDTO deviceSearchResponseDTO = new DeviceSearchResponseDTO();
        deviceSearchResponseDTO.setId(device.getId());
        deviceSearchResponseDTO.setArticleNo(device.getArticlenumber());
        deviceSearchResponseDTO.setFields(new HashSet<>());
        deviceSearchResponseDTO.setStatus(device.getStatus());

        for (DeviceField deviceField : device.getFields()) {
            deviceSearchResponseDTO.getFields().add(toDeviceFieldDTO(deviceField));
        }
        return deviceSearchResponseDTO;
    }

    public DeviceResponseDTO toDeviceResponseDTO(String requestUrl, Device device) {
        DeviceResponseDTO deviceResponseDTO = new DeviceResponseDTO();
        deviceResponseDTO.setId(device.getId());
        deviceResponseDTO.setArticleNo(device.getArticlenumber());
        deviceResponseDTO.setFields(new HashSet<>());
        deviceResponseDTO.setStatus(device.getStatus());

        for (DeviceField deviceField : device.getFields()) {
            deviceResponseDTO.getFields().add(toDeviceFieldDTO(deviceField));
        }

        updateSelfLink(requestUrl, deviceResponseDTO);
        return deviceResponseDTO;
    }

    public DeviceFieldDTO toDeviceFieldDTO(DeviceField deviceField) {
        DeviceFieldDTO deviceFieldDTO = new DeviceFieldDTO();
        deviceFieldDTO.setName(deviceField.getDeviceFieldPK().getName());
        deviceFieldDTO.setValue(deviceField.getValue());
        Optional<DeviceFieldConfig> deviceConfigField
                = deviceField.getDeviceFieldPK().getDevice().getDeviceConfig().getDeviceFieldConfigs().stream()
                        .filter((t) -> t.getName().equals(deviceFieldDTO.getName())).findFirst();
        if (deviceConfigField.isPresent()) {
            deviceFieldDTO.setType(deviceConfigField.get().getType());
        } else {
            deviceFieldDTO.setType("string(40)");
        }
        return deviceFieldDTO;
    }

    private void updateSelfLink(String requestUrl, DeviceResponseDTO deviceResponseDTO) {
        StringBuilder selfLinkBuilder = new StringBuilder();
        selfLinkBuilder.append(StringUtils.substringBefore(requestUrl, DeviceContextApplication.CONTEXT_PATH));
        selfLinkBuilder.append(DeviceContextApplication.CONTEXT_PATH);
        selfLinkBuilder.append("/devices");
        selfLinkBuilder.append("/");
        selfLinkBuilder.append(deviceResponseDTO.getId());

        deviceResponseDTO.getLinks().setSelf(new LinkDTO(selfLinkBuilder.toString()));
    }

    private void updatePageLink(String requestUrl, EmbeddedDevicesDTO embeddedDevicesDTO,
        SearchDevicesDTO searchDevicesDTO, PageDTO pageDTO) {
        StringBuilder selfLinkBuilder = new StringBuilder();
        selfLinkBuilder.append(StringUtils.substringBefore(requestUrl, DeviceContextApplication.CONTEXT_PATH));
        selfLinkBuilder.append(DeviceContextApplication.CONTEXT_PATH);
        selfLinkBuilder.append("/devices");
        selfLinkBuilder.append("?");
        QueryString qs = new QueryString("_size", String.valueOf(pageDTO.getSize()));
        qs.add("_page", String.valueOf(searchDevicesDTO.getPage() + 1));
        if (searchDevicesDTO.getArticlenumber() != null) {
            qs.add("articlenumber", searchDevicesDTO.getArticlenumber().trim());
        }
        if (searchDevicesDTO.getFields().getImei1() != null) {
            qs.add("fields.imei1", searchDevicesDTO.getFields().getImei1().trim());
        }
        if (searchDevicesDTO.getFields().getImei2() != null) {
            qs.add("fields.imei2", searchDevicesDTO.getFields().getImei2().trim());
        }
        if (searchDevicesDTO.getFields().getSerial() != null) {
            qs.add("fields.serial", searchDevicesDTO.getFields().getSerial().trim());
        }
        selfLinkBuilder.append(qs);
        embeddedDevicesDTO.getLinks().setNext((new LinkDTO(selfLinkBuilder.toString())));
    }

    public DeviceConfigResponseDTO toDeviceConfigResponseDTO(String requestUrl, DeviceConfig deviceConfig) {
        DeviceConfigResponseDTO deviceConfigResponseDTO = new DeviceConfigResponseDTO();
        if (deviceConfig != null) {
            deviceConfigResponseDTO.setId(deviceConfig.getId());
            deviceConfigResponseDTO.setArticleNo(deviceConfig.getArticlenumber());
            deviceConfigResponseDTO.setFields(new HashSet<>());

            for (DeviceFieldConfig deviceFieldConfig : deviceConfig.getDeviceFieldConfigs()) {
                deviceConfigResponseDTO.getFields().add(toDeviceConfigFieldDTO(deviceFieldConfig));
            }

            updateSelfLink(requestUrl, deviceConfigResponseDTO);
        }

        return deviceConfigResponseDTO;
    }

    public EmbeddedDeviceConfigsResponseDTO toEmbeddedDeviceConfigsResponseDTO(HttpServletRequest request,
        DeviceConfigResponseDTO deviceConfigResponseDTO) {
        DeviceConfigsResponseDTO deviceConfigsResponseDTO = new DeviceConfigsResponseDTO();
        deviceConfigsResponseDTO.setConfiguration(deviceConfigResponseDTO);
        EmbeddedDeviceConfigsResponseDTO embeddedDeviceConfigsResponseDTO = new EmbeddedDeviceConfigsResponseDTO();
        embeddedDeviceConfigsResponseDTO.setEmbedded(deviceConfigsResponseDTO);
        updateSelfLink(request, embeddedDeviceConfigsResponseDTO);
        return embeddedDeviceConfigsResponseDTO;
    }

    public DeviceConfigFieldDTO toDeviceConfigFieldDTO(DeviceFieldConfig deviceFieldConfig) {
        DeviceConfigFieldDTO deviceConfigFieldDTO = new DeviceConfigFieldDTO();
        deviceConfigFieldDTO.setName(deviceFieldConfig.getName());
        deviceConfigFieldDTO.setType(deviceFieldConfig.getType());
        return deviceConfigFieldDTO;
    }

    private void updateSelfLink(String requestUrl, DeviceConfigResponseDTO deviceConfigResponseDTO) {
        StringBuilder selfLinkBuilder = new StringBuilder();
        selfLinkBuilder.append(StringUtils.substringBefore(requestUrl, DeviceContextApplication.CONTEXT_PATH));
        selfLinkBuilder.append(DeviceContextApplication.CONTEXT_PATH);
        selfLinkBuilder.append("/configurations");
        selfLinkBuilder.append("/");
        selfLinkBuilder.append(deviceConfigResponseDTO.getId());

        deviceConfigResponseDTO.getLinks().setSelf(new LinkDTO(selfLinkBuilder.toString()));
    }

    private void updateSelfLink(HttpServletRequest request,
        EmbeddedDeviceConfigsResponseDTO embeddedDeviceConfigsResponseDTO) {
        StringBuilder selfLinkBuilder = new StringBuilder();
        selfLinkBuilder.append(request.getRequestURL().toString());
        selfLinkBuilder.append("?");
        selfLinkBuilder.append(request.getQueryString());
        embeddedDeviceConfigsResponseDTO.getLinks().setSelf(new LinkDTO(selfLinkBuilder.toString()));
    }
}
