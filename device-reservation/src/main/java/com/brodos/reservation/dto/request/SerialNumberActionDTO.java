/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.dto.request;

import javax.validation.constraints.NotBlank;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class SerialNumberActionDTO {

    @NotBlank(message = "Invalid type")
    private String type;
    private DeviceReservationActionArgumentsDTO arguments;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DeviceReservationActionArgumentsDTO getArguments() {
        return arguments;
    }

    public void setArguments(DeviceReservationActionArgumentsDTO arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
