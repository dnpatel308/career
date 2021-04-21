/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.service;

import com.brodos.reservation.dto.request.SerialNumberActionDTO;
import com.brodos.reservation.dto.request.SerialNumberRequestDTO;
import com.brodos.reservation.entity.SerialNumber;

/**
 *
 * @author padhaval
 */
public interface SerialNumberService {

    public SerialNumber importSerialNoInPool(SerialNumberRequestDTO serialNumberRequestDTO);

    public SerialNumber getSerialNumberById(Long id);

    public SerialNumber updateSerialNo(Long id, SerialNumberRequestDTO serialNumberRequestDTO);

    public SerialNumber doSerialNumberAction(Long id, SerialNumberActionDTO serialNumberActionDTO);

    public SerialNumber getSerialNumberByArticleNumberAndImei(String articleNo, String serialNo);
}
