package com.brodos.reservation.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
public class Customer extends Owner implements Serializable {

    private static final long serialVersionUID = 6782199470389352066L;

    @Embedded
    TenantId tenantId;

    @Column(name = "customer_number")
    private String customerNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "serial_no_reservation_count")
    private Integer serialNoReservationCount;

    @Column(name = "login_email", nullable = true)
    private String loginEmail;

    public Customer() {
    }

    public Customer(TenantId tenantId, String customerNumber, String email) {
        this.tenantId = tenantId;
        this.customerNumber = customerNumber;
        this.loginEmail = email;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSerialNoReservationCount() {
        return serialNoReservationCount;
    }

    public void setSerialNoReservationCount(Integer serialNoReservationCount) {
        this.serialNoReservationCount = serialNoReservationCount;
    }

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
