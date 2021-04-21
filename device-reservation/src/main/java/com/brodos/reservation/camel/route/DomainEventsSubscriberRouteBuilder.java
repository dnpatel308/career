/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.camel.route;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author padhaval
 */
@Component
// @Conditional(VirtualConsumerCondition.class)
public class DomainEventsSubscriberRouteBuilder extends RouteBuilder {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DomainEventsSubscriberRouteBuilder.class);

    private final String uniqueHeaderKey = "${in.header.eventid}";

    @Value("${device.reservation.consumer.queueName}")
    private String queueName;

    @Value("${device.reservation.consumer.transacted}")
    private Boolean transacted;

    @Value("${device.reservation.consumer.processorName}")
    private String processorName;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void postConstruct() {
        LOG.info("initialized={}, {}", this, processorName);
    }

    @Override
    public void configure() throws Exception {
        /*
         * from(
         * "jms:queue:" + queueName + "?messageListenerContainerFactory=#jmsListenerContainerFactory&transacted="
         * + transacted).idempotentConsumer(simple(uniqueHeaderKey),
         * new JdbcMessageIdRepository(dataSource, processorName)).bean(MessageProcessor.class, "processDomainEvent");
         * LOG.info("From Camel DomainEventsSubscriberRoute");
         */
        onException(SQLIntegrityConstraintViolationException.class).handled(true)
            .to("log:Duplicate event for domain event");
        from("jms:queue:" + queueName + "?messageListenerContainerFactory=#jmsCustomListenerContainerFactory")
            .idempotentConsumer(simple(uniqueHeaderKey), new JdbcMessageIdRepository(dataSource, processorName))
            .bean(MessageProcessor.class, "processDomainEvent");
        LOG.info("From Camel DomainEventsSubscriberRoute");
    }
}
