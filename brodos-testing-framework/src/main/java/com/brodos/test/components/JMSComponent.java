/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.components;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 *
 * @author padhaval
 */
public class JMSComponent {

    private static final Map<String, JMSComponent> JMSCOMPONENT_MAP = new HashMap<>();
    private static final String DEFAULT_CONFIG_FILE_NAME = "activemq.properties";

    private final String configFileName;
    private final Properties properties = new Properties();

    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer messageProducer;
    private MessageConsumer messageConsumer;

    private JMSComponent() throws Exception {
        configFileName = DEFAULT_CONFIG_FILE_NAME;
        loadProperties(configFileName);
    }

    private JMSComponent(String configFileName) throws Exception {
        this.configFileName = configFileName;
        loadProperties(configFileName);
    }

    synchronized public static JMSComponent instance() throws Exception {
        if (!JMSCOMPONENT_MAP.containsKey(DEFAULT_CONFIG_FILE_NAME)) {
            JMSCOMPONENT_MAP.put(DEFAULT_CONFIG_FILE_NAME, new JMSComponent());
        }

        return JMSCOMPONENT_MAP.get(DEFAULT_CONFIG_FILE_NAME);
    }

    synchronized public static JMSComponent instance(String configFileName) throws Exception {
        if (!JMSCOMPONENT_MAP.containsKey(configFileName)) {
            JMSCOMPONENT_MAP.put(configFileName, new JMSComponent(configFileName));
        }

        return JMSCOMPONENT_MAP.get(configFileName);
    }
    
    private void loadProperties(String configFileName) throws Exception {
        properties.load(new FileReader(configFileName));
    }

    private Connection getConnection() throws Exception {
        if (connection == null) {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(properties.getProperty("username"), properties.getProperty("password"), properties.getProperty("activemq.url"));
            connection = connectionFactory.createConnection();
            connection.start();
        }

        return connection;
    }

    private Session getSession() throws Exception {
        if (session == null) {
            session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
        }

        return session;
    }

    private Destination getDestination() throws Exception {
        if (destination == null) {
            switch (properties.getProperty("destination.type").toUpperCase()) {
                case "TOPIC": {
                    destination = getSession().createTopic(properties.getProperty("destination.name"));
                    break;
                }

                case "QUEUE": {
                    destination = getSession().createQueue(properties.getProperty("destination.name"));
                    break;
                }
            }
        }

        return destination;
    }

    public MessageProducer getMessageProducer() throws Exception {
        if (messageProducer == null) {
            messageProducer = getSession().createProducer(getDestination());
        }

        return messageProducer;
    }

    public MessageConsumer getMessageConsumer() throws Exception {
        if (messageConsumer == null) {
            messageConsumer = getSession().createConsumer(getDestination());
        }

        return messageConsumer;
    }    
}
