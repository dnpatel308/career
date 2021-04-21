/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brodos.devicecontext.DeviceContextApplication;
import com.brodos.devicecontext.model.dto.request.CreateOrUpdateDeviceDTO;
import com.brodos.devicecontext.model.dto.request.SearchDevicesDTO;
import com.brodos.devicecontext.model.dto.response.DeviceResponseDTO;
import com.brodos.devicecontext.model.dto.response.EmbeddedDevicesDTO;
import com.brodos.devicecontext.service.assembler.ResponseAssembler;

import io.micrometer.core.annotation.Timed;

/**
 *
 * @author padhaval
 */
@RestController
@RequestMapping(DeviceContextApplication.CONTEXT_PATH + "/devices")
public class DeviceResource {

    @Autowired
    DeviceService deviceService;

    @Autowired
    ResponseAssembler responseAssembler;

    @Timed(description = "Create Device")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DeviceResponseDTO createDevice(HttpServletRequest request,
        @RequestBody @Valid CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO) {
        return responseAssembler.toDeviceResponseDTO(request.getRequestURL().toString(),
            deviceService.createDevice(createOrUpdateDeviceDTO));
    }

    @Timed(description = "Update Device")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DeviceResponseDTO updateDevice(HttpServletRequest request, @PathVariable("id") Long id,
        @RequestBody @Valid CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO) {
        return responseAssembler.toDeviceResponseDTO(request.getRequestURL().toString(),
            deviceService.updateDevice(id, createOrUpdateDeviceDTO));
    }

    @Timed(description = "Get device")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DeviceResponseDTO getDevice(HttpServletRequest request, @PathVariable("id") Long id) {
        return responseAssembler.toDeviceResponseDTO(request.getRequestURL().toString(), deviceService.getDevice(id));
    }

    @Timed(description = "Search Devices")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EmbeddedDevicesDTO searchDevices(HttpServletRequest request, SearchDevicesDTO searchDevicesDTO,
        @RequestParam(name = "_page") int pageNo, @RequestParam(name = "_size") int size) {
        searchDevicesDTO.setPage(pageNo);
        searchDevicesDTO.setSize(size);
        return responseAssembler.toEmbeddedDeviceResponseDTOs(request.getRequestURL().toString(), searchDevicesDTO,
            deviceService.searchDevices(searchDevicesDTO));
    }

}
