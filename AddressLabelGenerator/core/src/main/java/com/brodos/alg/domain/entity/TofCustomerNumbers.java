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
@Table(name = "tof_customer_numbers")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TofCustomerNumbers.findAll", query = "SELECT t FROM TofCustomerNumbers t")
    , @NamedQuery(name = "TofCustomerNumbers.findByCustomerNumber", query = "SELECT t FROM TofCustomerNumbers t WHERE t.customerNumber = :customerNumber")
    , @NamedQuery(name = "TofCustomerNumbers.findByBarcodeNumber", query = "SELECT t FROM TofCustomerNumbers t WHERE t.barcodeNumber = :barcodeNumber")
    , @NamedQuery(name = "TofCustomerNumbers.findByDescription", query = "SELECT t FROM TofCustomerNumbers t WHERE t.description = :description")})
public class TofCustomerNumbers implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "customer_number")
    private Integer customerNumber;
    @Size(max = 5)
    @Column(name = "barcode_number")
    private String barcodeNumber;
    @Size(max = 255)
    @Column(name = "description")
    private String description;

    public TofCustomerNumbers() {
    }

    public TofCustomerNumbers(Integer customerNumber) {
        this.customerNumber = customerNumber;
    }

    public Integer getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(Integer customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getBarcodeNumber() {
        return barcodeNumber;
    }

    public void setBarcodeNumber(String barcodeNumber) {
        this.barcodeNumber = barcodeNumber;
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
        hash += (customerNumber != null ? customerNumber.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TofCustomerNumbers)) {
            return false;
        }
        TofCustomerNumbers other = (TofCustomerNumbers) object;
        if ((this.customerNumber == null && other.customerNumber != null) || (this.customerNumber != null && !this.customerNumber.equals(other.customerNumber))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
