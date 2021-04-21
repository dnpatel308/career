/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.domain.entity;

import com.brodos.alg.domain.util.JpaJsonConverter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONObject;

/**
 *
 * @author padhaval
 */
@Entity
@Table(name = "address_label")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AddressLabel.findAll", query = "SELECT a FROM AddressLabel a")
    , @NamedQuery(name = "AddressLabel.findById", query = "SELECT a FROM AddressLabel a WHERE a.id = :id")
    , @NamedQuery(name = "AddressLabel.findByTrackingCode", query = "SELECT a FROM AddressLabel a WHERE a.trackingCode = :trackingCode")})
public class AddressLabel implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Size(max = 45)
    @Column(name = "tracking_code")
    private String trackingCode;
    @Lob
    @Size(max = 2147483647)
    @Convert(converter = JpaJsonConverter.class)
    @Column(name = "request_json")
    private JSONObject requestJson; // NOSONAR
    @JoinColumn(name = "fk_freight_forwarder", referencedColumnName = "key")
    @ManyToOne
    private FreightForwarder freightForwarder;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_timestamp", insertable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Date createdDateAndTime;

    @Transient
    private Date deliveryTimestamp;

    @Transient
    String client;

    @Transient
    private byte[] labelRepresentation;

    public AddressLabel() {
    }

    public AddressLabel(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public JSONObject getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(JSONObject requestJson) {
        this.requestJson = requestJson;
    }

    public FreightForwarder getFreightForwarder() {
        return freightForwarder;
    }

    public void setFreightForwarder(FreightForwarder freightForwarder) {
        this.freightForwarder = freightForwarder;
    }

    public Date getDeliveryTimestamp() {
        return deliveryTimestamp;
    }

    public void setDeliveryTimestamp(Date deliveryTimestamp) {
        this.deliveryTimestamp = deliveryTimestamp;
    }

    public Date getCreatedDateAndTime() {
        return createdDateAndTime;
    }

    public void setCreatedDateAndTime(Date createdDateAndTime) {
        this.createdDateAndTime = createdDateAndTime;
    }

    public byte[] getLabelRepresentation() {
        return labelRepresentation;
    }

    public void setLabelRepresentation(byte[] labelRepresentation) {
        this.labelRepresentation = labelRepresentation;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
