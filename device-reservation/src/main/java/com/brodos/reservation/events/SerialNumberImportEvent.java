/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.events;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class SerialNumberImportEvent {

    private int toWarehouseNo;
    private String articleNo;
    private String serialNo;
    private Long tenantId;
    private String user;
    boolean restoreRequest;

    public int getToWarehouseNo() {
        return toWarehouseNo;
    }

    public void setToWarehouseNo(int toWarehouseNo) {
        this.toWarehouseNo = toWarehouseNo;
    }

    public String getArticleNo() {
        return articleNo;
    }

    public void setArticleNo(String articleNo) {
        this.articleNo = articleNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isRestoreRequest() {
        return restoreRequest;
    }

    public void setRestoreRequest(boolean restoreRequest) {
        this.restoreRequest = restoreRequest;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
