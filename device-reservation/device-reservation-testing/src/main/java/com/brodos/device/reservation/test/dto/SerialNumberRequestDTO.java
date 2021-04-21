/**
 * 
 */
package com.brodos.device.reservation.test.dto;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.ToStringBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author snihit
 *
 * 
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