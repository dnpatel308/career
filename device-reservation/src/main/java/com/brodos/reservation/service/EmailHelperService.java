/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service;

import com.brodos.email.domain.dto.EmailRequestDTO;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.events.RequestedForSendout;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author padhaval
 */
public interface EmailHelperService {

    public EmailRequestDTO prepareEmailRequestDTO(TicketReference ticketReference, String email) throws IOException;

    public Map<String, Object> prepareRequestSentoutContent(RequestedForSendout requestedForSendout, String subject);
}
