/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.camel.route;

import com.brodos.reservation.assembler.JmsMessageAssembler;
import com.brodos.reservation.events.handler.DomainEventsHandler;
import com.brodos.reservation.events.handler.VoucherEventHandler;
import java.io.IOException;
import javax.jms.MessageNotWriteableException;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author padhaval
 */
public class MessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

    @Autowired
    private VoucherEventHandler voucherEventHandler;

    @Autowired
    private DomainEventsHandler domainEventsHandler;

    @Autowired
    private JmsMessageAssembler jmsMessageAssembler;

    public void processDomainEvent(Exchange exchange) throws Exception {
        LOGGER.info("exchange.in.headers={}", exchange.getIn().getHeaders());
        LOGGER.info("exchange.in.body={}", exchange.getIn().getBody());
        ActiveMQTextMessage activeMQTextMessage = jmsMessageAssembler.toActiveMQTextMessage(exchange);
        domainEventsHandler.handleMessage(activeMQTextMessage);
    }

    public void processVoucherEvent(Exchange exchange) throws Exception {
        LOGGER.info("exchange.in.headers={}", exchange.getIn().getHeaders());
        LOGGER.info("exchange.in.body={}", exchange.getIn().getBody());
        ActiveMQTextMessage activeMQTextMessage = jmsMessageAssembler.toActiveMQTextMessage(exchange);
        voucherEventHandler.handleMessage(activeMQTextMessage);
    }
}
