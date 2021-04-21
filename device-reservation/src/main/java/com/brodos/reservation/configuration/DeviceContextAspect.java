/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.configuration;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.component.DeviceContextComponent;
import com.brodos.reservation.dto.response.SerialDTO;
import com.brodos.reservation.dto.response.DeviceReservationResponseDTO;
import com.brodos.reservation.dto.response.DeviceReservationsDTO;
import com.brodos.reservation.dto.response.SerialNumberResponseDTO;
import com.brodos.reservation.entity.SerialNumber;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.exception.DeviceReservationException;
import com.fasterxml.jackson.databind.JsonNode;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;

/**
 *
 * @author padhaval
 */
@Aspect
@Configuration
public class DeviceContextAspect {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceContextAspect.class);

    @Autowired
    DeviceContextComponent deviceContextComponent;

    @AfterReturning(
        pointcut = "execution(* com.brodos.reservation.resource.DeviceReservationResource.getDeviceReservationById(..))"
            + " || execution(* com.brodos.reservation.resource.DeviceReservationResource.createDeviceReservation(..))"
            + " || execution(* com.brodos.reservation.resource.DeviceReservationResource.doDeviceReservationAction(..))",
        returning = "deviceReservationResponseDTO")
    public void
        updateDeviceContextInfoInResponse(DeviceReservationResponseDTO deviceReservationResponseDTO) {
        if (deviceReservationResponseDTO.getEmbedded() == null) {
            updateDeviceContextInfo(deviceReservationResponseDTO);
        }
    }

    @AfterReturning(
        pointcut = "execution(* com.brodos.reservation.resource.DeviceReservationResource.getDeviceReservationsBySearchCriteria(..))",
        returning = "deviceReservationsDTO")
    public void
        updateDeviceContextInfoInResponse(DeviceReservationsDTO deviceReservationsDTO) {
        for (DeviceReservationResponseDTO deviceReservationResponseDTO : deviceReservationsDTO.getEmbedded()
            .getReservations()) {
            updateDeviceContextInfo(deviceReservationResponseDTO);
        }
    }

    @Before("execution(* com.brodos.reservation.assembler.DomainEventsAssembler.toImportedEvent(..))"
        + "|| execution(* com.brodos.reservation.assembler.DomainEventsAssembler.toSentoutEvent(..))"
        + "|| execution(* com.brodos.reservation.assembler.DomainEventsAssembler.toReservedEvent(..))"
        + "|| execution(* com.brodos.reservation.assembler.DomainEventsAssembler.toRequestedForSendoutEvent(..))")
    public void updateDeviceContextInfoForEvents(JoinPoint joinPoint) {
        SerialNumberReservation serialNumberReservation = null;
        if (joinPoint.getArgs()[0] instanceof SerialNumberReservation) {
            serialNumberReservation = (SerialNumberReservation) joinPoint.getArgs()[0];
        } else if (joinPoint.getArgs()[0] instanceof TicketReference) {
            serialNumberReservation = ((TicketReference) joinPoint.getArgs()[0]).getSerialNumberReservation();
        }

        if (serialNumberReservation != null && serialNumberReservation.getSerialNumber().getEmbedded() == null) {
            updateDeviceContextInfo(serialNumberReservation.getSerialNumber());
        }
    }

    private void updateDeviceContextInfo(DeviceReservationResponseDTO deviceReservationResponseDTO) {
        try {
            if (!StringUtils.isBlank(deviceReservationResponseDTO.getSerialNo())) {
                JsonNode deviceContextJsonNode =
                    deviceContextComponent.getDeviceContextInfoByArticleNoAndSerialNo(
                        deviceReservationResponseDTO.getArticleNo(), deviceReservationResponseDTO.getSerialNo());
                if (deviceContextComponent.isDeviceInfoValid(deviceContextJsonNode)) {
                    deviceReservationResponseDTO.setEmbedded(deviceContextComponent
                        .getExtractedDeviceInfo(deviceContextJsonNode));
                } else if (deviceReservationResponseDTO.getWarehouseNo() == 2) {
                    deviceReservationResponseDTO.setEmbedded(deviceContextComponent
                        .createDeviceContextInfo(deviceReservationResponseDTO.getSerialNo()));
                } else {
                    throw new DeviceReservationException(ErrorCodes.DEVICE_CONTEXT_ERROR);
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new DeviceReservationException(ErrorCodes.DEVICE_CONTEXT_ERROR);
        }
    }

    private void updateDeviceContextInfo(SerialNumber serialNumber) {
        try {
            JsonNode deviceContextJsonNode =
                deviceContextComponent.getDeviceContextInfoByArticleNoAndSerialNo(serialNumber.getArticle()
                    .getArticleId().getArticleNumber(), serialNumber.getNumber());
            if (deviceContextComponent.isDeviceInfoValid(deviceContextJsonNode)) {
                serialNumber.setEmbedded(deviceContextComponent.getExtractedDeviceInfo(deviceContextJsonNode));
            } else if (serialNumber.getWarehouseNo() == 2) {
                serialNumber.setEmbedded(deviceContextComponent.createDeviceContextInfo(serialNumber.getNumber()));
            } else {
                throw new DeviceReservationException(ErrorCodes.DEVICE_CONTEXT_ERROR);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new DeviceReservationException(ErrorCodes.DEVICE_CONTEXT_ERROR);
        }
    }

    private void updateDeviceContextInfo(SerialDTO serialDTO) {
        try {
            JsonNode deviceContextJsonNode =
                deviceContextComponent.getDeviceContextInfoByArticleNoAndSerialNo(serialDTO.getArticleNumber(),
                    serialDTO.getNumber());
            if (deviceContextComponent.isDeviceInfoValid(deviceContextJsonNode)) {
                serialDTO.setEmbedded(deviceContextComponent.getExtractedDeviceInfo(deviceContextJsonNode));
            } else if (serialDTO.getWarehouseId() == 2) {
                serialDTO.setEmbedded(deviceContextComponent.createDeviceContextInfo(serialDTO.getNumber()));
            } else {
                throw new DeviceReservationException(ErrorCodes.DEVICE_CONTEXT_ERROR);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new DeviceReservationException(ErrorCodes.DEVICE_CONTEXT_ERROR);
        }
    }

    @AfterReturning(
        pointcut = "execution(* com.brodos.reservation.resource.SerialNumberResource.importSerialNumber(..))"
            + " || execution(* com.brodos.reservation.resource.SerialNumberResource.getSerialNumberById(..))"
            + " || execution(* com.brodos.reservation.resource.SerialNumberResource.getSerialNumberByArticleNumberAndImei(..))",
        returning = "serialNumberResponseDTO")
    public void
        updateDeviceContextInfoInResponse(SerialNumberResponseDTO serialNumberResponseDTO) {
        if (serialNumberResponseDTO.getSerialDTO().getEmbedded() == null) {
            updateDeviceContextInfo(serialNumberResponseDTO.getSerialDTO());
        }
    }
}
