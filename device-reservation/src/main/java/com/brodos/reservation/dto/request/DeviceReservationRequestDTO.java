/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.dto.request;

import com.brodos.reservation.dto.ReservationReferenceDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class DeviceReservationRequestDTO {

    @NotBlank(message = "Article number is mandatory")
    @Pattern(regexp = "^([A-Za-z0-9-]*)", message = "Invalid article number")
    @JsonProperty("articleno")
    private String articleNo;
    @Pattern(regexp = "[0-9]+", message = "Invalid group")
    private String group = "2"; // default value 2
    private String comment;
    @Pattern(regexp = "[0-9]+", message = "Invalid customer number")
    @JsonProperty("customerno")
    private String customerNo;
    @Pattern(regexp = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$", message = "Invalid email")
    private String email;
    @JsonProperty("devicerequired")
    private Boolean deviceRequired = false;
    private Boolean consignment = false;
    private Set<ReservationReferenceDTO> references;

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

    public Boolean isDeviceRequired() {
        return deviceRequired;
    }

    public void setDeviceRequired(Boolean deviceRequired) {
        this.deviceRequired = deviceRequired;
    }

    public Boolean getConsignment() {
        return consignment;
    }

    public void setConsignment(Boolean consignment) {
        this.consignment = consignment;
    }

    public Set<ReservationReferenceDTO> getReferences() {
        return references;
    }

    public void setReferences(Set<ReservationReferenceDTO> references) {
        this.references = references;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
