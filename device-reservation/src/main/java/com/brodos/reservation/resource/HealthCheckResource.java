/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brodos.reservation.assembler.ResponseAssembler;
import com.brodos.reservation.component.JmsConfig;
import com.brodos.reservation.dto.response.HealthResponseDTO;
import com.brodos.reservation.infrastructure.HealthCheckRepository;

/**
 *
 * @author padhaval
 */
@RestController
@RequestMapping("/erp-context")
public class HealthCheckResource {

    @Autowired
    HealthCheckRepository healthCheckRepository;

    @Autowired
    ResponseAssembler responseAssembler;

    @Autowired
    JmsConfig jmsConfig;

    @GetMapping("/health")
    @ResponseBody
    @Transactional
    public HealthResponseDTO health() {
        return responseAssembler.toHealthResponseDTO(healthCheckRepository.checkDbHealth()
            && jmsConfig.checkJmsHealth());
    }
}
