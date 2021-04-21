/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.domain.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
@Entity
@Table(name = "freight_forwarder")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FreightForwarder.findAll", query = "SELECT f FROM FreightForwarder f")
    , @NamedQuery(name = "FreightForwarder.findByKey", query = "SELECT f FROM FreightForwarder f WHERE f.key = :key")
    , @NamedQuery(name = "FreightForwarder.findByDescription", query = "SELECT f FROM FreightForwarder f WHERE f.description = :description")})
public class FreightForwarder implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "key")
    private String key;
    @Size(max = 255)
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "freightForwarder")
    private Collection<AddressLabel> addressLabelCollection;

    public FreightForwarder() {
    }

    public FreightForwarder(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public Collection<AddressLabel> getAddressLabelCollection() {
        return addressLabelCollection;
    }

    public void setAddressLabelCollection(Collection<AddressLabel> addressLabelCollection) {
        this.addressLabelCollection = addressLabelCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (key != null ? key.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FreightForwarder)) {
            return false;
        }
        FreightForwarder other = (FreightForwarder) object;
        if ((this.key == null && other.key != null) || (this.key != null && !this.key.equals(other.key))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
         return ToStringBuilder.reflectionToString(this);
    }
    
}
