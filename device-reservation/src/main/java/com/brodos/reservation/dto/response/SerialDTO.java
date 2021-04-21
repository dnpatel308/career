/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.dto.response;

import com.brodos.reservation.dto.ProductCodeDTO;
import com.brodos.reservation.utility.EventDateSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SerialDTO {

    private Long id;
    private String number;
    @JsonProperty("warehouse-id")
    private Integer warehouseId;
    @JsonProperty("article-number")
    private String articleNumber;
    @JsonProperty("tenant-id")
    private Long tenantId;
    @JsonProperty("is-archived")
    private Boolean isArchived;
    @JsonProperty("is-reservable")
    private Boolean isReservable;
    @JsonProperty("is-relocated")
    private Boolean isRelocated;
    @JsonProperty("ticket-number")
    private String ticketNumber;
    @JsonProperty("created-by")
    private String createdBy;
    @JsonSerialize(using = EventDateSerializer.class)
    @JsonProperty("created-at")
    private Date createdAt;
    @JsonProperty("changed-by")
    private String changedBy;
    @JsonSerialize(using = EventDateSerializer.class)
    @JsonProperty("changed-at")
    private Date changedAt;
    private JsonNode _embedded;
    private SerialNumberLinksDTO _links;
    @JsonProperty("productcodes")
    private List<ProductCodeDTO> productCodeDTOs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public Boolean getIsReservable() {
        return isReservable;
    }

    public void setIsReservable(Boolean isReservable) {
        this.isReservable = isReservable;
    }

    public Boolean getIsRelocated() {
        return isRelocated;
    }

    public void setIsRelocated(Boolean isRelocated) {
        this.isRelocated = isRelocated;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public Date getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }

    @JsonProperty("_embedded")
    public JsonNode getEmbedded() {
        return _embedded;
    }

    public void setEmbedded(JsonNode _embedded) {
        this._embedded = _embedded;
    }

    @JsonProperty("_links")
    public SerialNumberLinksDTO getLinks() {
        if (_links == null) {
            this._links = new SerialNumberLinksDTO();
        }

        return _links;
    }

    public void setLinks(SerialNumberLinksDTO _links) {
        this._links = _links;
    }

    public List<ProductCodeDTO> getProductCodeDTOs() {
        return productCodeDTOs;
    }

    public void setProductCodeDTOs(List<ProductCodeDTO> productCodeDTOs) {
        this.productCodeDTOs = productCodeDTOs;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
