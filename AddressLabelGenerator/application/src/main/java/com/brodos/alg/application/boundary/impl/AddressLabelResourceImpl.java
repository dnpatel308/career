/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.application.boundary.impl;

import com.brodos.alg.application.assembler.AddressLabelAssembler;
import com.brodos.alg.domain.entity.AddressLabel;
import javax.inject.Named;
import javax.inject.Singleton;

import javax.ws.rs.core.Response;
import com.brodos.alg.application.boundary.AddressLabelResource;
import com.brodos.alg.application.dto.AddressLabelDTO;
import com.brodos.article.domain.service.DomainRegistryService;
import java.util.List;
import javax.ws.rs.core.Request;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author padhaval
 */
@Singleton
@Named("addressLabelResource")
public class AddressLabelResourceImpl implements AddressLabelResource {

    @Override
    public Response requestAddressLabelFor(Request request, AddressLabelDTO addressLabelDTO) {
        if (StringUtils.isBlank(addressLabelDTO.getClient())) {
            addressLabelDTO.setClient("BLM");
        }

        AddressLabel addressLabel = AddressLabelAssembler.toAddressLabel(addressLabelDTO, "v1");
        addressLabel = DomainRegistryService.instance().addressLabelService().storeAddressLabelRequest(addressLabel);
        AddressLabelAssembler.prepareResponse(request, addressLabelDTO, addressLabel);
        return Response.ok(addressLabelDTO).build();
    }

    @Override
    public Response generateAndDownloadAddressLabelPdf(Long id) {
        byte[] addressLabelData = DomainRegistryService.instance().addressLabelService().generateLabel(id, "application/pdf");
        return Response.ok(addressLabelData).build();
    }

    @Override
    public Response getAddressLabelInfoById(Request request, Long id) {
        AddressLabel addressLabel = DomainRegistryService.instance().addressLabelService().getAddressLabelInfoById(id);
        AddressLabelDTO addressLabelDTO = AddressLabelAssembler.toAddressLabelDTO(request, addressLabel);
        return Response.ok(addressLabelDTO).build();
    }

    @Override
    public Response getAddressLabelInfoByTrackingCode(Request request, String trackingCode) {
        List<AddressLabel> addressLabels = DomainRegistryService.instance().addressLabelService().getAddressLabelInfoByTrackingCode(trackingCode);
        List<AddressLabelDTO> toAddressLabelDTOs = AddressLabelAssembler.toAddressLabelDTOs(request, addressLabels);
        return Response.ok(toAddressLabelDTOs).build();
    }
}
