/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.events.handler;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import com.brodos.reservation.entity.DeviceReservationDomainevents;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.events.Opened;
import com.brodos.reservation.infrastructure.DeviceReservationDomaineventsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 *
 * @author padhaval
 */
// @Component
@Transactional
public class FailedDomainEventsHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FailedDomainEventsHandler.class);

    @Value("${failed.domain.events.handler.interval}")
    public Long failedDomainEventsHandlerInterval;

    @Value("${failed.events.older.than.seconds}")
    public Long failedEventsOlderThanSeconds;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    DomainEventsHandlerHelper domainEventsHandlerHelper;

    @Autowired
    DeviceReservationDomaineventsRepository deviceReservationDomaineventsRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<SerialNumberReservation> findOpenSerialNumberReservations() {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder
            .append(
                "SELECT * FROM `serial_number_reservation` snr WHERE STATUS = 'OPEN' AND `reservation_time` < NOW() - INTERVAL ")
            .append(failedEventsOlderThanSeconds).append(" SECOND");
        LOG.debug("Fetching open reservations, {}", queryBuilder);
        return entityManager.createNativeQuery(queryBuilder.toString(), SerialNumberReservation.class).getResultList();
    }

    public void findAndHandleFailedOpenedEvents() {
        List<SerialNumberReservation> serialNumberReservations = findOpenSerialNumberReservations();
        if (!serialNumberReservations.isEmpty()) {
            LOG.info("Handling {} open reservation events", serialNumberReservations.size());
            for (SerialNumberReservation serialNumberReservation : serialNumberReservations) {
                try {
                    DeviceReservationDomainevents deviceReservationDomainevents =
                        deviceReservationDomaineventsRepository.findByReservationIdAndStatus(
                            serialNumberReservation.getId(), "OPEN");
                    Opened opened = objectMapper.readValue(deviceReservationDomainevents.getEventBody(), Opened.class);
                    LOG.info("Handling open reservation event, eventId={}, reservationId={}, status={}",
                        deviceReservationDomainevents.getId(), opened.getReservationId(), opened.getStatus());
                    domainEventsHandlerHelper.handleOpenedEvent(opened);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage());
                }
            }
        }
    }

    @PostConstruct
    public void startFailedDomainEventsHandler() {
        TimerTask publisherTimerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    findAndHandleFailedOpenedEvents();
                } catch (Exception ex) {
                    LOG.error(ex.getMessage());
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(publisherTimerTask, 0, failedDomainEventsHandlerInterval);
    }
}
