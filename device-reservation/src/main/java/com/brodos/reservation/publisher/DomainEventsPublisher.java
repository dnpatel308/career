/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.publisher;

import com.brodos.reservation.assembler.JmsMessageAssembler;
import com.brodos.reservation.component.JmsConfig;
import com.brodos.reservation.component.MBassadorComponent;
import com.brodos.reservation.entity.DeviceReservationDomainevents;
import com.brodos.reservation.entity.TrackerStore;
import com.brodos.reservation.events.marker.IntegrationEvent;
import com.brodos.reservation.infrastructure.DeviceReservationDomaineventsRepository;
import com.brodos.reservation.infrastructure.TrackerStoreRepository;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author padhaval
 */
@Component
public class DomainEventsPublisher {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DomainEventsPublisher.class);

    @Value("${device.reservation.producer.topicNames}")
    private List<String> topicNames;

    @Autowired
    MBassadorComponent mBassadorComponent;

    @Autowired
    JmsConfig jmsConfig;

    @Autowired
    DeviceReservationDomaineventsRepository deviceReservationDomaineventsRepository;

    @Autowired
    TrackerStoreRepository trackerStoreRepository;

    @Autowired
    JmsMessageAssembler jmsMessageAssembler;

    @Value("${domain.events.publisher.interval}")
    Long domainEventsPublisherInterval;

    @PostConstruct
    public void startDomainEventsPublisher() {
        jmsConfig.getJmsTemplate().setPubSubDomain(true);
        TimerTask publisherTimerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    publishPendingDomainEvents();
                } catch (Exception ex) {
                    LOG.error(ex.getMessage());
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(publisherTimerTask, 0, domainEventsPublisherInterval);
    }

    @Transactional
    private void publishPendingDomainEvents() throws Exception {
        TrackerStore trackerStore = trackerStoreRepository.findByChannelName("device.reservation");
        LOG.debug("trackerStore={}", trackerStore);
        List<DeviceReservationDomainevents> deviceReservationDomaineventsList =
            deviceReservationDomaineventsRepository.findByIdGreaterThanAndOccurredOnLessThan(
                Integer.valueOf(trackerStore.getMostRecentlyPublished()), new Date(System.currentTimeMillis()
                    - domainEventsPublisherInterval));
        if (!deviceReservationDomaineventsList.isEmpty()) {
            LOG.info("Publishing {} pending domain events.", deviceReservationDomaineventsList.size());
            LOG.debug("Publishing events={}", deviceReservationDomaineventsList);
            for (DeviceReservationDomainevents deviceReservationDomainevents : deviceReservationDomaineventsList) {
                IntegrationEvent integrationEvent =
                    Class.forName(deviceReservationDomainevents.getTypeName()).getDeclaredAnnotation(
                        IntegrationEvent.class);
                if (integrationEvent == null) {
                    mBassadorComponent.getMBassador().publish(deviceReservationDomainevents);
                } else {
                    for (String topicName : topicNames) {
                        jmsConfig.getJmsTemplate().send(topicName,
                            jmsMessageAssembler.toActiveMQTextMessageCreator(deviceReservationDomainevents));
                    }
                }
            }

            trackerStore.setMostRecentlyPublished(deviceReservationDomaineventsList
                .get(deviceReservationDomaineventsList.size() - 1).getId().toString());
            trackerStoreRepository.save(trackerStore);
        }
    }
}
