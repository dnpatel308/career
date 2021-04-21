/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
@JsonPropertyOrder({"freightForwarderType", "customProperties"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "freightForwarderType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DHLDTO.class, name = "DHL")
    ,
    @JsonSubTypes.Type(value = TOFDTO.class, name = "TOF")})
@JsonInclude(Include.NON_NULL)
public abstract class FreightForwarderDTO {

    @NotNull(message = "Field 'freight forwarder type' is blank. Please provide a valid 'freight forwarder type'")
    @JsonProperty(value = "freightForwarderType")
    protected FreightForwarderType freightForwarderType;

    @NotNull(message = "Field 'custom properties' is blank. Please provide a valid 'custom properties'")
    private Map<String, Object> customProperties;    

    public FreightForwarderType getFreightForwarderType() {
        return freightForwarderType;
    }

    public void setFreightForwarderType(FreightForwarderType freightForwarderType) {
        this.freightForwarderType = freightForwarderType;
    }

    public Map<String, Object> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, Object> customProperties) {
        this.customProperties = customProperties;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }        
}
