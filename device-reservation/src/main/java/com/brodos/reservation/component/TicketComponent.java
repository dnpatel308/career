/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.component;

import com.brodos.ticket.domain.adapter.impl.TicketAPIClient;
import com.brodos.ticket.domain.adapter.impl.TicketAdapterImpl;
import com.brodos.ticket.domain.service.impl.TicketServiceImpl;
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
@PropertySource("classpath:brodos.ticket.properties")
public class TicketComponent {

    @Autowired
    private Environment brodosTicketEnvironment;

    private TicketServiceImpl ticketServiceImpl;

    @PostConstruct
    public void postConstruct() {
        TicketAPIClient ticketAPIClient = new TicketAPIClient();
        ticketAPIClient.setCreateTicketURL(brodosTicketEnvironment.getProperty("createTicketURL"));
        ticketAPIClient.setChangeTicketURL(brodosTicketEnvironment.getProperty("changeTicketURL"));
        ticketAPIClient.setEnvironment(brodosTicketEnvironment.getProperty("environment"));

        TicketAdapterImpl ticketAdapterImpl = new TicketAdapterImpl();
        ticketAdapterImpl.setTicketAPIClient(ticketAPIClient);

        ticketServiceImpl = new TicketServiceImpl();
        ticketServiceImpl.setTicketAdapter(ticketAdapterImpl);
    }

    public Environment geEnvironment() {
        return brodosTicketEnvironment;
    }

    public TicketServiceImpl getTicketService() {
        return ticketServiceImpl;
    }
}
