/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author padhaval
 */
@JsonPropertyOrder({"weightInIntegerRepresentation", "unit"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeightDTO {

    @NotNull(message = "Field 'weight' is blank. Please provide a valid 'weight'")
    private Integer weightInIntegerRepresentation;
    @NotBlank(message = "Field 'unit' is blank. Please provide a valid 'unit'")
    private String unit;

    public WeightDTO() {

    }

    public Integer getWeightInIntegerRepresentation() {
        return weightInIntegerRepresentation;
    }

    public void setWeightInIntegerRepresentation(Integer weightInIntegerRepresentation) {
        this.weightInIntegerRepresentation = weightInIntegerRepresentation;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
