/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 
package com.brodos.reservation.subscriber;

import com.brodos.reservation.camel.route.MessageProcessor;
import com.brodos.reservation.configuration.NormalConsumerCondition;
import com.brodos.reservation.events.handler.VoucherEventHandler;

import java.sql.SQLIntegrityConstraintViolationException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

*//**
 *
 * @author padhaval
 *//*
@Component
@Conditional(NormalConsumerCondition.class)
public class VoucherEventsSubscriber extends RouteBuilder {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(VoucherEventsSubscriber.class);

    @Autowired
    VoucherEventHandler voucherEventHandler;

    @Value("${voucher.import.consumer.topicName}")
    private String topicName;

    private final String uniqueHeaderKey = "${in.header.eventid}";

    @Value("${voucher.import.consumer.processorName}")
    private String processorName;

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void postConstruct() {
        LOG.info("initialized={}", this);
    }

    @Value("${voucher.import.consumer.transacted}")
    private Boolean transacted;

    // @JmsListener(destination = "${voucher.import.consumer.topicName}", id = "${voucher.import.consumer.clientId}",
    // subscription = "${voucher.import.consumer.durableSubscriptionName}",
    // containerFactory = "jmsListenerContainerFactory")
    // public void subscribeReservationEvent(ActiveMQTextMessage message) {
    // voucherEventHandler.handleMessage(message);
    // }

    @Override
    public void configure() throws Exception {
        onException(SQLIntegrityConstraintViolationException.class).handled(true).to(
            "log:Cannot process this message from Topic due to already consumed from VoucherTopic queue.");
        from(
            "jms:topic:" + topicName + "?messageListenerContainerFactory=#jmsListenerContainerFactory&transacted="
                + transacted).idempotentConsumer(simple(uniqueHeaderKey),
            new JdbcMessageIdRepository(dataSource, processorName)).bean(MessageProcessor.class, "processVoucherEvent");
        LOG.info("From Camel VoucherEventsSubscriber");

    }
}*/