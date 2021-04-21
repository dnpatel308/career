package com.brodos.devicecontext.model.dto.response;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PageDTO {
    private int size;
    private long totalelements;
    private int totalpages;
    private int number;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalelements() {
        return totalelements;
    }

    public void setTotalelements(long totalelements) {
        this.totalelements = totalelements;
    }

    public int getTotalpages() {
        return totalpages;
    }

    public void setTotalpages(int totalpages) {
        this.totalpages = totalpages;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
