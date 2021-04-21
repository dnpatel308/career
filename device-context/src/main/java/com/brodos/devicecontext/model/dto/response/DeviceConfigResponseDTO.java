package com.brodos.devicecontext.model.dto.response;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.brodos.devicecontext.model.dto.DeviceConfigFieldDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceConfigResponseDTO {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("articlenumber")
    private String articleNo;
    private Set<DeviceConfigFieldDTO> fields;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DeviceConfigLinksDTO _links;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @JsonProperty("_links")
    public DeviceConfigLinksDTO getLinks() {
        if (_links == null && id != null) {
            this._links = new DeviceConfigLinksDTO();
        }

        return _links;
    }

    public void setLinks(DeviceConfigLinksDTO _links) {
        this._links = _links;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
