/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.configuration;

import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.events.DomainEventAbstract;
import com.brodos.reservation.infrastructure.SerialNumberReservationRepository;
import com.brodos.reservation.infrastructure.TicketReferenceRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author padhaval
 */
@Aspect
@Configuration
public class DomainEventsHandlerHelperAspect {

    private static final Logger LOG = LoggerFactory.getLogger(DomainEventsHandlerHelperAspect.class);

    @Autowired
    SerialNumberReservationRepository serialNumberReservationRepository;

    @Autowired
    TicketReferenceRepository ticketReferenceRepository;

    @Before("execution(* com.brodos.reservation.events.handler.DomainEventsHandlerHelper.*(..))")
    public void validateDeviceReservationRequest(JoinPoint joinPoint) {
        DomainEventAbstract domainEventAbstract = (DomainEventAbstract) joinPoint.getArgs()[0];
        SerialNumberReservation serialNumberReservation =
            serialNumberReservationRepository.findById(domainEventAbstract.getReservationId()).get();
        TicketReference ticketReference =
            ticketReferenceRepository.findFirstBySerialNumberReservationOrderByIdDesc(serialNumberReservation);
        domainEventAbstract.setTicketReference(ticketReference);
        LOG.debug("domainEventAbstract={}", domainEventAbstract);
    }
}
