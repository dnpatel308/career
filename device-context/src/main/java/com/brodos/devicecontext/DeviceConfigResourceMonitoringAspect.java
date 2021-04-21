/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext;

import com.brodos.devicecontext.model.dto.response.DeviceConfigResponseDTO;
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
public class DeviceConfigResourceMonitoringAspect {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceConfigResourceMonitoringAspect.class);

    private final Counter createDeviceConfigCounter;
    private final Counter createDeviceConfigSuccessCounter;
    private final Counter getDeviceConfigByIdCounter;
    private final Counter getDeviceConfigByIdSuccessCounter;
    private final Counter getDeviceConfigByArticleNoCounter;
    private final Counter getDeviceConfigByArticleNoSuccessCounter;

    public DeviceConfigResourceMonitoringAspect(MeterRegistry meterRegistry) {
        LOG.info("meterRegistry={}", meterRegistry);
        createDeviceConfigCounter = meterRegistry.counter("request.create.deviceconfig");
        createDeviceConfigSuccessCounter = meterRegistry.counter("request.create.deviceconfig.success");
        getDeviceConfigByIdCounter = meterRegistry.counter("request.getbyid.deviceconfig");
        getDeviceConfigByIdSuccessCounter = meterRegistry.counter("request.getbyid.deviceconfig.success");
        getDeviceConfigByArticleNoCounter = meterRegistry.counter("request.getbyarticleno.deviceconfig");
        getDeviceConfigByArticleNoSuccessCounter = meterRegistry.counter("request.getbyarticleno.deviceconfig.success");
    }

    @Before("execution(* com.brodos.devicecontext.service.DeviceConfigResource.createDeviceConfig(..))")
    public void beforeCreateDeviceConfigRequest() {
        createDeviceConfigCounter.increment();
    }

    @AfterReturning(
        value = "execution(* com.brodos.devicecontext.service.DeviceConfigResource.createDeviceConfig(..))",
        returning = "deviceConfigResponseDTO")
    public void afterCreateDeviceConfigRequest(DeviceConfigResponseDTO deviceConfigResponseDTO) {
        createDeviceConfigSuccessCounter.increment();
    }

    @Before("execution(* com.brodos.devicecontext.service.DeviceConfigResource.getDeviceConfigById(..))")
    public void beforeGetDeviceConfigByIdRequest() {
        getDeviceConfigByIdCounter.increment();
    }

    @AfterReturning(
        value = "execution(* com.brodos.devicecontext.service.DeviceConfigResource.getDeviceConfigById(..))",
        returning = "deviceConfigResponseDTO")
    public void afterGetDeviceConfigByIdRequest(DeviceConfigResponseDTO deviceConfigResponseDTO) {
        getDeviceConfigByIdSuccessCounter.increment();
    }

    @Before("execution(* com.brodos.devicecontext.service.DeviceConfigResource.getDeviceConfigByArticleNo(..))")
    public void beforeGetDeviceConfigByArticleNoRequest() {
        getDeviceConfigByArticleNoCounter.increment();
    }

    @AfterReturning(
        value = "execution(* com.brodos.devicecontext.service.DeviceConfigResource.getDeviceConfigByArticleNo(..))",
        returning = "deviceConfigResponseDTO")
    public void afterGetDeviceConfigByArticleNoRequest(DeviceConfigResponseDTO deviceConfigResponseDTO) {
        getDeviceConfigByArticleNoSuccessCounter.increment();
    }
}
