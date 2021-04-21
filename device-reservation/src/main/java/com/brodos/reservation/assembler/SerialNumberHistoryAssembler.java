/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.assembler;

import com.brodos.reservation.entity.SerialNumber;
import com.brodos.reservation.entity.SerialNumberHistory;
import java.util.Date;

/**
 *
 * @author padhaval
 */
public class SerialNumberHistoryAssembler {

    public SerialNumberHistory toSerialNumberHistory(SerialNumber serialNumber) {
        SerialNumberHistory serialNumberHistory = new SerialNumberHistory();
        serialNumberHistory.setArchived(serialNumber.getArchived());
        serialNumberHistory.setArticleNo(serialNumber.getArticle().getArticleId().getArticleNumber());

        if (serialNumber.getModifiedBy() == null) {
            serialNumberHistory.setCreatedBy(serialNumber.getCreatedBy());
        } else {
            serialNumberHistory.setCreatedBy(serialNumber.getModifiedBy());
        }

        serialNumberHistory.setCreatedDate(new Date());
        serialNumberHistory.setEan(serialNumber.getEan());
        serialNumberHistory.setRelocated(serialNumber.getRelocated());
        serialNumberHistory.setReservable(serialNumber.getReservable());
        serialNumberHistory.setSerialNumber(serialNumber);
        serialNumberHistory.setTicketNumber(serialNumber.getTicketNumber());
        serialNumberHistory.setWarehouseNo(serialNumber.getWarehouseNo());

        return serialNumberHistory;
    }
}
