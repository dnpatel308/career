/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brodos.devicecontext.ErrorCodes;
import com.brodos.devicecontext.infrastructure.DeviceFieldRepository;
import com.brodos.devicecontext.infrastructure.DeviceRepository;
import com.brodos.devicecontext.model.dto.DeviceFieldDTO;
import com.brodos.devicecontext.model.dto.request.CreateOrUpdateDeviceDTO;
import com.brodos.devicecontext.model.dto.request.SearchDevicesDTO;
import com.brodos.devicecontext.model.entity.Device;
import com.brodos.devicecontext.model.entity.DeviceField;
import com.brodos.devicecontext.service.assembler.DeviceAssembler;
import com.brodos.devicecontext.service.exception.DeviceContextException;
import java.util.List;

/**
 *
 * @author padhaval
 */
@Service
@Transactional
public class DeviceServiceImpl implements DeviceService {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    DeviceAssembler deviceAssembler;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    DeviceFieldRepository deviceFieldRepository;

    @Override
    public Device createDevice(CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO) {
        return deviceRepository.save(deviceAssembler.toDevice(createOrUpdateDeviceDTO));
    }

    @Override
    public Device getDevice(Long id) {
        LOG.info("Getting device by Id={}", id);
        return deviceRepository.findById(id).orElseThrow(() -> new DeviceContextException(ErrorCodes.DEVICE_NOT_FOUND));
    }

    @Override
    public Device updateDevice(Long id, CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO) {        
        Device requestedDevice = deviceAssembler.toDevice(createOrUpdateDeviceDTO);
        Device existingDevice
                = deviceRepository.findById(id).orElseThrow(() -> new DeviceContextException(ErrorCodes.DEVICE_NOT_FOUND));
        existingDevice.setArticlenumber(requestedDevice.getArticlenumber());
        for (DeviceField deviceField : requestedDevice.getFields()) {
            Optional<DeviceField> existingDeviceField = existingDevice.getFields().stream()
                    .filter((t) -> t.getDeviceFieldPK().getName().equals(deviceField.getDeviceFieldPK().getName()))
                    .findFirst();
            if (existingDeviceField.isPresent()) {
                existingDeviceField.get().setValue(deviceField.getValue());
            } else {
                deviceField.getDeviceFieldPK().setDevice(existingDevice);
                existingDevice.getFields().add(deviceField);
            }
        }

        existingDevice = deviceRepository.save(existingDevice);
        return existingDevice;
    }

    @Override
    public Page<Device> searchDevices(SearchDevicesDTO searchDevicesDTO) {
        LOG.info("Finding reservation by search criteria searchDevicesDTO={}", searchDevicesDTO);
        return deviceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            if (searchDevicesDTO.getFields().getImei1() != null) {
                LOG.info("Preparing criteria for IMEI1 imei1={}", searchDevicesDTO.getFields().getImei1());
                Path<Set<DeviceField>> fieldsPath = root.join("fields");
                Predicate predicateForName
                        = criteriaBuilder.equal(fieldsPath.get("deviceFieldPK").get("name"), "imei1");
                Predicate predicateForValue
                        = fieldsPath.get("value").in(searchDevicesDTO.getFields().getImei1().trim());
                predicates.add(criteriaBuilder.and(predicateForName, predicateForValue));

            }
            if (searchDevicesDTO.getFields().getImei2() != null) {
                LOG.info("Preparing criteria for IMEI2 imei2={}", searchDevicesDTO.getFields().getImei2());
                Path<Set<DeviceField>> fieldsPath = root.join("fields");
                Predicate predicateForName
                        = criteriaBuilder.equal(fieldsPath.get("deviceFieldPK").get("name"), "imei2");
                Predicate predicateForValue
                        = fieldsPath.get("value").in(searchDevicesDTO.getFields().getImei2().trim());
                predicates.add(criteriaBuilder.and(predicateForName, predicateForValue));
            }

            if (searchDevicesDTO.getFields().getSerial() != null) {
                LOG.info("Preparing criteria for isSerial={}", searchDevicesDTO.getFields().getSerial());
                Path<Set<DeviceField>> fieldsPath = root.join("fields");
                Predicate predicateForName
                        = criteriaBuilder.equal(fieldsPath.get("deviceFieldPK").get("name"), "serial");
                Predicate predicateForValue
                        = fieldsPath.get("value").in(searchDevicesDTO.getFields().getSerial().trim());
                predicates.add(criteriaBuilder.and(predicateForName, predicateForValue));
            }

            if (searchDevicesDTO.getArticlenumber() != null) {
                LOG.info("Preparing criteria for Device articleNumber={}", searchDevicesDTO.getArticlenumber().trim());
                predicates
                        .add(criteriaBuilder.equal(root.get("articlenumber"), searchDevicesDTO.getArticlenumber().trim()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(searchDevicesDTO.getPage(), searchDevicesDTO.getSize(), Direction.DESC, "id"));
    }
}
