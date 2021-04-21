package com.brodos.alg.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "isAllRecordCreated", "totalRecord" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TofRouteFigureKeyDTO {
    Boolean isAllRecordCreated;
    int totalRecord;

    public Boolean getIsAllRecordCreated() {
        return isAllRecordCreated;
    }

    public void setIsAllRecordCreated(Boolean isAllRecordCreated) {
        this.isAllRecordCreated = isAllRecordCreated;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecordAdded) {
        this.totalRecord = totalRecordAdded;
    }

}
