/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.model.dto.request;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class SearchDevicesDTO {

    private String articlenumber;
    private SearchFieldsDTO fields;
    private Integer _page;
    private Integer _size;

    public String getArticlenumber() {
        return articlenumber;
    }

    public void setArticlenumber(String articlenumber) {
        this.articlenumber = articlenumber;
    }

    public SearchFieldsDTO getFields() {
        if (fields == null) {
            setFields(new SearchFieldsDTO());
        }

        return fields;
    }

    public void setFields(SearchFieldsDTO fields) {
        this.fields = fields;
    }

    public Integer getPage() {
        return _page;
    }

    public void setPage(Integer _page) {
        this._page = _page;
    }

    public Integer getSize() {
        return _size;
    }

    public void setSize(Integer _size) {
        this._size = _size;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
