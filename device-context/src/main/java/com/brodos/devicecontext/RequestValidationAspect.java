/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext;

import com.brodos.devicecontext.infrastructure.DeviceFieldRepository;
import com.brodos.devicecontext.infrastructure.DeviceRepository;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.brodos.devicecontext.model.dto.DeviceFieldDTO;
import com.brodos.devicecontext.model.dto.request.CreateOrUpdateDeviceConfigDTO;
import com.brodos.devicecontext.model.dto.request.CreateOrUpdateDeviceDTO;
import com.brodos.devicecontext.model.entity.Device;
import com.brodos.devicecontext.model.entity.DeviceConfig;
import com.brodos.devicecontext.model.entity.DeviceField;
import com.brodos.devicecontext.model.entity.DeviceFieldConfig;
import com.brodos.devicecontext.service.DeviceConfigService;
import com.brodos.devicecontext.service.exception.DeviceContextException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
@Aspect
@Configuration
public class RequestValidationAspect {

    private static final Logger LOG = LoggerFactory.getLogger(RequestValidationAspect.class);

    @Autowired
    DeviceFieldRepository deviceFieldRepository;

    @Autowired
    DeviceConfigService deviceConfigService;

    @Autowired
    DeviceRepository deviceRepository;

    @Before("execution(* com.brodos.devicecontext.service.DeviceResource.createDevice(..))")
    public void validateCreateDevice(JoinPoint joinPoint) {
        CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO = (CreateOrUpdateDeviceDTO) joinPoint.getArgs()[1];
        DeviceConfig deviceConfig = deviceConfigService.getByArticlenumber(createOrUpdateDeviceDTO.getArticleNo());
        validateDeviceConfig(createOrUpdateDeviceDTO, deviceConfig);

        if (createOrUpdateDeviceDTO.getFields().stream().noneMatch(f -> f.getName().equals("contract_key"))) {
            DeviceFieldDTO deviceFieldDTO = new DeviceFieldDTO();
            deviceFieldDTO.setName("contract_key");
            deviceFieldDTO.setType("string(40)");
            deviceFieldDTO.setValue("def");
            createOrUpdateDeviceDTO.getFields().add(deviceFieldDTO);
        }

        for (DeviceFieldDTO deviceFieldDTO : createOrUpdateDeviceDTO.getFields()) {
            if (deviceFieldDTO.getValue() == null) {
                deviceFieldDTO.setValue("");
            }
        }
    }

    private void validateDeviceConfig(CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO, DeviceConfig deviceConfig) {
        if (deviceConfig == null) {
            throw new DeviceContextException(ErrorCodes.DEVICE_CONFIG_NOT_FOUND);
        }

        for (DeviceFieldDTO deviceFieldDTO : createOrUpdateDeviceDTO.getFields()) {
            Optional<DeviceFieldConfig> deviceConfigField = deviceConfig.getDeviceFieldConfigs().stream()
                .filter((t) -> t.getName().equals(deviceFieldDTO.getName())).findFirst();

            if (!deviceFieldDTO.getName().equalsIgnoreCase("contract_key") && !deviceConfigField.isPresent()) {
                throw new DeviceContextException(ErrorCodes.DEVICE_CONFIG_INCOMPLETE);
            }
        }
    }

    @Before("execution(* com.brodos.devicecontext.service.DeviceConfigResource.createDeviceConfig(..))")
    public void validateCreateDeviceConfig(JoinPoint joinPoint) {
        CreateOrUpdateDeviceConfigDTO createOrUpdateDeviceConfigDTO =
            (CreateOrUpdateDeviceConfigDTO) joinPoint.getArgs()[1];
        if (createOrUpdateDeviceConfigDTO.getFields() == null || createOrUpdateDeviceConfigDTO.getFields().isEmpty()) {
            throw new DeviceContextException(ErrorCodes.INVALID_DEVICE_CONFIGURATION);
        }
    }

    @Before("execution(* com.brodos.devicecontext.service.DeviceResource.updateDevice(..))")
    public void validateUpdateDevice(JoinPoint joinPoint) {
        CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO = (CreateOrUpdateDeviceDTO) joinPoint.getArgs()[2];
        DeviceConfig deviceConfig = deviceConfigService.getByArticlenumber(createOrUpdateDeviceDTO.getArticleNo());
        validateDeviceConfig(createOrUpdateDeviceDTO, deviceConfig);
        Long id = (Long) joinPoint.getArgs()[1];
        Device existingDevice =
            deviceRepository.findById(id).orElseThrow(() -> new DeviceContextException(ErrorCodes.DEVICE_NOT_FOUND));

        if (!existingDevice.getArticlenumber().equalsIgnoreCase(createOrUpdateDeviceDTO.getArticleNo())) {
            DeviceContextException e = new DeviceContextException(ErrorCodes.DEVICE_EXIST_WITH_DEFFERENT_ARTICLE);
            LOG.error(e.getMessage(), e);
            throw e;
        }

        LOG.info("Updating device{}", existingDevice.getId());
        Set<DeviceFieldDTO> fields = createOrUpdateDeviceDTO.getFields();
        if (fields != null) {
            for (DeviceFieldDTO deviceFieldDTO : fields) {
                if (deviceFieldDTO.getName().equals("imei1") && !StringUtils.isEmpty(deviceFieldDTO.getValue())) {
                    List<DeviceField> deviceFields = deviceFieldRepository.findByValue(deviceFieldDTO.getValue());
                    for (DeviceField deviceField : deviceFields) {
                        if (!deviceField.getDeviceFieldPK().getDevice().getId().equals(existingDevice.getId())
                            && deviceField.getDeviceFieldPK().getDevice().getArticlenumber()
                                .equals(existingDevice.getArticlenumber())) {
                            throw new DeviceContextException(ErrorCodes.VALUE_ALREADY_ASSOCIATED,
                                deviceField.getValue());
                        }
                    }
                }
            }
        }
    }
}
