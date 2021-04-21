/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.domain.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
@Entity
@Table(name = "freight_forwarder_client_config")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FreightForwarderClientConfig.findAll", query = "SELECT f FROM FreightForwarderClientConfig f")
    , @NamedQuery(name = "FreightForwarderClientConfig.findById", query = "SELECT f FROM FreightForwarderClientConfig f WHERE f.id = :id")
    , @NamedQuery(name = "FreightForwarderClientConfig.findByFreightForwarder", query = "SELECT f FROM FreightForwarderClientConfig f WHERE f.freightForwarder = :freightForwarder")
    , @NamedQuery(name = "FreightForwarderClientConfig.findByClient", query = "SELECT f FROM FreightForwarderClientConfig f WHERE f.client = :client")
    , @NamedQuery(name = "FreightForwarderClientConfig.findByKey", query = "SELECT f FROM FreightForwarderClientConfig f WHERE f.key = :key")
    , @NamedQuery(name = "FreightForwarderClientConfig.findByValue", query = "SELECT f FROM FreightForwarderClientConfig f WHERE f.value = :value")})
public class FreightForwarderClientConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 10)
    @Column(name = "freight_forwarder")
    private String freightForwarder;
    @Size(max = 45)
    @Column(name = "client")
    private String client;
    @Size(max = 45)
    @Column(name = "key")
    private String key;
    @Size(max = 45)
    @Column(name = "value")
    private String value;

    public FreightForwarderClientConfig() {
    }

    public FreightForwarderClientConfig(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFreightForwarder() {
        return freightForwarder;
    }

    public void setFreightForwarder(String freightForwarder) {
        this.freightForwarder = freightForwarder;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FreightForwarderClientConfig)) {
            return false;
        }
        FreightForwarderClientConfig other = (FreightForwarderClientConfig) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
