package com.brodos.devicecontext.test.dto;

import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateOrUpdateDeviceConfigDTO {
 
    // @Pattern(regexp = "^([A-Za-z0-9-.]*)", message = "Invalid article number")
    @JsonProperty("articlenumber")
    private String articleNo;
    @Valid
    private Set<DeviceConfigFieldDTO> fields;

    public String getArticleNo() {
        return articleNo;
    }

    public void setArticleNo(String articleNo) {
        this.articleNo = articleNo;
    }

    public Set<DeviceConfigFieldDTO> getFields() {
        return fields;
    }

    public void setFields(Set<DeviceConfigFieldDTO> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
