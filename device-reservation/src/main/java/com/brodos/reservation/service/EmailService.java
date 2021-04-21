/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service;

import com.brodos.reservation.entity.SerialNumberReservationTicketReference;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.events.RequestedForSendout;

/**
 *
 * @author padhaval
 */
public interface EmailService {

    public void sendReservationMail(TicketReference ticketReference);

    public void sendRequestSentoutSuccessMail(SerialNumberReservationTicketReference ticketReference, String voucherNo);

    public void sendRequestSentoutFailureMail(RequestedForSendout requestedForSendout);
}
