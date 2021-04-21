/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class EmbeddedDeviceConfigsResponseDTO {

    private DeviceConfigsResponseDTO _embedded;
    private DeviceConfigLinksDTO _links;

    @JsonProperty("_embedded")
    public DeviceConfigsResponseDTO getEmbedded() {
        return _embedded;
    }

    public void setEmbedded(DeviceConfigsResponseDTO _embedded) {
        this._embedded = _embedded;
    }

    @JsonProperty("_links")
    public DeviceConfigLinksDTO getLinks() {
        if (_links == null) {
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
