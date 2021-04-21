package com.brodos.devicecontext.model.dto.response;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceDTO {
    DeviceSearchResponseDTO device;
    private DeviceLinksDTO _links;

    public DeviceSearchResponseDTO getDevice() {
        return device;
    }

    public void setDevice(DeviceSearchResponseDTO device) {
        this.device = device;
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
