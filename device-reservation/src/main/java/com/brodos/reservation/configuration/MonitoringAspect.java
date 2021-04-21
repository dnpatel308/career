/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author padhaval
 */
@Aspect
@Configuration
public class MonitoringAspect {

    private static final Logger LOG = LoggerFactory.getLogger(MonitoringAspect.class);

    private final Counter openedEventCounter;
    private final Counter reservedEventCounter;
    private final Counter pendedEventCounter;
    private final Counter sentoutEventCounter;
    private final Counter cancelledEventCounter;
    private final Counter importedEventCounter;
    private final Counter requestedForSendoutEventCounter;

    public MonitoringAspect(MeterRegistry meterRegistry) {
        LOG.info("meterRegistry={}", meterRegistry);
        openedEventCounter = meterRegistry.counter("com.brodos.reservation.events.Opened");
        reservedEventCounter = meterRegistry.counter("com.brodos.reservation.events.Reserved");
        pendedEventCounter = meterRegistry.counter("com.brodos.reservation.events.Pended");
        sentoutEventCounter = meterRegistry.counter("com.brodos.reservation.events.Sentout");
        cancelledEventCounter = meterRegistry.counter("com.brodos.reservation.events.Cancelled");
        importedEventCounter = meterRegistry.counter("com.brodos.reservation.events.Imported");
        requestedForSendoutEventCounter = meterRegistry.counter("com.brodos.reservation.events.RequestedForSendout");
    }

    @Before("execution(* com.brodos.reservation.service.DomainEventsHelperService.*(..))")
    public void updatePrometheusMetricsForEvents(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        LOG.debug("Updating prometheus metrics, name={}", name);
        switch (name) {
            case "createAndStoreOpenedEvent": {
                openedEventCounter.increment();
                break;
            }

            case "createAndStoreReservedEvent": {
                reservedEventCounter.increment();
                break;
            }

            case "createAndStorePendingEvent": {
                pendedEventCounter.increment();
                break;
            }

            case "createAndStoreSentoutEvent": {
                sentoutEventCounter.increment();
                break;
            }

            case "createAndStoreCancelledEvent": {
                cancelledEventCounter.increment();
                break;
            }

            case "createAndStoreImportedEvent": {
                importedEventCounter.increment();
                break;
            }

            case "createAndStoreRequestForSentoutEvent": {
                requestedForSendoutEventCounter.increment();
                break;
            }
        }
    }
}
