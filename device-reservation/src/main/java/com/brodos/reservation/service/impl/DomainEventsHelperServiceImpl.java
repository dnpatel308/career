/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service.impl;

import com.brodos.reservation.assembler.DomainEventsAssembler;
import com.brodos.reservation.dto.request.DeviceReservationActionDTO;
import com.brodos.reservation.dto.request.DeviceReservationRequestDTO;
import com.brodos.reservation.entity.DeviceReservationDomainevents;
import com.brodos.reservation.entity.SerialNumberImportTicketReference;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.SerialNumberReservationTicketReference;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.events.Opened;
import com.brodos.reservation.infrastructure.DeviceReservationDomaineventsRepository;
import com.brodos.reservation.service.DomainEventsHelperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author padhaval
 */
@Service
public class DomainEventsHelperServiceImpl implements DomainEventsHelperService {

    @Autowired
    DomainEventsAssembler domainEventsAssembler;

    @Autowired
    DeviceReservationDomaineventsRepository deviceReservationDomaineventsRepository;

    @Override
    public void createAndStoreOpenedEvent(SerialNumberReservation serialNumberReservation,
        DeviceReservationRequestDTO deviceReservationRequestDTO) {
        DeviceReservationDomainevents deviceReservationDomainevents =
            deviceReservationDomaineventsRepository.save(domainEventsAssembler.toOpenedEvent(
                serialNumberReservation.getId(), deviceReservationRequestDTO));
        serialNumberReservation.setDomainEvent((Opened) deviceReservationDomainevents.extractEventBodyObject());
    }

    @Override
    public void createAndStoreReservedEvent(
        SerialNumberReservationTicketReference serialNumberReservationTicketReference) {
        deviceReservationDomaineventsRepository.save(domainEventsAssembler
            .toReservedEvent(serialNumberReservationTicketReference));
    }

    @Override
    public void createAndStorePendingEvent(SerialNumberImportTicketReference serialNumberImportTicketReference) {
        deviceReservationDomaineventsRepository.save(domainEventsAssembler
            .toPendingEvent(serialNumberImportTicketReference));
    }

    @Override
    public void createAndStoreSentoutEvent(SerialNumberReservation serialNumberReservation) {
        deviceReservationDomaineventsRepository.save(domainEventsAssembler.toSentoutEvent(serialNumberReservation));
    }

    @Override
    public void createAndStoreCancelledEvent(TicketReference ticketReference) {
        deviceReservationDomaineventsRepository.save(domainEventsAssembler.toCancelledEvent(ticketReference));
    }

    @Override
    public void createAndStoreImportedEvent(SerialNumberReservation serialNumberReservation) {
        deviceReservationDomaineventsRepository.save(domainEventsAssembler.toImportedEvent(serialNumberReservation));
    }

    @Override
    public void createAndStoreRequestForSentoutEvent(SerialNumberReservation serialNumberReservation,
        DeviceReservationActionDTO deviceReservationActionDTO) {
        deviceReservationDomaineventsRepository.save(domainEventsAssembler.toRequestedForSendoutEvent(
            serialNumberReservation, deviceReservationActionDTO));
    }
}
