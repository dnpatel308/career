/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service;

import com.brodos.reservation.entity.SerialNumberImportTicketReference;
import com.brodos.ticket.domain.dto.TicketRequestDTO;
import java.io.IOException;

/**
 *
 * @author padhaval
 */
public interface TicketHelperService {

    public TicketRequestDTO prepareTicketDTO(String responsibility, int sendMail, Long tenantId, String customerNumber,
        String ticketSubject, String ticketXMLContent);

    public String getTicketXMLContentForReservation(String articleNo, String email, String reservationComment,
        String user, String serialNo) throws IOException;

    public String getTicketXMLContentForOpenCase(Integer warehouseNo, String articleNo, String email, String createdBy)
        throws IOException;

    public String getTicketXMLContentForOpenCaseReservation(String articleNo, String email, String reservationComment,
        String user, String serialNo, String opencaseTicket) throws IOException;

    public String
        getCommentForSerialNumberImportTicketUpdate(
            SerialNumberImportTicketReference serialNumberImportTicketReference, String user,
            String reservationTicketNumber);

    public TicketRequestDTO getTicketDTOForUpdateRequest(String ticketNumber, String responsibility, String comment);
}
