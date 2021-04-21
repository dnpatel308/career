package com.brodos.reservation.configuration;

import com.brodos.reservation.assembler.ArticleAssembler;
import com.brodos.reservation.assembler.ReservationReferencesAssembler;
import com.brodos.reservation.assembler.DomainEventsAssembler;
import com.brodos.reservation.assembler.JmsMessageAssembler;
import com.brodos.reservation.assembler.ProductCodeAssembler;
import com.brodos.reservation.assembler.ResponseAssembler;
import com.brodos.reservation.assembler.SerialNumberAssembler;
import com.brodos.reservation.assembler.SerialNumberHistoryAssembler;
import com.brodos.reservation.assembler.VoucherEventsAssembler;
import com.brodos.reservation.camel.route.EmailRouteBuilder;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQConnectionFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author padhaval
 */
@Configuration
@EnableJms
public class DeviceReservationApplicationConfig {

    @Value("${jms.max.redelivery.policy}")
    private Integer maxRedeliveryPolicy;

    @Value("${spring.jms.pub-sub-domain}")
    private Boolean pubSubDomain;

    @Bean
    public ReservationReferencesAssembler reservationReferencesAssembler() {
        return new ReservationReferencesAssembler();
    }

    @Bean
    public ResponseAssembler responseAssembler() {
        return new ResponseAssembler();
    }

    @Bean
    public DomainEventsAssembler domainEventsAssembler() {
        return new DomainEventsAssembler();
    }

    @Bean
    public VoucherEventsAssembler voucherEventsAssembler() {
        return new VoucherEventsAssembler();
    }

    @Bean
    public ArticleAssembler articleAssembler() {
        return new ArticleAssembler();
    }

    @Bean
    public ProductCodeAssembler productCodeAssembler() {
        return new ProductCodeAssembler();
    }

    @Bean
    public EmailRouteBuilder emailRouteBuilder() {
        return new EmailRouteBuilder();
    }

    @Bean
    public JmsMessageAssembler jmsMessageAssembler() {
        return new JmsMessageAssembler();
    }

    @Bean
    public SerialNumberHistoryAssembler serialNumberHistoryAssembler() {
        return new SerialNumberHistoryAssembler();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ActiveMQConnectionFactoryCustomizer configureRedeliveryPolicy() {
        return connectionFactory -> {
            RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
            redeliveryPolicy.setMaximumRedeliveries(maxRedeliveryPolicy);
            redeliveryPolicy.setUseExponentialBackOff(true);
            connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        };
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(ConnectionFactory connectionFactory,
        DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(pubSubDomain);
        configurer.configure(factory, connectionFactory);
        JmsTransactionManager transactionManager = new JmsTransactionManager();
        transactionManager.setConnectionFactory(connectionFactory);
        factory.setTransactionManager(transactionManager);
        return factory;
    }

    @Bean
    public MessageConverter jmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public SerialNumberAssembler serialNumberAssembler() {
        return new SerialNumberAssembler();
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsCustomListenerContainerFactory(ConnectionFactory connectionFactory,
        DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        JmsTransactionManager transactionManager = new JmsTransactionManager();
        transactionManager.setConnectionFactory(connectionFactory);
        factory.setTransactionManager(transactionManager);
        return factory;
    }
}
