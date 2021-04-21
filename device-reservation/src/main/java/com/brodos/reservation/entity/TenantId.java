package com.brodos.reservation.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Embeddable
public class TenantId implements Serializable {

    private static final long serialVersionUID = 6959351441364056113L;

    @Column(name = "tenant_id")
    private Long tenant;

    public TenantId() {

    }

    public TenantId(Long tenant) {
        this.tenant = tenant;
    }

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
