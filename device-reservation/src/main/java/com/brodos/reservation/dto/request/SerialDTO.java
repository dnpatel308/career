/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.dto.request;

import com.brodos.reservation.dto.ProductCodeDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class SerialDTO {

    @NotBlank(message = "Invalid serial number")
    private String number;
    @NotNull(message = "Invalid warehouse-id")
    @Min(value = 1, message = "Invalid warehouse-id")
    @JsonProperty("warehouse-id")
    private Integer warehouseId;
    @NotBlank(message = "Article number is mandatory")
    @Pattern(regexp = "^([A-Za-z0-9-]*)", message = "Invalid article number")
    @JsonProperty("article-number")
    private String articleNumber;
    @NotNull(message = "Invalid tenant")
    @Min(value = 1, message = "Invalid tenant")
    @JsonProperty("tenant-id")
    private Long tenantId;
    @Valid
    @NotNull(message = "Invalid product codes")
    @Size(min = 1, max = 20, message = "Invalid product codes")
    @JsonProperty("productcodes")
    private List<ProductCodeDTO> productCodeDTOs;
    @JsonIgnore
    private JsonNode _embedded;

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

    public List<ProductCodeDTO> getProductCodeDTOs() {
        return productCodeDTOs;
    }

    public void setProductCodeDTOs(List<ProductCodeDTO> productCodeDTOs) {
        this.productCodeDTOs = productCodeDTOs;
    }

    public JsonNode getEmbedded() {
        return _embedded;
    }

    public void setEmbedded(JsonNode _embedded) {
        this._embedded = _embedded;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
