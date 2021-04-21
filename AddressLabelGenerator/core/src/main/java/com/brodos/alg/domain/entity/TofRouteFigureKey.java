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
@Table(name = "tof_route_figure_key")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TofRouteFigureKey.findAll", query = "SELECT t FROM TofRouteFigureKey t")
    , @NamedQuery(name = "TofRouteFigureKey.findById", query = "SELECT t FROM TofRouteFigureKey t WHERE t.id = :id")
    , @NamedQuery(name = "TofRouteFigureKey.findByVehicleRegistrationCode", query = "SELECT t FROM TofRouteFigureKey t WHERE t.vehicleRegistrationCode = :vehicleRegistrationCode")
    , @NamedQuery(name = "TofRouteFigureKey.findByPostalRangeFrom", query = "SELECT t FROM TofRouteFigureKey t WHERE t.postalRangeFrom = :postalRangeFrom")
    , @NamedQuery(name = "TofRouteFigureKey.findByPostalRangeTo", query = "SELECT t FROM TofRouteFigureKey t WHERE t.postalRangeTo = :postalRangeTo")
    , @NamedQuery(name = "TofRouteFigureKey.findByDepotId", query = "SELECT t FROM TofRouteFigureKey t WHERE t.depotId = :depotId")
    , @NamedQuery(name = "TofRouteFigureKey.findByDepotSupplement", query = "SELECT t FROM TofRouteFigureKey t WHERE t.depotSupplement = :depotSupplement")
    , @NamedQuery(name = "TofRouteFigureKey.findByDepotAbbreviation", query = "SELECT t FROM TofRouteFigureKey t WHERE t.depotAbbreviation = :depotAbbreviation")
    , @NamedQuery(name = "TofRouteFigureKey.findByDepotLocation", query = "SELECT t FROM TofRouteFigureKey t WHERE t.depotLocation = :depotLocation")
    , @NamedQuery(name = "TofRouteFigureKey.findByCountryCallingCode", query = "SELECT t FROM TofRouteFigureKey t WHERE t.countryCallingCode = :countryCallingCode")})
public class TofRouteFigureKey implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Size(max = 3)
    @Column(name = "vehicle_registration_code")
    private String vehicleRegistrationCode;
    @Size(max = 6)
    @Column(name = "postal_range_from")
    private String postalRangeFrom;
    @Size(max = 6)
    @Column(name = "postal_range_to")
    private String postalRangeTo;
    @Size(max = 3)
    @Column(name = "depot_id")
    private String depotId;
    @Size(max = 2)
    @Column(name = "depot_supplement")
    private String depotSupplement;
    @Size(max = 2)
    @Column(name = "depot_abbreviation")
    private String depotAbbreviation;
    @Size(max = 30)
    @Column(name = "depot_location")
    private String depotLocation;
    @Size(max = 5)
    @Column(name = "country_calling_code")
    private String countryCallingCode;

    public TofRouteFigureKey() {
    }

    public TofRouteFigureKey(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVehicleRegistrationCode() {
        return vehicleRegistrationCode;
    }

    public void setVehicleRegistrationCode(String vehicleRegistrationCode) {
        this.vehicleRegistrationCode = vehicleRegistrationCode;
    }

    public String getPostalRangeFrom() {
        return postalRangeFrom;
    }

    public void setPostalRangeFrom(String postalRangeFrom) {
        this.postalRangeFrom = postalRangeFrom;
    }

    public String getPostalRangeTo() {
        return postalRangeTo;
    }

    public void setPostalRangeTo(String postalRangeTo) {
        this.postalRangeTo = postalRangeTo;
    }

    public String getDepotId() {
        return depotId;
    }

    public void setDepotId(String depotId) {
        this.depotId = depotId;
    }

    public String getDepotSupplement() {
        return depotSupplement;
    }

    public void setDepotSupplement(String depotSupplement) {
        this.depotSupplement = depotSupplement;
    }

    public String getDepotAbbreviation() {
        return depotAbbreviation;
    }

    public void setDepotAbbreviation(String depotAbbreviation) {
        this.depotAbbreviation = depotAbbreviation;
    }

    public String getDepotLocation() {
        return depotLocation;
    }

    public void setDepotLocation(String depotLocation) {
        this.depotLocation = depotLocation;
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
        if (!(object instanceof TofRouteFigureKey)) {
            return false;
        }
        TofRouteFigureKey other = (TofRouteFigureKey) object;
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
