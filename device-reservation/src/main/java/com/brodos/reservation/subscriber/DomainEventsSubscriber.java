/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 
package com.brodos.reservation.subscriber;

import com.brodos.reservation.camel.route.MessageProcessor;
import com.brodos.reservation.configuration.NormalConsumerCondition;
import com.brodos.reservation.events.handler.DomainEventsHandler;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

*//**
 *
 * @author padhaval
 *//*
@Component
@Conditional(NormalConsumerCondition.class)
public class DomainEventsSubscriber extends RouteBuilder {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DomainEventsSubscriber.class);

    private final String uniqueHeaderKey = "${in.header.eventid}";

    @Value("${device.reservation.consumer.topicName}")
    private String topicName;

    @Value("${device.reservation.consumer.processorName}")
    private String processorName;

    @Autowired
    private DataSource dataSource;

    @Autowired
    DomainEventsHandler domainEventsHandler;

    @PostConstruct
    public void postConstruct() {
        LOG.info("initialized={}, {}", this, processorName);
    }

    @Override
    public void configure() throws Exception {
        onException(SQLIntegrityConstraintViolationException.class).handled(true).to(
            "log:Cannot process this message from Topic due to already consumed from Domain VirtualTopic queue.");
        from("jms:topic:" + topicName + "?messageListenerContainerFactory=#jmsListenerContainerFactory")
            .idempotentConsumer(simple(uniqueHeaderKey), new JdbcMessageIdRepository(dataSource, processorName)).bean(
                MessageProcessor.class, "processDomainEvent");
        LOG.info("From Camel DomainEventsSubscriber");
    }
}
*/