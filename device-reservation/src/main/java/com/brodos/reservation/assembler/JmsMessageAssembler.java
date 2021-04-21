/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.assembler;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.entity.DeviceReservationDomainevents;
import com.brodos.reservation.exception.DeviceReservationException;
import java.io.IOException;
import javax.jms.MessageNotWriteableException;
import javax.jms.Session;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.camel.Exchange;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.MessageCreator;

/**
 *
 * @author padhaval
 */
public class JmsMessageAssembler {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(JmsMessageAssembler.class);

    public MessageCreator toActiveMQTextMessageCreator(DeviceReservationDomainevents deviceReservationDomainevents) throws IOException, MessageNotWriteableException {
        return (Session session) -> {
            try {
                ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) session.createTextMessage();
                activeMQTextMessage.setProperty("eventid", deviceReservationDomainevents.getId());
                activeMQTextMessage.setProperty("typename", deviceReservationDomainevents.getTypeName());
                activeMQTextMessage.setProperty("occurredon", deviceReservationDomainevents.getOccurredOn().getTime());
                activeMQTextMessage.setProperty("version", deviceReservationDomainevents.getVersion());
                activeMQTextMessage.setProperty("group", deviceReservationDomainevents.getGroup());
                activeMQTextMessage.setText(deviceReservationDomainevents.getEventBody());
                return activeMQTextMessage;
            } catch (IOException ex) {
                LOG.error(ex.getMessage());
                throw new DeviceReservationException(ErrorCodes.INTERNAL_SERVER_ERROR);
            }
        };
    }

    public ActiveMQTextMessage toActiveMQTextMessage(Exchange exchange)
        throws MessageNotWriteableException,
            IOException {
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setText(exchange.getIn().getBody(String.class));
        message.setProperty("eventId", exchange.getIn().getHeader("eventId"));
        message.setProperty("eventid", exchange.getIn().getHeader("eventid"));
        message.setProperty("typename", exchange.getIn().getHeader("typename"));
        message.setProperty("occurredon", exchange.getIn().getHeader("occurredon"));
        message.setProperty("version", exchange.getIn().getHeader("version"));
        message.setProperty("group", exchange.getIn().getHeader("group"));
        return message;
    }
}
