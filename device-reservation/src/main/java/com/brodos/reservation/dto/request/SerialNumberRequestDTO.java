/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class SerialNumberRequestDTO {

    @Valid
    @JsonProperty("serial")
    private SerialDTO serialDTO;

    public SerialDTO getSerialDTO() {
        return serialDTO;
    }

    public void setSerialDTO(SerialDTO serialDTO) {
        this.serialDTO = serialDTO;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
