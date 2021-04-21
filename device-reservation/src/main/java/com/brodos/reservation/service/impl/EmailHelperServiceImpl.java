/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service.impl;

import com.brodos.email.domain.dto.EmailRequestDTO;
import com.brodos.reservation.dto.request.AddressType;
import com.brodos.reservation.dto.request.PurchaseType;
import com.brodos.reservation.dto.request.Salutation;
import com.brodos.reservation.entity.Article;
import com.brodos.reservation.entity.EAN;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.SerialNumberReservationTicketReference;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.events.RequestedForSendout;
import com.brodos.reservation.infrastructure.EANRepository;
import com.brodos.reservation.service.EmailHelperService;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 *
 * @author padhaval
 */
@Service
@PropertySource("classpath:reservation.mail.properties")
public class EmailHelperServiceImpl implements EmailHelperService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailHelperServiceImpl.class);

    @Autowired
    private Environment reservationMailEnvironment;

    @Autowired
    private EANRepository eanRepository;

    private final String SHIP_TO_SHOP = "Lieferung an die Filiale";
    private final String SHIP_TO_CUSTOMER_ADDRESS = "Streckenlieferung an den Kunden";
    private final String COMMISSION_PURCHASE = "Kommissionsware versenden";
    private final String INVOICE_PURCHASE = "Kaufware versenden";
    private final String ADDRESS_TYPE = "addressType";
    private final String PURCHASE_TYPE = "purchaseType";
    private final String DELIVERY_TYPE = "{deliverytype}";
    private final String TR = "<tr>";
    private final String TD = "<td>";

    @Override
    public EmailRequestDTO prepareEmailRequestDTO(TicketReference ticketReference, String email) throws IOException {
        String templateXMLContent =
            new String(Files.readAllBytes(Paths.get(reservationMailEnvironment
                .getProperty("reservationmail.template.location"))));
        EmailRequestDTO emailRequestDTO = new EmailRequestDTO();
        emailRequestDTO.setUserName(reservationMailEnvironment.getProperty("reservationmail.username"));
        emailRequestDTO.setPassword(reservationMailEnvironment.getProperty("reservationmail.password"));
        emailRequestDTO.setEmailtemplateXML(prepareRequestXML(prepareParameterMap(ticketReference, email),
            templateXMLContent));
        return emailRequestDTO;
    }

    private Map<String, String> prepareParameterMap(TicketReference ticketReference, String email) {
        Map<String, String> parameterMapping = new HashMap<>();
        String articleName = ticketReference.getSerialNumberReservation().getSerialNumber().getArticle().getArticleName();
        if (articleName == null) {
            articleName = ticketReference.getSerialNumberReservation().getSerialNumber().getArticle().getArticleId().getArticleNumber();
        }
        parameterMapping.put("${theme}", reservationMailEnvironment.getProperty("reservationmail.theme"));
        parameterMapping.put("${template}", reservationMailEnvironment.getProperty("reservationmail.templatename"));
        parameterMapping.put("${language}", reservationMailEnvironment.getProperty("reservationmail.language"));

        parameterMapping.put("${articlename}", articleName);
        parameterMapping.put("${imeinumber}", ticketReference.getSerialNumberReservation().getSerialNumber().getNumber());
        parameterMapping.put("${producer}", getProducer(ticketReference.getSerialNumberReservation().getSerialNumber().getArticle()));
        parameterMapping.put("${ean}", getEanForArticle(ticketReference.getSerialNumberReservation()));
        parameterMapping.put("${articlenumber}",
                ticketReference.getSerialNumberReservation().getSerialNumber().getArticle().getArticleId().getArticleNumber());
        parameterMapping.put("${ticketid}", ticketReference.getTicketNumber());
        parameterMapping.put("${toemailaddress}", email);

        LOG.info("Preparng reservation Email To={}", parameterMapping.get("${toemailaddress}"));
        return parameterMapping;
    }

    private String prepareRequestXML(Map<String, String> parameterMapping, String templateXMLContent) {

        String xmlContent = templateXMLContent;
        for (Map.Entry<String, String> mapEntry : parameterMapping.entrySet()) {
            xmlContent = xmlContent.replace(mapEntry.getKey(), mapEntry.getValue());
        }
        return xmlContent;
    }

    private String getEanForArticle(SerialNumberReservation serialNumberReservation) {
        if (serialNumberReservation.getSerialNumber().getEan() != null) {
            return serialNumberReservation.getSerialNumber().getEan();
        } else {
            List<EAN> eans =
                eanRepository.findByArticleNumber(serialNumberReservation.getSerialNumber().getArticle().getArticleId()
                    .getArticleNumber());
            if (eans != null && !eans.isEmpty()) {
                return eans.get(0).getCode();
            }
        }
        return StringUtils.EMPTY;
    }

    private String getProducer(Article article) {
        return article.getProducer() != null ? article.getProducer() : StringUtils.EMPTY;
    }

    @Override
    public Map<String, Object> prepareRequestSentoutContent(RequestedForSendout requestedForSendout, String subject) {
        SerialNumberReservationTicketReference ticketReference = (SerialNumberReservationTicketReference) requestedForSendout.getTicketReference();
        Map<String, Object> requestSentoutValueMap = new HashMap<>();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("articlenumber",
                ticketReference.getSerialNumberReservation().getSerialNumber().getArticle().getArticleId().getArticleNumber());
        paramMap.put("imeinumber", ticketReference.getSerialNumberReservation().getSerialNumber().getNumber());
        paramMap.put("ticketnumber", ticketReference.getTicketNumber());

        JsonNode deliveryDetails = requestedForSendout.getDeliveryDetails();
        if (deliveryDetails.get("purchase_type").asText().equals(PurchaseType.COMMISSION.name())
                && deliveryDetails.get("shipt_to").asText().equals(AddressType.SELF.name())) {
            paramMap.put(PURCHASE_TYPE, COMMISSION_PURCHASE);
            paramMap.put(ADDRESS_TYPE, SHIP_TO_SHOP);
            subject = subject.replace(DELIVERY_TYPE, "");
        } else if (deliveryDetails.get("purchase_type").asText().equals(PurchaseType.COMMISSION.name())
                && deliveryDetails.get("shipt_to").asText().equals(AddressType.CUSTOMER.name())) {
            paramMap.put(PURCHASE_TYPE, COMMISSION_PURCHASE);
            paramMap.put(ADDRESS_TYPE, SHIP_TO_CUSTOMER_ADDRESS);
            subject = subject.replace(DELIVERY_TYPE, "STRECKE");
        } else if (deliveryDetails.get("purchase_type").asText().equals(PurchaseType.INVOICE.name())
                && deliveryDetails.get("shipt_to").asText().equals(AddressType.SELF.name())) {
            paramMap.put(PURCHASE_TYPE, INVOICE_PURCHASE);
            paramMap.put(ADDRESS_TYPE, SHIP_TO_SHOP);
            subject = subject.replace(DELIVERY_TYPE, "");
        } else {
            paramMap.put(PURCHASE_TYPE, INVOICE_PURCHASE);
            paramMap.put(ADDRESS_TYPE, SHIP_TO_CUSTOMER_ADDRESS);
            subject = subject.replace(DELIVERY_TYPE, "STRECKE");
        }
        paramMap.put("address", prepareAddress(deliveryDetails.get("delivery_address")));
        requestSentoutValueMap.put("subject", subject);
        requestSentoutValueMap.put("paramMap", paramMap);
        return requestSentoutValueMap;
    }

    private String prepareAddress(JsonNode addressNode) {
        StringBuilder address = new StringBuilder();
        if (addressNode.has("salutation")) {
            if (addressNode.get("salutation").asText().equals(Salutation.Firma.name())) {
                address.append(TR + TD).append(addressNode.get("salutation").asText()).append(".")
                    .append(addressNode.get("company").asText()).append(TD);
            } else {
                address.append(TR + TD).append(addressNode.get("salutation").asText()).append(".")
                    .append(addressNode.get("firstname").asText()).append(" ")
                    .append(addressNode.get("lastname").asText()).append(TD);
            }
        }
        if (addressNode.has("suffix")) {
            address.append(TR + TD).append(addressNode.get("suffix").asText()).append(TD);
        }
        if (addressNode.has("houseno")) {
            address.append(TR + TD).append(addressNode.get("houseno").asText()).append(TD);
        }
        if (addressNode.has("street")) {
            address.append(TR + TD).append(addressNode.get("street").asText()).append(TD);
        }
        if (addressNode.has("city")) {
            address.append(TR + TD).append(addressNode.get("city").asText()).append(TD);
        }
        if (addressNode.has("zipcode")) {
            address.append(TR + TD).append(addressNode.get("zipcode").asText());
        }
        return address.toString();
    }
}
