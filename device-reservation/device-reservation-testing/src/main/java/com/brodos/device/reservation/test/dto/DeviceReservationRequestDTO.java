/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.device.reservation.test.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class DeviceReservationRequestDTO {
        
    @JsonProperty("articleno")
    private String articleNo;    
    private String group = "2"; // default value 2
    private String comment;    
    @JsonProperty("customerno")
    private String customerNo;    
    private String email;
    @JsonProperty("devicerequired")
    private Boolean deviceRequired = false;
    private Set<DeviceReservationReferenceDTO> references;

    public String getArticleNo() {
        return articleNo;
    }

    public void setArticleNo(String articleNo) {
        this.articleNo = articleNo;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getDeviceRequired() {
        return deviceRequired;
    }

    public void setDeviceRequired(Boolean deviceRequired) {
        this.deviceRequired = deviceRequired;
    }

    public Set<DeviceReservationReferenceDTO> getReferences() {
        return references;
    }

    public void setReferences(Set<DeviceReservationReferenceDTO> references) {
        this.references = references;
    }    

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
