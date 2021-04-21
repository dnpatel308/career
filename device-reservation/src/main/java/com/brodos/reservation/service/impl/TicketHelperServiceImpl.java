/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service.impl;

import com.brodos.reservation.component.TicketComponent;
import com.brodos.reservation.entity.OpenCaseStatus;
import com.brodos.reservation.entity.SerialNumberImportTicketReference;
import com.brodos.reservation.service.TicketHelperService;
import com.brodos.ticket.domain.dto.TicketRequestDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author padhaval
 */
@Service
@Transactional
public class TicketHelperServiceImpl implements TicketHelperService {

    @Autowired
    private TicketComponent ticketComponent;

    @Override
    public TicketRequestDTO prepareTicketDTO(String responsibility, int sendMail, Long tenantId, String customerNumber,
        String ticketSubject, String ticketXMLContent) {
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setIspublic(Integer.parseInt(ticketComponent.geEnvironment().getProperty("ticket.ispublic")));
        ticketRequestDTO.setPassword(ticketComponent.geEnvironment().getProperty("ticket.password"));
        ticketRequestDTO.setUserName(ticketComponent.geEnvironment().getProperty("ticket.username"));
        ticketRequestDTO.setResponsibility(responsibility);
        ticketRequestDTO.setSendmail(sendMail);
        ticketRequestDTO.setState(ticketComponent.geEnvironment().getProperty("ticket.openstatus"));
        ticketRequestDTO.setSubject(ticketComponent.geEnvironment().getProperty("ticket.reservation.subject"));
        ticketRequestDTO.setSystemId(tenantId.toString());
        if (!StringUtils.isBlank(customerNumber)) {
            ticketRequestDTO.setCustomerNo(customerNumber);
        }
        ticketRequestDTO.setSubject(ticketSubject);
        ticketRequestDTO.setXml(ticketXMLContent);
        return ticketRequestDTO;

    }

    @Override
    public String getTicketXMLContentForOpenCase(Integer warehouseNo, String articleNo, String email, String createdBy)
        throws IOException {
        String xmlContent = getXMLContent(ticketComponent.geEnvironment().getProperty("ticket.opencase.xml"));
        xmlContent = xmlContent.replace("${ARTICLEVAL}", articleNo);
        xmlContent = xmlContent.replace("${WAREHOUSENO}", String.valueOf(warehouseNo));
        xmlContent = xmlContent.replace("${EMAILVAL}", email == null ? "" : email);
        xmlContent = xmlContent.replace("${CREATEDBYVAL}", createdBy);
        return xmlContent;
    }

    @Override
    public String getTicketXMLContentForReservation(String articleNo, String email, String reservationComment,
        String user, String serialNo) throws IOException {
        String xmlContent = getXMLContent(ticketComponent.geEnvironment().getProperty("ticket.reservation.xml"));
        xmlContent = xmlContent.replace("${ARTICLEVAL}", articleNo);
        xmlContent = xmlContent.replace("${EMAILVAL}", email == null ? "" : email);
        xmlContent = xmlContent.replace("${IMEIVAL}", serialNo);
        xmlContent =
            xmlContent.replace("${RESERVATIONCOMMENTVAL}",
                reservationComment == null || reservationComment.equals(StringUtils.EMPTY) ? "No Comment"
                    : reservationComment);
        xmlContent = xmlContent.replace("${CREATEDBYVAL}", user);
        return xmlContent;
    }

    @Override
    public String getTicketXMLContentForOpenCaseReservation(String articleNo, String email, String reservationComment,
        String user, String serialNo, String opencaseTicket) throws IOException {
        String xmlContent =
            getXMLContent(ticketComponent.geEnvironment().getProperty("ticket.opencase.reservation.xml"));
        xmlContent = xmlContent.replace("${OPENCASETICKETVAL}", opencaseTicket);
        xmlContent = xmlContent.replace("${ARTICLEVAL}", articleNo);
        xmlContent = xmlContent.replace("${EMAILVAL}", email == null ? "" : email);
        xmlContent = xmlContent.replace("${IMEIVAL}", serialNo);
        xmlContent =
            xmlContent.replace("${RESERVATIONCOMMENTVAL}",
                reservationComment == null || reservationComment.equals(StringUtils.EMPTY) ? "No Comment"
                    : reservationComment);
        xmlContent = xmlContent.replace("${CREATEDBYVAL}", user);
        return xmlContent;
    }

    private String getXMLContent(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(ticketComponent.geEnvironment().getProperty(
            "ticket.ticketxml.location")
            + fileName)));
    }

    @Override
    public String getCommentForSerialNumberImportTicketUpdate(
        SerialNumberImportTicketReference serialNumberImportTicketReference, String user, String comment) {
        OpenCaseStatus openCaseStatus = serialNumberImportTicketReference.getOpenCaseStatus();
        if (openCaseStatus.equals(OpenCaseStatus.CANCELLED)) {
            serialNumberImportTicketReference.setCancellationComment(comment);
            return serialNumberImportTicketReference.getCancellationComment() + "." + " Cancelled by : " + user;
        }

        return ticketComponent.geEnvironment().getProperty("ticket.imei.import.change.comment") + " Handled by : "
            + user;
    }

    @Override
    public TicketRequestDTO getTicketDTOForUpdateRequest(String ticketNumber, String responsibility, String comment) {
        TicketRequestDTO ticketRequestDTO = new TicketRequestDTO();
        ticketRequestDTO.setId(ticketNumber);
        ticketRequestDTO.setUserName(ticketComponent.geEnvironment().getProperty("ticket.username"));
        ticketRequestDTO.setPassword(ticketComponent.geEnvironment().getProperty("ticket.password"));
        ticketRequestDTO.setResponsibility(responsibility);
        ticketRequestDTO.setState(ticketComponent.geEnvironment().getProperty("ticket.closedstatus"));
        ticketRequestDTO.setComment(comment);
        ticketRequestDTO.setIspublic(Integer.parseInt(ticketComponent.geEnvironment().getProperty("ticket.ispublic")));
        return ticketRequestDTO;
    }
}
