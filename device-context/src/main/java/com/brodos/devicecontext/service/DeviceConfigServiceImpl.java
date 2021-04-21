package com.brodos.devicecontext.service;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brodos.devicecontext.ErrorCodes;
import com.brodos.devicecontext.infrastructure.DeviceConfigRepository;
import com.brodos.devicecontext.infrastructure.DeviceFieldConfigRepository;
import com.brodos.devicecontext.model.dto.request.CreateOrUpdateDeviceConfigDTO;
import com.brodos.devicecontext.model.entity.DeviceConfig;
import com.brodos.devicecontext.model.entity.DeviceFieldConfig;
import com.brodos.devicecontext.service.assembler.DeviceAssembler;
import com.brodos.devicecontext.service.exception.DeviceContextException;

@Service
public class DeviceConfigServiceImpl implements DeviceConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceConfigServiceImpl.class);

    @Autowired
    DeviceAssembler deviceAssembler;

    @Autowired
    DeviceConfigRepository deviceConfigRepository;

    @Autowired
    DeviceFieldConfigRepository deviceFieldConfigRepository;

    @Override
    public DeviceConfig createDeviceConfig(CreateOrUpdateDeviceConfigDTO createOrUpdateDeviceConfigDTO) {
        LOG.info("Creating/Updating configuration={}", createOrUpdateDeviceConfigDTO);
        DeviceConfig deviceConfig = deviceAssembler.toDeviceConfigs(createOrUpdateDeviceConfigDTO);
        DeviceConfig existingDeviceConfig = deviceConfigRepository.findByArticlenumber(deviceConfig.getArticlenumber());
        if (existingDeviceConfig != null) {
            for (DeviceFieldConfig deviceFieldConfig : deviceConfig.getDeviceFieldConfigs()) {
                Optional<DeviceFieldConfig> existingDeviceConfigField = existingDeviceConfig.getDeviceFieldConfigs()
                    .stream().filter((t) -> t.getName().equals(deviceFieldConfig.getName())).findFirst();
                if (existingDeviceConfigField.isPresent()) {
                    existingDeviceConfigField.get().setType(deviceFieldConfig.getType());
                } else {
                    deviceFieldConfig.setDeviceConfig(existingDeviceConfig);
                    existingDeviceConfig.getDeviceFieldConfigs().add(deviceFieldConfig);
                }
            }
            //To remove device field config
            Set<DeviceFieldConfig> deviceFieldConfigs = new CopyOnWriteArraySet<>();
            deviceFieldConfigs.addAll(existingDeviceConfig.getDeviceFieldConfigs());
            Iterator<DeviceFieldConfig> deviceConfigIterator = deviceFieldConfigs.iterator();
            while (deviceConfigIterator.hasNext()) {
                DeviceFieldConfig deviceFieldConfig = deviceConfigIterator.next();
                Optional<DeviceFieldConfig> existingDeviceConfigField = deviceConfig.getDeviceFieldConfigs().stream()
                    .filter((t) -> t.getName().equals(deviceFieldConfig.getName())).findFirst();
                if (!existingDeviceConfigField.isPresent()) {
                    existingDeviceConfig.getDeviceFieldConfigs().remove(deviceFieldConfig);
                    deviceFieldConfigRepository.deleteById(deviceFieldConfig.getId());
                }
            }
            return deviceConfigRepository.save(existingDeviceConfig);
        } else {
            return deviceConfigRepository.save(deviceConfig);
        }

    }

    @Override
    public DeviceConfig getDeviceConfig(Long id) {
        LOG.info("Getting device configuration by Id={}", id);
        return deviceConfigRepository.findById(id)
            .orElseThrow(() -> new DeviceContextException(ErrorCodes.DEVICE_CONFIG_NOT_FOUND));
    }

    @Override
    public DeviceConfig getByArticlenumber(String articlenumber) {
        LOG.info("Getting device configuration by articlenumber={}", articlenumber);
        return deviceConfigRepository.findByArticlenumber(articlenumber);
    }
}
