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
public class VoucherEventsSubscriberRouteBuilder extends RouteBuilder {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(VoucherEventsSubscriberRouteBuilder.class);

    private final String uniqueHeaderKey = "${in.header.eventid}";

    @Value("${voucher.import.consumer.queueName}")
    private String queueName;

    @Value("${voucher.import.consumer.transacted}")
    private Boolean transacted;

    @Value("${voucher.import.consumer.processorName}")
    private String processorName;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void postConstruct() {
        LOG.info("initialized={}, {}", this, processorName);
    }

    @Override
    public void configure() throws Exception {
        onException(SQLIntegrityConstraintViolationException.class).handled(true)
            .to("log:Duplicate event for voucher event");
        from("jms:queue:" + queueName
            + "?messageListenerContainerFactory=#jmsCustomListenerContainerFactory&transacted=" + transacted)
                .idempotentConsumer(simple(uniqueHeaderKey), new JdbcMessageIdRepository(dataSource, processorName))
                .bean(MessageProcessor.class, "processVoucherEvent");
        LOG.info("From Camel DomainEventsSubscriberRoute");

        // @JmsListener(destination = "${voucher.import.consumer.topicName}", id =
        // "${voucher.import.consumer.clientId}",
        // subscription = "${voucher.import.consumer.durableSubscriptionName}",
        // containerFactory = "jmsListenerContainerFactory")
        // public void subscribeReservationEvent(ActiveMQTextMessage message) {
        // voucherEventHandler.handleMessage(message);
        // }
    }
}
