/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.component;

import javax.jms.Connection;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author padhaval
 */
@Component
public class JmsConfig {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(JmsConfig.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    public boolean checkJmsHealth() {
        try {
            Connection connection = jmsTemplate.getConnectionFactory().createConnection();
            LOG.debug("Connected to JMS, JMS Version={}, Active MQ Version={}", connection.getMetaData()
                .getJMSVersion(), connection.getMetaData().getProviderVersion());
            connection.close();
            return true;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return false;
    }

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }
}
