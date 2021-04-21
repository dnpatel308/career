/**
 * 
 */
package com.brodos.device.reservation.test.dto;

import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author snihit
 *
 * 
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
