package com.brodos.device.reservation.test.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SerialDTO {

    private String number;
    @JsonProperty("warehouse-id")
    private Integer warehouseId;
    @JsonProperty("article-number")
    private String articleNumber;
    @JsonProperty("tenant-id")
    private Long tenantId;
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
