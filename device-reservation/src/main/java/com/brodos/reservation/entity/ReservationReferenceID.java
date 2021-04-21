/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
@Embeddable
public class ReservationReferenceID implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "label")
    private String label;
    @Basic(optional = false)
    @NotNull
    @JoinColumn(name = "serial_number_res_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private SerialNumberReservation serialNumberReservation;

    public ReservationReferenceID() {
    }

    public ReservationReferenceID(String label, SerialNumberReservation serialNumberReservation) {
        this.label = label;
        this.serialNumberReservation = serialNumberReservation;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public SerialNumberReservation getSerialNumberReservation() {
        return serialNumberReservation;
    }

    public void setSerialNumberReservation(SerialNumberReservation serialNumberReservation) {
        this.serialNumberReservation = serialNumberReservation;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReservationReferenceID other = (ReservationReferenceID) obj;
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.serialNumberReservation, other.serialNumberReservation)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
