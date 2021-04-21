/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author padhaval
 */
@JsonPropertyOrder({"id", "self", "printSize", "documentFormat", "client", "freightForwarder", "weight", "packageNoOutOfTotalPackages", "trackingCode", "totalNoOfPackages", "freight", "cod", "deliveryTimestamp", "deliveryTimeRange", "sender", "recipient"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressLabelDTO {

    public enum PrintSize {
        A6, LASERPRINTER, DEFAULT
    }

    @NotNull(message = "Field 'print size' is blank. Please provide a valid 'print size'")
    private PrintSize printSize;
    @NotBlank(message = "Field 'document format' is blank. Please provide a valid 'document format'")
    private String documentFormat;
    private String client;
    @NotNull(message = "Field 'freight forwarder' is blank. Please provide a valid 'freight forwarder'")
    private FreightForwarderDTO freightForwarder;
    @Valid
    @NotNull(message = "Field 'weight' is blank. Please provide a valid 'weight'")
    private WeightDTO weight;
    @NotNull(message = "Field 'package number out of total packages' is blank. Please provide a valid 'package number out of total packages'")
    private Integer packageNoOutOfTotalPackages;
    private String trackingCode;
    @JsonIgnore
    private String routingCode;
    @NotNull(message = "Field 'total number of packages' is blank. Please provide a valid 'total number of packages'")
    private Integer totalNoOfPackages;
    private FreightDTO freight;
    private CODDTO cod;
    private String deliveryTimestamp;
    @Valid
    @NotNull(message = "Field 'sender address' is blank. Please provide a valid 'sender address'")
    private AddressDTO sender;
    @Valid
    @NotNull(message = "Field 'recipient address' is blank. Please provide a valid 'recipient address'")
    private AddressDTO recipient;
    private String self;
    private String id;

    private Map<String, Object> labelSettings = new HashMap<String, Object>() {
        {
            put("labelWidth", 0);
            put("labelHeight", 0);
            put("labelRotation", 0);
            put("qrcodeDPI", 0);
            put("barcodeDPI", 0);
        }
    };

    public AddressLabelDTO() {

    }

    public PrintSize getPrintSize() {
        return printSize;
    }

    public void setPrintSize(PrintSize printSize) {
        this.printSize = printSize;
    }

    public String getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(String documentFormat) {
        this.documentFormat = documentFormat;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public FreightForwarderDTO getFreightForwarder() {
        return freightForwarder;
    }

    public void setFreightForwarder(FreightForwarderDTO freightForwarder) {
        this.freightForwarder = freightForwarder;
    }

    public Integer getPackageNoOutOfTotalPackages() {
        return packageNoOutOfTotalPackages;
    }

    public void setPackageNoOutOfTotalPackages(Integer packageNoOutOfTotalPackages) {
        this.packageNoOutOfTotalPackages = packageNoOutOfTotalPackages;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public Integer getTotalNoOfPackages() {
        return totalNoOfPackages;
    }

    public void setTotalNoOfPackages(Integer totalNoOfPackages) {
        this.totalNoOfPackages = totalNoOfPackages;
    }

    public FreightDTO getFreight() {
        return freight;
    }

    public void setFreight(FreightDTO freight) {
        this.freight = freight;
    }

    public CODDTO getCod() {
        return cod;
    }

    public void setCod(CODDTO cod) {
        this.cod = cod;
    }

    public WeightDTO getWeight() {
        return weight;
    }

    public void setWeight(WeightDTO weight) {
        this.weight = weight;
    }

    public String getDeliveryTimestamp() {
        return deliveryTimestamp;
    }

    public void setDeliveryTimestamp(String deliveryTimestamp) {
        this.deliveryTimestamp = deliveryTimestamp;
    }

    public AddressDTO getSender() {
        return sender;
    }

    public void setSender(AddressDTO sender) {
        this.sender = sender;
    }

    public AddressDTO getRecipient() {
        return recipient;
    }

    public void setRecipient(AddressDTO recipient) {
        this.recipient = recipient;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getLabelSettings() {
        return labelSettings;
    }

    public void setLabelSettings(Map<String, Object> labelSettings) {
        this.labelSettings = labelSettings;
    }

    public String getRoutingCode() {
        return routingCode;
    }

    public void setRoutingCode(String routingCode) {
        this.routingCode = routingCode;
    }       

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
