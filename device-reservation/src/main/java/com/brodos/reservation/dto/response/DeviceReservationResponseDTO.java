/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.dto.response;

import com.brodos.reservation.dto.ReservationReferenceDTO;
import com.brodos.reservation.entity.DeviceReservationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
@JsonInclude(Include.NON_NULL)
public class DeviceReservationResponseDTO {

    private Long id;
    private DeviceReservationStatus status;
    private Long bulkid;
    private String articleNo;
    private OpenCaseDTO opencase;
    private Set<ReservationReferenceDTO> references;
    private Boolean deviceRequired;
    private Boolean consignment;
    private String group;

    private JsonNode _embedded;
    private ReservationLinksDTO _links;

    @JsonIgnore
    private String serialNo;
    @JsonIgnore
    private Integer warehouseNo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeviceReservationStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceReservationStatus status) {
        this.status = status;
    }

    public Long getBulkid() {
        return bulkid;
    }

    public void setBulkid(Long bulkid) {
        this.bulkid = bulkid;
    }

    public String getArticleNo() {
        return articleNo;
    }

    public void setArticleNo(String articleNo) {
        this.articleNo = articleNo;
    }

    public OpenCaseDTO getOpencase() {
        return opencase;
    }

    public void setOpencase(OpenCaseDTO opencase) {
        this.opencase = opencase;
    }

    public Set<ReservationReferenceDTO> getReferences() {
        return references;
    }

    public void setReferences(Set<ReservationReferenceDTO> references) {
        this.references = references;
    }

    public Boolean getDeviceRequired() {
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @JsonProperty("_embedded")
    public JsonNode getEmbedded() {
        return _embedded;
    }

    public void setEmbedded(JsonNode _embedded) {
        this._embedded = _embedded;
    }

    @JsonProperty("_links")
    public ReservationLinksDTO getLinks() {
        if (_links == null) {
            this._links = new ReservationLinksDTO();
        }

        return _links;
    }

    public void setLinks(ReservationLinksDTO _links) {
        this._links = _links;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public Integer getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(Integer warehouseNo) {
        if (warehouseNo != null) {
            setGroup(warehouseNo.toString());
        }

        this.warehouseNo = warehouseNo;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
