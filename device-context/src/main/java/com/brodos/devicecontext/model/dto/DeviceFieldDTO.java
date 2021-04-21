/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceFieldDTO {

    @NotBlank(message = "Field name is mandatory")
    private String name;
    private String type;
    @NotNull(message = "Field value is mandatory")
    @Pattern(regexp = "^([A-Za-z0-9]*)", message = "Invalid field value")
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
