/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.model.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
@Entity
@Table(name = "device_field")
public class DeviceField implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DeviceFieldPK deviceFieldPK;
    @Size(max = 45)
    @Column(name = "value")
    private String value;

    public DeviceFieldPK getDeviceFieldPK() {
        return deviceFieldPK;
    }

    public void setDeviceFieldPK(DeviceFieldPK deviceFieldPK) {
        this.deviceFieldPK = deviceFieldPK;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final DeviceField other = (DeviceField) obj;
        if (!Objects.equals(this.deviceFieldPK, other.deviceFieldPK)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
