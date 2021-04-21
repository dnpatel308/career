package com.brodos.devicecontext.model.dto.response;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.brodos.devicecontext.model.dto.DeviceFieldDTO;
import com.brodos.devicecontext.model.entity.DeviceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author vmukesh
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceSearchResponseDTO {
    @JsonProperty("id")
    private Long id;
    private DeviceStatus status;
    @JsonProperty("articlenumber")
    private String articleNo;
    private Set<DeviceFieldDTO> fields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

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
