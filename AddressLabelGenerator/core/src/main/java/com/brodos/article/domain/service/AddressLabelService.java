/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.article.domain.service;

import com.brodos.alg.domain.entity.AddressLabel;
import java.util.List;

/**
 *
 * @author padhaval
 */
public interface AddressLabelService {

    AddressLabel storeAddressLabelRequest(AddressLabel addressLabel);

    byte[] generateLabel(Long id, String formatType);

    public AddressLabel getAddressLabelInfoById(Long id);

    public List<AddressLabel> getAddressLabelInfoByTrackingCode(String trackingCode);
}
