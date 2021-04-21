/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext;

import com.brodos.devicecontext.service.assembler.DeviceAssembler;
import com.brodos.devicecontext.service.assembler.ResponseAssembler;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author padhaval
 */
@Configuration
public class DeviceContextApplicationConfig {

    @Bean
    public ResponseAssembler responseAssembler() {
        return new ResponseAssembler();
    }

    @Bean
    public DeviceAssembler deviceAssembler() {
        return new DeviceAssembler();
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
