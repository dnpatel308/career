/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.resource;

import com.brodos.reservation.DeviceReservationApplication;
import com.brodos.reservation.assembler.SerialNumberAssembler;
import com.brodos.reservation.dto.request.SerialNumberActionDTO;
import com.brodos.reservation.dto.request.SerialNumberRequestDTO;
import com.brodos.reservation.dto.response.SerialNumberResponseDTO;
import io.micrometer.core.annotation.Timed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.brodos.reservation.service.SerialNumberService;
import javax.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author padhaval
 */
@RestController
@RequestMapping(DeviceReservationApplication.CONTEXT_PATH + "/serials")
@Validated
public class SerialNumberResource {

    private static final Logger LOG = LoggerFactory.getLogger(SerialNumberResource.class);

    @Autowired
    private SerialNumberAssembler serialNumberAssembler;

    @Autowired
    private SerialNumberService serialNumberService;

    @ResponseStatus(HttpStatus.CREATED)
    @Timed(description = "Import serial number request")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SerialNumberResponseDTO importSerialNumber(HttpServletRequest request,
        @RequestBody @Valid SerialNumberRequestDTO serialNumberRequestDTO) {
        LOG.info("Processing serial number import request, number={}", serialNumberRequestDTO.getSerialDTO()
            .getNumber());
        SerialNumberResponseDTO serialNumberResponseDTO =
            serialNumberAssembler.toSerialNumberResponseDTO(request.getRequestURL().toString(),
                serialNumberService.importSerialNoInPool(serialNumberRequestDTO));
        serialNumberResponseDTO.getSerialDTO().setEmbedded(serialNumberRequestDTO.getSerialDTO().getEmbedded());
        return serialNumberResponseDTO;
    }

    @Timed(description = "Get serial number by id")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SerialNumberResponseDTO getSerialNumberById(HttpServletRequest request, @PathVariable("id") Long id) {
        LOG.info("Getting serial number by id={}", id);
        return serialNumberAssembler.toSerialNumberResponseDTO(request.getRequestURL().toString(),
            serialNumberService.getSerialNumberById(id));
    }

    @Timed(description = "Update serial number request")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SerialNumberResponseDTO updateSerialNumber(HttpServletRequest request, @PathVariable("id") Long id,
        @RequestBody @Valid SerialNumberRequestDTO serialNumberRequestDTO) {
        LOG.info("Processing serial number update request, number={}", serialNumberRequestDTO.getSerialDTO()
            .getNumber());
        return serialNumberAssembler.toSerialNumberResponseDTO(request.getRequestURL().toString(),
            serialNumberService.updateSerialNo(id, serialNumberRequestDTO));
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @Timed(description = "Do serial number action")
    @PostMapping(value = "/{id}/actions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SerialNumberResponseDTO doSerialNumberAction(HttpServletRequest request, @PathVariable("id") Long id,
        @RequestBody @Valid SerialNumberActionDTO serialNumberActionDTO) {
        return serialNumberAssembler.toSerialNumberResponseDTO(request.getRequestURL().toString(),
            serialNumberService.doSerialNumberAction(id, serialNumberActionDTO));
    }

    @Timed(description = "Get serial by article number and imei")
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SerialNumberResponseDTO getSerialNumberByArticleNumberAndImei(HttpServletRequest request, @NotBlank(
        message = "Invalid article number") @RequestParam(value = "articlenumber") String articleNo, @NotBlank(
        message = "Invalid serial number") @RequestParam(value = "serialnumber") String serialNo) {
        LOG.info("Getting serial number by article number={} and imei={}", articleNo, serialNo);
        return serialNumberAssembler.toSerialNumberResponseDTO(request.getRequestURL().toString(),
            serialNumberService.getSerialNumberByArticleNumberAndImei(articleNo, serialNo));
    }
}
