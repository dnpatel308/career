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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
@Entity
@Table(name = "tof_sevicecodes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TofSevicecodes.findAll", query = "SELECT t FROM TofSevicecodes t")
    , @NamedQuery(name = "TofSevicecodes.findByServiceCode", query = "SELECT t FROM TofSevicecodes t WHERE t.serviceCode = :serviceCode")
    , @NamedQuery(name = "TofSevicecodes.findByServiceName", query = "SELECT t FROM TofSevicecodes t WHERE t.serviceName = :serviceName")
    , @NamedQuery(name = "TofSevicecodes.findByDescription", query = "SELECT t FROM TofSevicecodes t WHERE t.description = :description")})
public class TofSevicecodes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "service_code")
    private Integer serviceCode;
    @Size(max = 10)
    @Column(name = "service_name")
    private String serviceName;
    @Size(max = 255)
    @Column(name = "description")
    private String description;

    public TofSevicecodes() {
    }

    public TofSevicecodes(Integer serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Integer getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(Integer serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceCode != null ? serviceCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TofSevicecodes)) {
            return false;
        }
        TofSevicecodes other = (TofSevicecodes) object;
        if ((this.serviceCode == null && other.serviceCode != null) || (this.serviceCode != null && !this.serviceCode.equals(other.serviceCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
