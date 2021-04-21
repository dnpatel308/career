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
public class EmbeddedDevicesDTO {

    private DevicesDTO _embedded;
    private PageDTO _page;
    private DeviceLinksDTO _links;

    @JsonProperty("_embedded")
    public DevicesDTO getEmbedded() {
        return _embedded;
    }

    public void setEmbedded(DevicesDTO _embedded) {
        this._embedded = _embedded;
    }

    @JsonProperty("_page")
    public PageDTO getPage() {
        return _page;
    }

    public void setPage(PageDTO _page) {
        this._page = _page;
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
