/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.events.handler;

import com.brodos.reservation.component.AsyncComponent;
import com.brodos.reservation.component.MBassadorComponent;
import com.brodos.reservation.entity.DeviceReservationDomainevents;
import com.brodos.reservation.events.Cancelled;
import com.brodos.reservation.events.DomainEventAbstract;
import com.brodos.reservation.events.Imported;
import com.brodos.reservation.events.Pended;
import com.brodos.reservation.events.Opened;
import com.brodos.reservation.events.RequestedForSendout;
import com.brodos.reservation.events.Reserved;
import com.brodos.reservation.events.Sentout;
import com.brodos.reservation.events.filter.ImportedEventFilter;
import com.brodos.reservation.infrastructure.ConfigurationRepository;
import com.brodos.reservation.infrastructure.SerialNumberRepository;
import com.brodos.reservation.service.DeviceReservationHelperService;
import com.brodos.reservation.service.DeviceReservationInternalEventHandlerService;
import com.brodos.reservation.service.EmailService;
import com.brodos.reservation.service.TicketService;
import com.brodos.reservation.service.VoucherService;
import com.brodos.ticket.domain.exception.TicketException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.PostConstruct;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author padhaval
 */
@Component
public class DomainEventsHandler {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DomainEventsHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ticket.maxretrial.count}")
    private int MAX_RETRIAL_COUNT;

    @Autowired
    TicketService ticketService;

    @Autowired
    DeviceReservationInternalEventHandlerService deviceReservationInternalEventHandlerService;

    @Autowired
    SerialNumberRepository serialNumberRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    VoucherService voucherService;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    DeviceReservationHelperService deviceReservationHelperService;

    @Autowired
    AsyncComponent asyncComponent;

    @Autowired
    DomainEventsHandlerHelper domainEventsHandlerHelper;

    @Autowired
    MBassadorComponent mBassadorComponent;

    @PostConstruct
    public void postConstruct() {
        mBassadorComponent.getMBassador().subscribe(this);
    }

    @Handler(filters = @Filter(ImportedEventFilter.class))
    public void subscribeImportedEvent(DeviceReservationDomainevents deviceReservationDomainevents) throws Exception {
        LOG.info("Handling event={}", deviceReservationDomainevents);
        Imported importedEvent = (Imported) deviceReservationDomainevents.extractEventBodyObject();
        domainEventsHandlerHelper.handleImportedEvent(importedEvent);
    }

    @Transactional
    public void handleMessage(ActiveMQTextMessage message) throws Exception {
        DomainEventAbstract domainEventAbstract = null;
        Exception exception = null;
        for (int i = 0; i < MAX_RETRIAL_COUNT; i++) {
            try {
                LOG.info("Handling Integration event,id={}", message.getProperty("eventid"));
                LOG.debug("Handling Integration event,id={}, message={}", message.getProperty("eventid"),
                    message.getText());
                switch (message.getProperty("typename").toString()) {
                    case "com.brodos.reservation.events.Opened": {
                        Opened opened = objectMapper.readValue(message.getText(), Opened.class);
                        domainEventsHandlerHelper.handleOpenedEvent(opened);
                        break;
                    }

                    case "com.brodos.reservation.events.Pended": {
                        domainEventAbstract = objectMapper.readValue(message.getText(), Pended.class);
                        domainEventsHandlerHelper.handlePendingEvent((Pended) domainEventAbstract);
                        break;
                    }

                    case "com.brodos.reservation.events.Reserved": {
                        domainEventAbstract = objectMapper.readValue(message.getText(), Reserved.class);
                        domainEventsHandlerHelper.handleReservedEvent((Reserved) domainEventAbstract);
                        break;
                    }

                    case "com.brodos.reservation.events.Sentout": {
                        domainEventAbstract = objectMapper.readValue(message.getText(), Sentout.class);
                        domainEventsHandlerHelper.handleSentoutEvent((Sentout) domainEventAbstract);
                        break;
                    }

                    case "com.brodos.reservation.events.Cancelled": {
                        domainEventAbstract = objectMapper.readValue(message.getText(), Cancelled.class);
                        domainEventsHandlerHelper.handleCancelledEvent((Cancelled) domainEventAbstract);
                        break;
                    }

                    case "com.brodos.reservation.events.RequestedForSendout": {
                        domainEventAbstract = objectMapper.readValue(message.getText(), RequestedForSendout.class);
                        domainEventsHandlerHelper
                            .handleRequestedForSendoutEvent((RequestedForSendout) domainEventAbstract);
                        break;
                    }

                    default: {
                        LOG.error("Invalid event received={}", message.getProperty("typename").toString());
                        break;
                    }
                }

                break;
            } catch (Exception ex) {
                LOG.error("System Error : while handling integration event {}", ex.getMessage());
                exception = ex;
            }
        }

        if (domainEventAbstract != null && domainEventAbstract.getTicketReference() != null && exception != null
            && (exception instanceof TicketException || ExceptionUtils.hasCause(exception, TicketException.class))) {
            ticketService.setTicketReferenceAsFailed(domainEventAbstract.getTicketReference(), exception);
        } else if (exception != null) {
            throw exception;
        }
    }
}
