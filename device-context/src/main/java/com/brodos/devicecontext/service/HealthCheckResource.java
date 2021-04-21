/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.service;

import com.brodos.devicecontext.service.assembler.ResponseAssembler;
import com.brodos.devicecontext.infrastructure.HealthCheckRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brodos.devicecontext.model.dto.response.HealthResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author padhaval
 */
@RestController
@RequestMapping("/erp-context")
public class HealthCheckResource {

    @Autowired
    ResponseAssembler responseAssembler;

    @Autowired
    HealthCheckRepository healthCheckRepository;

    @GetMapping("/health")
    @ResponseBody
    @Transactional
    public HealthResponseDTO health() {
        return responseAssembler.toHealthResponseDTO(healthCheckRepository.checkDbHealth());
    }
}
