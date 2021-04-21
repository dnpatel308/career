/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.model.dto.request;

import com.brodos.devicecontext.model.dto.DeviceFieldDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class CreateOrUpdateDeviceDTO {

    @NotBlank(message = "Article number is mandatory")
    // @Pattern(regexp = "^([A-Za-z0-9-]*)", message = "Invalid article number")
    @JsonProperty("articlenumber")
    private String articleNo;
    @Valid
    private Set<DeviceFieldDTO> fields;

    public String getArticleNo() {
        return articleNo;
    }

    public void setArticleNo(String articleNo) {
        this.articleNo = articleNo;
    }

    public Set<DeviceFieldDTO> getFields() {
        return fields;
    }

    public void setFields(Set<DeviceFieldDTO> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
