/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.assembler;

import com.brodos.reservation.DeviceReservationApplication;
import java.util.ArrayList;
import java.util.List;

import com.brodos.reservation.dto.response.DeviceReservationResponseDTO;
import com.brodos.reservation.dto.response.DeviceReservationsDTO;
import com.brodos.reservation.dto.response.HealthResponseDTO;
import com.brodos.reservation.dto.response.LinkDTO;
import com.brodos.reservation.dto.response.OpenCaseDTO;
import com.brodos.reservation.dto.response.ReservationDTO;
import com.brodos.reservation.entity.SerialNumberImportTicketReference;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.TicketReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;

/**
 *
 * @author padhaval
 */
public class ResponseAssembler {

    @Autowired
    ReservationReferencesAssembler reservationReferencesAssembler;

    @Autowired
    PagedResourcesAssembler assembler;

    public HealthResponseDTO toHealthResponseDTO(boolean success) {
        HealthResponseDTO healthResponseDTO = new HealthResponseDTO();
        healthResponseDTO.setServiceName("device-reservation-service");
        healthResponseDTO.setTimestamp(System.currentTimeMillis());
        healthResponseDTO.setIsSuccessful(success);
        return healthResponseDTO;
    }

    public DeviceReservationsDTO toDeviceReservationResponse(String requestUrl,
            Page<TicketReference> ticketReferences) {
        List<SerialNumberReservation> serialNumberReservations = new ArrayList<>();
        for (TicketReference ticketReference : ticketReferences) {
            if (ticketReference.getSerialNumberReservation() != null) {
                serialNumberReservations.add(ticketReference.getSerialNumberReservation());
            }
        }

        DeviceReservationsDTO deviceReservationsDTO = toDeviceReservationResponse(requestUrl, serialNumberReservations);
        PagedModel pagedModel = assembler.toModel(ticketReferences);
        deviceReservationsDTO.setLinksAndPage(pagedModel);

        return deviceReservationsDTO;
    }

    public DeviceReservationsDTO toDeviceReservationResponse(String requestUrl,
            List<SerialNumberReservation> serialNumberReservations) {
        List<DeviceReservationResponseDTO> deviceReservationResponseDTOs = new ArrayList<>();
        for (SerialNumberReservation serialNumberReservation : serialNumberReservations) {
            deviceReservationResponseDTOs.add(toDeviceReservationResponse(requestUrl, serialNumberReservation));
        }
        ReservationDTO reservations = new ReservationDTO();
        reservations.setReservations(deviceReservationResponseDTOs);
        DeviceReservationsDTO deviceReservationsDTO = new DeviceReservationsDTO();
        deviceReservationsDTO.setEmbedded(reservations);
        return deviceReservationsDTO;
    }

    public DeviceReservationResponseDTO toDeviceReservationResponse(String requestUrl,
        SerialNumberReservation serialNumberReservation) {
        DeviceReservationResponseDTO deviceReservationResponseDTO = new DeviceReservationResponseDTO();
        deviceReservationResponseDTO.setId(serialNumberReservation.getId());
        if (serialNumberReservation.getSerialNumber() != null) {
            deviceReservationResponseDTO.setArticleNo(serialNumberReservation.getSerialNumber().getArticle()
                .getArticleId().getArticleNumber());
            deviceReservationResponseDTO.setSerialNo(serialNumberReservation.getSerialNumber().getNumber());
            deviceReservationResponseDTO.setEmbedded(serialNumberReservation.getSerialNumber().getEmbedded());
            deviceReservationResponseDTO.setWarehouseNo(serialNumberReservation.getSerialNumber().getWarehouseNo());
        } else if (serialNumberReservation.getTicketReference() != null) {
            SerialNumberImportTicketReference serialNumberImportTicketReference =
                (SerialNumberImportTicketReference) serialNumberReservation.getTicketReference();
            deviceReservationResponseDTO.setArticleNo(serialNumberImportTicketReference.getArticle().getArticleId()
                .getArticleNumber());
            if (!StringUtils.isBlank(serialNumberImportTicketReference.getTicketNumber())) {
                deviceReservationResponseDTO.setOpencase(new OpenCaseDTO());
                deviceReservationResponseDTO.getOpencase().setNo(serialNumberImportTicketReference.getTicketNumber());
            }
            deviceReservationResponseDTO.setWarehouseNo(serialNumberImportTicketReference.getWarehouseNo());
        } else if (serialNumberReservation.getDomainEvent() != null) {
            deviceReservationResponseDTO.setArticleNo(serialNumberReservation.getDomainEvent().getArticleNo());
            deviceReservationResponseDTO.setWarehouseNo(serialNumberReservation.getDomainEvent().getGroup());
        }

        deviceReservationResponseDTO.setStatus(serialNumberReservation.getStatus());
        deviceReservationResponseDTO.setReferences(reservationReferencesAssembler
            .toReservationReferences(serialNumberReservation.getReservationReferences()));
        deviceReservationResponseDTO.setConsignment(serialNumberReservation.getConsignment());
        if (serialNumberReservation.getDomainEvent() != null) {
            deviceReservationResponseDTO.setDeviceRequired(serialNumberReservation.getDomainEvent()
                .getCancelIfUnavailable());
        }
        updateReservationLink(requestUrl, deviceReservationResponseDTO);
        updateCancellationAndSentoutLinks(deviceReservationResponseDTO);
        deviceReservationResponseDTO.setBulkid(serialNumberReservation.getBulkReservationId());
        return deviceReservationResponseDTO;
    }

    private void updateReservationLink(String requestUrl, DeviceReservationResponseDTO deviceReservationResponseDTO) {
        StringBuilder selfLinkBuilder = new StringBuilder();
        selfLinkBuilder.append(StringUtils.substringBefore(requestUrl, DeviceReservationApplication.CONTEXT_PATH));
        selfLinkBuilder.append(DeviceReservationApplication.CONTEXT_PATH);
        selfLinkBuilder.append("reservations/");
        selfLinkBuilder.append(deviceReservationResponseDTO.getId());

        deviceReservationResponseDTO.getLinks().setSelf(new LinkDTO(selfLinkBuilder.toString()));
    }

    private void updateCancellationAndSentoutLinks(DeviceReservationResponseDTO deviceReservationResponseDTO) {
        switch (deviceReservationResponseDTO.getStatus()) {
            case RESERVED: {
                String selfLink = deviceReservationResponseDTO.getLinks().getSelf().getHref();
                deviceReservationResponseDTO.getLinks().setCancel(new LinkDTO(selfLink + "/actions"));
                deviceReservationResponseDTO.getLinks().setSendout(new LinkDTO(selfLink + "/actions"));
                break;
            }

            case PENDING: {
                String selfLink = deviceReservationResponseDTO.getLinks().getSelf().getHref();
                deviceReservationResponseDTO.getLinks().setCancel(new LinkDTO(selfLink + "/actions"));
                break;
            }

            default: {
                break;
            }
        }

    }
}
