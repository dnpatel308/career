/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.component;

import com.brodos.email.domain.adapter.impl.EmailAPIClient;
import com.brodos.email.domain.adapter.impl.EmailAdapterImpl;
import com.brodos.email.domain.service.impl.EmailServiceImpl;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 *
 * @author padhaval
 */
@Component
@PropertySource("classpath:brodos.mail.properties")
public class BrodosEmailComponent {

    @Autowired
    private Environment brodosMailEnvironment;

    private EmailServiceImpl emailServiceImpl;

    @PostConstruct
    public void postConstruct() {
        EmailAPIClient emailAPIClient = new EmailAPIClient();
        emailAPIClient.setEmailAPIURL(brodosMailEnvironment.getProperty("emailAPIURL"));
        emailAPIClient.setEnvironment(brodosMailEnvironment.getProperty("environment"));

        EmailAdapterImpl emailAdapterImpl = new EmailAdapterImpl();
        emailAdapterImpl.setEmailAPIClient(emailAPIClient);

        emailServiceImpl = new EmailServiceImpl();
        emailServiceImpl.setEmailAdapter(emailAdapterImpl);
    }

    public Environment geEnvironment() {
        return brodosMailEnvironment;
    }

    public EmailServiceImpl getEmailService() {
        return emailServiceImpl;
    }
}
