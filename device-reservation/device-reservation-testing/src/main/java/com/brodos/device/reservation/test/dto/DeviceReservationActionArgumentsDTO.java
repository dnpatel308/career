/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.device.reservation.test.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author padhaval
 */
public class DeviceReservationActionArgumentsDTO {

    private String reason;
    @JsonProperty("shipt_to")
    private String addressType;
    @JsonProperty("purchase_type")
    private String purchaseType;
    @JsonProperty("delivery_address")
    private RequestSentoutAddressDTO address;

    public DeviceReservationActionArgumentsDTO() {
    }

    public DeviceReservationActionArgumentsDTO(String reason) {
        this.reason = reason;
    }

    public DeviceReservationActionArgumentsDTO(RequestSentoutAddressDTO address) {
        this.address = address;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public RequestSentoutAddressDTO getAddress() {
        return address;
    }

    public void setAddress(RequestSentoutAddressDTO address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}