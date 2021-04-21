/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.assembler;

import com.brodos.reservation.Constants;
import com.brodos.reservation.dto.ProductCodeDTO;
import com.brodos.reservation.dto.request.SerialDTO;
import com.brodos.reservation.dto.request.SerialNumberRequestDTO;
import com.brodos.reservation.dto.response.LinkDTO;
import com.brodos.reservation.dto.response.SerialNumberResponseDTO;
import com.brodos.reservation.entity.Article;
import com.brodos.reservation.entity.EAN;
import com.brodos.reservation.entity.SerialNumber;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author padhaval
 */
public class SerialNumberAssembler {

    public SerialNumber toSerialNumber(SerialNumberRequestDTO serialNumberRequestDTO, Article article,
        SerialNumber serialNumber) {
        SerialDTO serialDTO = serialNumberRequestDTO.getSerialDTO();

        if (serialNumber == null) {
            serialNumber = new SerialNumber();
            serialNumber.setReservable(Boolean.TRUE);
            serialNumber.setCreatedBy(Constants.USER_NAME);
            serialNumber.setCreatedDate(new Date());
            serialNumber.setArchived(false);
        } else {
            serialNumber.setModifiedBy(Constants.USER_NAME);
            serialNumber.setModifiedDate(new Date());
        }

        serialNumber.setNumber(serialDTO.getNumber());
        serialNumber.setWarehouseNo(serialDTO.getWarehouseId());
        serialNumber.setArticle(article);
        serialNumber.setEan(serialDTO.getProductCodeDTOs().stream().findFirst().get().getValue());
        return serialNumber;
    }

    public SerialNumberResponseDTO toSerialNumberResponseDTO(String requestUrl, SerialNumber serialNumber) {
        SerialNumberResponseDTO serialNumberResponseDTO = new SerialNumberResponseDTO();
        com.brodos.reservation.dto.response.SerialDTO serialDTO = new com.brodos.reservation.dto.response.SerialDTO();
        serialDTO.setId(serialNumber.getId());
        serialDTO.setArticleNumber(serialNumber.getArticle().getArticleId().getArticleNumber());
        serialDTO.setNumber(serialNumber.getNumber());
        serialDTO.setTenantId(serialNumber.getArticle().getArticleId().getTenantId().getTenant());
        serialDTO.setWarehouseId(serialNumber.getWarehouseNo());
        serialDTO.setIsReservable(serialNumber.getReservable());
        serialDTO.setIsArchived(serialNumber.getArchived());
        serialDTO.setCreatedBy(serialNumber.getCreatedBy());
        serialDTO.setCreatedAt(serialNumber.getCreatedDate());
        serialDTO.setChangedBy(serialNumber.getModifiedBy());
        serialDTO.setChangedAt(serialNumber.getModifiedDate());
        serialDTO.setIsRelocated(serialNumber.getRelocated());
        serialDTO.setTicketNumber(serialNumber.getTicketNumber());
        serialDTO.setProductCodeDTOs(new ArrayList<>());
        for (EAN ean : serialNumber.getArticle().getArticleEAN()) {
            ProductCodeDTO productCodeDTO = new ProductCodeDTO();
            productCodeDTO.setType("EAN");
            productCodeDTO.setValue(ean.getCode());
            serialDTO.getProductCodeDTOs().add(productCodeDTO);
        }
        
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(StringUtils.substringBeforeLast(requestUrl, "serials"));
        requestBuilder.append("serials/");
        requestBuilder.append(serialDTO.getId());
        
        serialDTO.getLinks().setSelf(new LinkDTO(requestBuilder.toString()));
        serialNumberResponseDTO.setSerialDTO(serialDTO);
        return serialNumberResponseDTO;
    }
}
