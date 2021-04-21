/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.configuration;

import static com.brodos.reservation.Constants.TENANT_ID;
import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.dto.request.AddressType;
import com.brodos.reservation.dto.request.DeviceReservationActionDTO;
import com.brodos.reservation.dto.request.DeviceReservationRequestDTO;
import com.brodos.reservation.entity.ArticleId;
import com.brodos.reservation.entity.TenantId;
import com.brodos.reservation.exception.DeviceReservationException;
import com.brodos.reservation.infrastructure.ArticleRepository;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author padhaval
 */
@Aspect
@Configuration
public class RequestValidationAspect {

    private static final Logger LOG = LoggerFactory.getLogger(RequestValidationAspect.class);

    @Autowired
    ArticleRepository articleRepository;

    @Value("${warehouse.group}")
    private String warehouseGroup;

    @Before("execution(* com.brodos.reservation.resource.DeviceReservationResource.createDeviceReservation(..))")
    public void validateDeviceReservationRequest(JoinPoint joinPoint) {
        DeviceReservationRequestDTO deviceReservationRequestDTO = (DeviceReservationRequestDTO) joinPoint.getArgs()[1];
        if (StringUtils.isBlank(deviceReservationRequestDTO.getGroup())) {
            deviceReservationRequestDTO.setGroup(warehouseGroup);
        }

        LOG.debug("deviceReservationRequestDTO={}", deviceReservationRequestDTO);

        if (!StringUtils.isBlank(deviceReservationRequestDTO.getArticleNo())) {
            articleRepository
                .findById(new ArticleId(deviceReservationRequestDTO.getArticleNo(), new TenantId(TENANT_ID)))
                .orElseThrow(() -> new DeviceReservationException(ErrorCodes.ARTICLE_NOT_FOUND));
        }
    }

    @Before("execution(* com.brodos.reservation.resource.DeviceReservationResource.doDeviceReservationAction(..))")
    public void validateDoDeviceReservationAction(JoinPoint joinPoint) {
        Long id = (Long) joinPoint.getArgs()[1];
        DeviceReservationActionDTO deviceReservationActionDTO = (DeviceReservationActionDTO) joinPoint.getArgs()[2];
        LOG.info("Validating request for {} request, for reservationId={}", deviceReservationActionDTO.getType(), id);
        LOG.debug("Validating request for {} request, for reservationId={} and DTO={}",
            deviceReservationActionDTO.getType(), id, deviceReservationActionDTO);
        switch (deviceReservationActionDTO.getType().toLowerCase()) {
            case "request-sendout": {
                if (deviceReservationActionDTO.getArguments() == null) {
                    throw new DeviceReservationException(ErrorCodes.MISSING_ARGUMENTS);
                }

                String addressType = deviceReservationActionDTO.getArguments().getAddressType();
                if (StringUtils.isBlank(addressType)
                    || !Arrays.asList(AddressType.values()).contains(AddressType.valueOf(addressType.toUpperCase()))) {
                    throw new DeviceReservationException(ErrorCodes.INVALID_ADDRESS_TYPE);
                }

                if (addressType.equalsIgnoreCase(AddressType.CUSTOMER.name())
                    && deviceReservationActionDTO.getArguments().getAddress() == null) {
                    throw new DeviceReservationException(ErrorCodes.MISSING_ADDRESS);
                }

                deviceReservationActionDTO.getArguments().getAddress().validate(addressType);
                break;
            }

            case "cancel": {
                break;
            }
            case "cancel-and-archive": {
                break;
            }

            default: {
                throw new DeviceReservationException(ErrorCodes.INVALID_ACTION_REQUESTED);
            }
        }
    }
}
