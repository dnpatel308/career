/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.model.dto.response;

import com.brodos.devicecontext.model.dto.DeviceFieldDTO;
import com.brodos.devicecontext.model.entity.DeviceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceResponseDTO {

    @JsonProperty("id")
    private Long id;
    private DeviceStatus status;
    @JsonProperty("articlenumber")
    private String articleNo;
    private Set<DeviceFieldDTO> fields;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DeviceLinksDTO _links;

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

    @JsonProperty("_links")
    public DeviceLinksDTO getLinks() {
        if (_links == null) {
            this._links = new DeviceLinksDTO();
        }

        return _links;
    }

    public void setLinks(DeviceLinksDTO _links) {
        this._links = _links;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
