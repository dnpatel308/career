package com.brodos.reservation.component;

import com.brodos.reservation.camel.route.EmailRouteBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;

//import org.apache.camel.CamelContext;
//import org.apache.camel.ProducerTemplate;
//import org.apache.camel.component.mail.MailComponent;
//import org.apache.camel.impl.DefaultProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:mail.properties")
public class EmailComponent {

    private static final Logger LOG = LoggerFactory.getLogger(EmailComponent.class);

    // private ExecutorService executorService;

    @Autowired
    Environment mailEnvironment;

    // @Autowired
    // CamelContext camelContext;

    // @Autowired
    // EmailRouteBuilder emailRouteBuilder;

    // private ProducerTemplate emailTemplate;

    @PostConstruct
    public void postConstruct() {
        // try {
        // executorService = Executors.newFixedThreadPool(10);
        // MailComponent mailComponent = new MailComponent();
        // camelContext.addComponent("mailComponent", mailComponent);
        //
        // camelContext.addRoutes(emailRouteBuilder);
        //
        // emailTemplate = new DefaultProducerTemplate(camelContext);
        // emailTemplate.setDefaultEndpointUri("direct:sendEmail");
        // emailTemplate.start();
        // } catch (Exception ex) {
        // LOG.error(ex.getMessage(), ex);
        // }
    }

    public void sendMail(String from, String to, String subject, String body) {
        // Map<String, Object> headers = new HashMap<>();
        // headers.put("from", from);
        // headers.put("to", to);
        // headers.put("subject", subject);
        // emailTemplate.sendBodyAndHeaders(body, headers);
    }

    public void sendMailAsyc(String from, String to, String subject, String body) {
        // Map<String, Object> headers = new HashMap<>();
        // headers.put("from", from);
        // headers.put("to", to);
        // headers.put("subject", subject);
        // executorService.submit(() -> {
        // emailTemplate.sendBodyAndHeaders(body, headers);
        // });
    }

    public Environment getMailEnvironment() {
        return mailEnvironment;
    }

}
