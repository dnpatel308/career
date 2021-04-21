/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.configuration;

import com.brodos.reservation.assembler.SerialNumberHistoryAssembler;
import com.brodos.reservation.entity.SerialNumber;
import com.brodos.reservation.entity.SerialNumberHistory;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.infrastructure.SerialNumberHistoryRepository;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author padhaval
 */
@Aspect
@Configuration
public class SerialNumberHistoryAspect {

    @Autowired
    SerialNumberHistoryAssembler serialNumberHistoryAssembler;

    @Autowired
    SerialNumberHistoryRepository serialNumberHistoryRepository;

    @AfterReturning(pointcut = "execution(* com.brodos.reservation.infrastructure.SerialNumberRepository.save(..))",
        returning = "serialNumber")
    public void updateSerialNumberHistory(SerialNumber serialNumber) {
        if (serialNumber != null && serialNumber.hasChanged()) {
            if (serialNumber.getOriginalWarehouseNo() != null && serialNumber.getWarehouseNo() != null
                && !serialNumber.getOriginalWarehouseNo().equals(serialNumber.getWarehouseNo())) {
                SerialNumberHistory serialNumberHistory =
                    serialNumberHistoryAssembler.toSerialNumberHistory(serialNumber);
                serialNumberHistory.setWarehouseNo(serialNumber.getOriginalWarehouseNo());
                serialNumberHistory.setRelocated(Boolean.TRUE);
                serialNumberHistoryRepository.save(serialNumberHistory);
            }

            serialNumberHistoryRepository.save(serialNumberHistoryAssembler.toSerialNumberHistory(serialNumber));
        }
    }

    @AfterReturning(
        pointcut = "execution(* com.brodos.reservation.infrastructure.SerialNumberReservationRepository.save(..))",
        returning = "serialNumberReservation")
    public void updateSerialNumberHistory(SerialNumberReservation serialNumberReservation) {
        if (serialNumberReservation != null && serialNumberReservation.getSerialNumber() != null
            && serialNumberReservation.getSerialNumber().hasChanged()) {
            serialNumberHistoryRepository.save(serialNumberHistoryAssembler
                .toSerialNumberHistory(serialNumberReservation.getSerialNumber()));
        }
    }

    @AfterReturning(
        pointcut = "execution(* com.brodos.reservation.infrastructure.SerialNumberReservationTicketReferenceRepository.save(..))"
            + " || execution(* com.brodos.reservation.infrastructure.SerialNumberImportTicketReferenceRepository.save(..))"
            + " || execution(* com.brodos.reservation.infrastructure.TicketReferenceRepository.save(..))",
        returning = "ticketReference")
    public void
        updateSerialNumberHistory(TicketReference ticketReference) {
        if (ticketReference != null && ticketReference.getSerialNumberReservation() != null
            && ticketReference.getSerialNumberReservation().getSerialNumber() != null
            && ticketReference.getSerialNumberReservation().getSerialNumber().hasChanged()) {
            serialNumberHistoryRepository.save(serialNumberHistoryAssembler.toSerialNumberHistory(ticketReference
                .getSerialNumberReservation().getSerialNumber()));
        }
    }
}
