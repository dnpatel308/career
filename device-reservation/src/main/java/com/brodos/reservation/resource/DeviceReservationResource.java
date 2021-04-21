/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.resource;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.brodos.reservation.DeviceReservationApplication;
import com.brodos.reservation.assembler.ResponseAssembler;
import com.brodos.reservation.dto.request.DeviceReservationActionDTO;
import com.brodos.reservation.dto.request.DeviceReservationRequestDTO;
import com.brodos.reservation.dto.response.DeviceReservationResponseDTO;
import com.brodos.reservation.dto.response.DeviceReservationsDTO;
import com.brodos.reservation.dto.response.ReservationDTO;
import com.brodos.reservation.entity.DeviceReservationStatus;
import com.brodos.reservation.service.DeviceReservationService;
import io.micrometer.core.annotation.Timed;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author padhaval
 */
@RestController
@RequestMapping(DeviceReservationApplication.CONTEXT_PATH + "reservations")
public class DeviceReservationResource {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceReservationResource.class);

    @Autowired
    DeviceReservationService deviceReservationService;

    @Autowired
    ResponseAssembler responseAssembler;

    @Value("${reservation.request.header.key}")
    private String reservationRequestHeaderKey;

    @Timed(description = "Get Device Reservation By Id")
    @GetMapping("/{id}")
    @ResponseBody
    public DeviceReservationResponseDTO
        getDeviceReservationById(HttpServletRequest request, @PathVariable("id") long id) {
        return responseAssembler.toDeviceReservationResponse(request.getRequestURL().toString(),
            deviceReservationService.getDeviceReservationById(id));
    }

    @Timed(description = "Get Device Reservation By Bulk Reservation Id")
    @GetMapping("/by-bulkid/{bulkid}")
    @ResponseBody
    public DeviceReservationsDTO getDeviceReservationByBulkReservationId(HttpServletRequest request,
        @PathVariable("bulkid") long bulkid) {
        return responseAssembler.toDeviceReservationResponse(request.getRequestURL().toString(),
            deviceReservationService.getDeviceReservationByBulkReservationId(bulkid));
    }

    @Timed(description = "Get Device Reservations By Search Criteria")
    @GetMapping("")
    @ResponseBody
    public DeviceReservationsDTO getDeviceReservationsBySearchCriteria(HttpServletRequest request, @RequestParam(
        required = false) DeviceReservationStatus status, @RequestParam(required = false) String articleno,
        @RequestParam(required = false) String customerno,
        @RequestParam(name = "page", defaultValue = "0") String page,
        @RequestParam(name = "size", defaultValue = "10") String size, @RequestParam(name = "references.q_number",
            required = false) String reference,
        @RequestParam(name = "device.fields.imei1", required = false) String imei1) {
        // API response in definition for that
        return responseAssembler.toDeviceReservationResponse(request.getRequestURL().toString(),
            deviceReservationService.getDeviceReservationBySearchCriteria(status, articleno, customerno, reference,
                imei1, Integer.parseInt(page), Integer.parseInt(size)));
    }

    @Timed(description = "Create Device Reservation")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DeviceReservationResponseDTO createDeviceReservation(HttpServletRequest request,
        @RequestBody @Valid DeviceReservationRequestDTO deviceReservationRequestDTO) {
        LOG.info("Processing Device Reservation request, articleNo={}", deviceReservationRequestDTO.getArticleNo());
        return responseAssembler.toDeviceReservationResponse(
            request.getRequestURL().toString(),
            deviceReservationService.createDeviceReservationRequest(deviceReservationRequestDTO, null,
                request.getHeader(reservationRequestHeaderKey)));
    }

    @Timed(description = "Create Device Reservations")
    @PostMapping(value = "/bulk", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ReservationDTO createDeviceReservations(HttpServletRequest request,
        @RequestBody @Valid List<DeviceReservationRequestDTO> deviceReservationRequestDTOs) {
        return responseAssembler.toDeviceReservationResponse(request.getRequestURL().toString(),
            deviceReservationService.createDeviceReservationRequests(deviceReservationRequestDTOs)).getEmbedded();
    }

    @Timed(description = "Do Device Reservation Action")
    @PostMapping(value = "/{id}/actions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DeviceReservationResponseDTO doDeviceReservationAction(HttpServletRequest request,
        @PathVariable("id") Long id, @RequestBody @Valid DeviceReservationActionDTO deviceReservationActionDTO) {
        return responseAssembler.toDeviceReservationResponse(request.getRequestURL().toString(),
            deviceReservationService.doDeviceReservationAction(id, deviceReservationActionDTO));
    }
}
