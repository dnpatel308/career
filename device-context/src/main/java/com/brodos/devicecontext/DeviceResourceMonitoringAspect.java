/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext;

import com.brodos.devicecontext.model.dto.response.DeviceResponseDTO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author padhaval
 */
@Aspect
@Component
public class DeviceResourceMonitoringAspect {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceResourceMonitoringAspect.class);

    private final Counter createDeviceCounter;
    private final Counter createDeviceSuccessCounter;
    private final Counter updateDeviceCounter;
    private final Counter updateDeviceSuccessCounter;
    private final Counter searchDevicesCounter;
    private final Counter searchDevicesSuccessCounter;
    private final Counter getDeviceCounter;
    private final Counter getDeviceSuccessCounter;

    public DeviceResourceMonitoringAspect(MeterRegistry meterRegistry) {
        LOG.info("meterRegistry={}", meterRegistry);
        createDeviceCounter = meterRegistry.counter("request.create.device");
        createDeviceSuccessCounter = meterRegistry.counter("request.create.device.success");
        updateDeviceCounter = meterRegistry.counter("request.update.device");
        updateDeviceSuccessCounter = meterRegistry.counter("request.update.device.success");
        searchDevicesCounter = meterRegistry.counter("request.search.devices");
        searchDevicesSuccessCounter = meterRegistry.counter("request.search.devices.success");
        getDeviceCounter = meterRegistry.counter("request.get.device");
        getDeviceSuccessCounter = meterRegistry.counter("request.get.device.success");
    }

    @Before("execution(* com.brodos.devicecontext.service.DeviceResource.createDevice(..))")
    public void beforeCreateDeviceRequest() {
        createDeviceCounter.increment();
    }

    @AfterReturning(value = "execution(* com.brodos.devicecontext.service.DeviceResource.createDevice(..))",
        returning = "deviceResponseDTO")
    public void afterCreateDeviceRequest(DeviceResponseDTO deviceResponseDTO) {
        createDeviceSuccessCounter.increment();
    }

    @Before("execution(* com.brodos.devicecontext.service.DeviceResource.updateDevice(..))")
    public void beforeUpdateDeviceRequest() {
        updateDeviceCounter.increment();
    }

    @AfterReturning(value = "execution(* com.brodos.devicecontext.service.DeviceResource.updateDevice(..))",
        returning = "deviceResponseDTO")
    public void afterUpdateDeviceRequest(DeviceResponseDTO deviceResponseDTO) {
        updateDeviceSuccessCounter.increment();
    }

    @Before("execution(* com.brodos.devicecontext.service.DeviceResource.searchDevices(..))")
    public void beforeSearchDevicesRequest() {
        searchDevicesCounter.increment();
    }

    @AfterReturning(value = "execution(* com.brodos.devicecontext.service.DeviceResource.searchDevices(..))",
        returning = "deviceResponseDTO")
    public void afterSearchDevicesRequest(DeviceResponseDTO deviceResponseDTO) {
        searchDevicesSuccessCounter.increment();
    }

    @Before("execution(* com.brodos.devicecontext.service.DeviceResource.getDevice(..))")
    public void beforeGetDeviceRequest() {
        getDeviceCounter.increment();
    }

    @AfterReturning(value = "execution(* com.brodos.devicecontext.service.DeviceResource.getDevice(..))",
        returning = "deviceResponseDTO")
    public void afterGetDeviceRequest(DeviceResponseDTO deviceResponseDTO) {
        getDeviceSuccessCounter.increment();
    }
}
