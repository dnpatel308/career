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
@Table(name = "countrykeys")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Countrykeys.findAll", query = "SELECT c FROM Countrykeys c")
    , @NamedQuery(name = "Countrykeys.findById", query = "SELECT c FROM Countrykeys c WHERE c.id = :id")
    , @NamedQuery(name = "Countrykeys.findByIsocodeAlpha2", query = "SELECT c FROM Countrykeys c WHERE c.isocodeAlpha2 = :isocodeAlpha2")
    , @NamedQuery(name = "Countrykeys.findByIsocodeAlpha3", query = "SELECT c FROM Countrykeys c WHERE c.isocodeAlpha3 = :isocodeAlpha3")
    , @NamedQuery(name = "Countrykeys.findByVehicleRegistrationCode", query = "SELECT c FROM Countrykeys c WHERE c.vehicleRegistrationCode = :vehicleRegistrationCode")
    , @NamedQuery(name = "Countrykeys.findByCountryCallingCode", query = "SELECT c FROM Countrykeys c WHERE c.countryCallingCode = :countryCallingCode")})
public class Countrykeys implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 2)
    @Column(name = "isocode_alpha_2")
    private String isocodeAlpha2;
    @Size(max = 3)
    @Column(name = "isocode_alpha_3")
    private String isocodeAlpha3;
    @Size(max = 3)
    @Column(name = "vehicle_registration_code")
    private String vehicleRegistrationCode;
    @Size(max = 5)
    @Column(name = "country_calling_code")
    private String countryCallingCode;

    public Countrykeys() {
    }

    public Countrykeys(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsocodeAlpha2() {
        return isocodeAlpha2;
    }

    public void setIsocodeAlpha2(String isocodeAlpha2) {
        this.isocodeAlpha2 = isocodeAlpha2;
    }

    public String getIsocodeAlpha3() {
        return isocodeAlpha3;
    }

    public void setIsocodeAlpha3(String isocodeAlpha3) {
        this.isocodeAlpha3 = isocodeAlpha3;
    }

    public String getVehicleRegistrationCode() {
        return vehicleRegistrationCode;
    }

    public void setVehicleRegistrationCode(String vehicleRegistrationCode) {
        this.vehicleRegistrationCode = vehicleRegistrationCode;
    }

    public String getCountryCallingCode() {
        return countryCallingCode;
    }

    public void setCountryCallingCode(String countryCallingCode) {
        this.countryCallingCode = countryCallingCode;
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
        if (!(object instanceof Countrykeys)) {
            return false;
        }
        Countrykeys other = (Countrykeys) object;
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
