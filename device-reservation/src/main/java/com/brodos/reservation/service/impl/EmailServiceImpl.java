/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service.impl;

import com.brodos.email.domain.dto.EmailRequestDTO;
import com.brodos.reservation.component.BrodosEmailComponent;
import com.brodos.reservation.component.EmailComponent;
import com.brodos.reservation.entity.Customer;
import com.brodos.reservation.entity.SerialNumberReservationTicketReference;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.events.RequestedForSendout;
import com.brodos.reservation.infrastructure.TicketReferenceRepository;
import com.brodos.reservation.service.EmailHelperService;
import com.brodos.reservation.service.EmailService;
import com.brodos.reservation.service.EmailTemplateService;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author padhaval
 */
@Service
@Transactional
public class EmailServiceImpl implements EmailService {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EmailHelperServiceImpl.class);

    @Autowired
    private BrodosEmailComponent brodosEmailComponent;

    @Autowired
    private EmailComponent emailComponent;

    @Autowired
    private EmailTemplateService emailTemplateService;

    @Autowired
    private EmailHelperService emailHelperService;

    @Autowired
    private TicketReferenceRepository ticketReferenceRepository;

    @Override
    public void sendReservationMail(TicketReference ticketReference) {
        try {
            EmailRequestDTO emailRequestDTO =
                emailHelperService.prepareEmailRequestDTO(ticketReference, ticketReference.getEmail());
            if (brodosEmailComponent.getEmailService().sendMail(emailRequestDTO)) {
                ticketReference.getSerialNumberReservation().setMailSentToCustomer(Boolean.TRUE);
                ticketReferenceRepository.save(ticketReference);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            LOG.trace(ex.getMessage(), ex);
        }
    }

    @Override
    public void sendRequestSentoutSuccessMail(SerialNumberReservationTicketReference ticketReference, String voucherNo) {
        String subject = emailComponent.getMailEnvironment().getProperty("requestforsentout.salespersonnel.email.subject.success");
        subject = subject.replace("{customernumber}",
                ((Customer) ticketReference.getSerialNumberReservation().getOwner()).getCustomerNumber());
        HashMap<String, String> voucherSuccessMap = new HashMap<>();
        voucherSuccessMap.put("voucherNo", voucherNo);
        String body = emailTemplateService.getFormattedContent(emailComponent.getMailEnvironment().getProperty("requestforsentout.salespersonnel.email.template.success"),
                voucherSuccessMap);
        sendRequestSentoutMail(ticketReference, subject, body);
    }

    @Override
    public void sendRequestSentoutFailureMail(RequestedForSendout requestedForSendout) {
        SerialNumberReservationTicketReference ticketReference =
            (SerialNumberReservationTicketReference) requestedForSendout.getTicketReference();
        String subject =
            emailComponent.getMailEnvironment().getProperty("requestforsentout.salespersonnel.email.subject.failure");
        if (!StringUtils.isBlank(requestedForSendout.getCustomerno())) {
            subject =
                subject.replace("{customernumber}",
                    ((Customer) ticketReference.getSerialNumberReservation().getOwner()).getCustomerNumber());
        } else {
            subject =
                emailComponent.getMailEnvironment().getProperty(
                    "requestforsentout.salespersonnel.email.subject.failure.missing.customernumber");
            subject = subject.replace("{ticketno}", ticketReference.getTicketNumber());
        }

        Map<String, Object> requestParamMap =
            emailHelperService.prepareRequestSentoutContent(requestedForSendout, subject);
        subject = (String) requestParamMap.get("subject");
        @SuppressWarnings("unchecked")
        Map<String, String> paramMap = (Map<String, String>) requestParamMap.get("paramMap");
        String body =
            emailTemplateService.getFormattedContent(
                emailComponent.getMailEnvironment().getProperty(
                    "requestforsentout.salespersonnel.email.template.failure"), paramMap);
        sendRequestSentoutMail(ticketReference, subject, body);
    }

    private void sendRequestSentoutMail(SerialNumberReservationTicketReference ticketReference, String subject,
        String body) {
        String from = emailComponent.getMailEnvironment().getProperty("reservation.from");
        String to = emailComponent.getMailEnvironment().getProperty("requestforsentout.salespersonnel.email.address");
        emailComponent.sendMail(from, to, subject, body);
        ticketReference.getSerialNumberReservation().setBrodosVoucherMailSuccess(true);
    }
}
