/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.brodos.devicecontext.infrastructure.DeviceRepository;
import com.brodos.devicecontext.model.entity.Device;
import com.brodos.devicecontext.model.entity.DeviceConfig;
import com.brodos.devicecontext.model.entity.DeviceField;
import com.brodos.devicecontext.model.entity.DeviceFieldConfig;
import com.brodos.devicecontext.model.entity.DeviceFieldPK;
import com.brodos.devicecontext.model.entity.DeviceStatus;
import com.brodos.devicecontext.service.DeviceConfigService;
import com.brodos.devicecontext.service.exception.DeviceContextException;

/**
 *
 * @author padhaval
 */
@Aspect
@Configuration
public class DeviceRepositoryAspect {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceRepositoryAspect.class);

    @Autowired
    DeviceConfigService deviceConfigService;

    @Autowired
    DeviceRepository deviceRepository;

    @Before("execution(* com.brodos.devicecontext.infrastructure.DeviceRepository.save(..))")
    public void updateDeviceStatus(JoinPoint joinPoint) {
        Device device = (Device) joinPoint.getArgs()[0];
        // If deviceid is null then we will insert otherwise we will update
        if (device.getId() == null) {
            // Check for the uniqueness and throw the exception when we try to import into unique fields
            LOG.info("Check for unique insert fields");
            checkForUniqueInsertFields(device);
        } else {
            LOG.info("Check for unique update fields");
            checkForUniqueUpdateFields(device);
        }

        device.setStatus(DeviceStatus.COMPLETE);
        if (device.getFields() == null || device.getFields().isEmpty()) {
            throw new DeviceContextException(ErrorCodes.INVALID_DEVICE_INFORMATION);
        }

        DeviceConfig deviceConfig = deviceConfigService.getByArticlenumber(device.getArticlenumber());
        for (DeviceFieldConfig deviceFieldConfig : deviceConfig.getDeviceFieldConfigs()) {
            if (deviceFieldConfig.getName().equals("contract_key") && device.getFields().stream()
                .noneMatch(f -> f.getDeviceFieldPK().getName().equals(deviceFieldConfig.getName()))) {
                DeviceField deviceField = new DeviceField();
                DeviceFieldPK deviceFieldPK = new DeviceFieldPK();
                deviceFieldPK.setName("contract_key");
                deviceField.setDeviceFieldPK(deviceFieldPK);
                deviceField.setValue("def");
                deviceFieldPK.setDevice(device);
                device.getFields().add(deviceField);
            }
            if (device.getFields().stream()
                .noneMatch(f -> f.getDeviceFieldPK().getName().equals(deviceFieldConfig.getName()))) {
                device.setStatus(DeviceStatus.INCOMPLETE);
            }

            for (DeviceField deviceField : device.getFields()) {
                if (!deviceField.getDeviceFieldPK().getName().equals("contract_key")
                    && StringUtils.isEmpty(deviceField.getValue())) {
                    device.setStatus(DeviceStatus.INCOMPLETE);
                    break;
                }
            }
        }
        LOG.info("Created device for articleNo={}", device.getArticlenumber());
    }

    @AfterReturning(pointcut = "execution(* com.brodos.devicecontext.infrastructure.DeviceRepository.save(..))",
        returning = "device")
    public void updateDeviceConfig(Device device) {
        DeviceConfig deviceConfig = deviceConfigService.getByArticlenumber(device.getArticlenumber());
        device.setDeviceConfig(deviceConfig);
    }

    /*
     * If imei1 or imei2 or serial of the given device
     * is already used return false otherwise true
     */
    private void checkForUniqueInsertFields(Device device) {
        List<Device> devices = getUniqueDevices(device);
        if (!devices.isEmpty()) {
            throw new DeviceContextException(ErrorCodes.VALUE_NOT_UNIQUE);
        }
    }

    /*
     * If imei1 or imei2 or serial of the given device
     * is already used then is will throw value not unique exception.
     */
    private void checkForUniqueUpdateFields(Device device) {
        List<Device> devices = getUniqueDevices(device);
        // Check if there is only one device if so check if the device is the one that will be updated
        // if the id is not matching then there is already different device with unique value there
        for (Device d : devices) {
            if (d.getId() != device.getId()) {
                throw new DeviceContextException(ErrorCodes.VALUE_NOT_UNIQUE);
            }
        }
    }

    /*
     * If there are unique values in the given device
     * this method will return a list of all devices
     * in the database that contain those values
     */
    private List<Device> getUniqueDevices(Device device) {
        String imei1 = null;
        String imei2 = null;
        String serial = null;
        String articleNo = device.getArticlenumber();
        Set<DeviceField> deviceFields = device.getFields();
        for (DeviceField df : deviceFields) {
            String name = df.getDeviceFieldPK().getName();
            if (name.equals("imei1")) {
                imei1 = df.getValue();
            } else if (name.equals("imei2")) {
                imei2 = df.getValue();
            } else if (name.equals("serial")) {
                serial = df.getValue();
            }
        }
        LOG.debug("imei1={},imei2={} and Serial={}", imei1, imei2, serial);
        if (imei1 != null && imei2 != null && !imei1.trim().isEmpty() && !imei2.trim().isEmpty()) {
            if (imei1.equalsIgnoreCase(imei2)) {
                throw new DeviceContextException(ErrorCodes.VALUE_NOT_UNIQUE);
            }
        }
        List<Device> devices = new ArrayList<>();

        if (imei1 != null) {
            devices.addAll(getDeviceByArticleNoAndImei(articleNo, imei1));
            LOG.info("Device By imei1={}", devices);
        }

        if (imei2 != null) {
            devices.addAll(getDeviceByArticleNoAndImei(articleNo, imei2));
            LOG.info("Device By imei2={}", devices);
        }

        if (serial != null) {
            devices.addAll(getDeviceByArticleNoAndSerial(articleNo, serial));
            LOG.info("Device By serial={}", devices);
        }
        return devices;
    }

    private List<Device> getDeviceByArticleNoAndImei(String articleNo, String imei) {
        return deviceRepository.findDeviceByArticleNoAndImei(articleNo, imei);
    }

    private List<Device> getDeviceByArticleNoAndSerial(String articleNo, String serial) {
        return deviceRepository.findDeviceByArticleNoAndSerial(articleNo, serial);
    }
}
