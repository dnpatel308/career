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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brodos.alg.application.boundary.AddressLabelResourceV2;
import com.brodos.alg.application.dto.AddressLabelDTO;
import com.brodos.article.domain.service.DomainRegistryService;
import java.util.List;
import javax.ws.rs.core.Request;

/**
 *
 * @author padhaval
 */
@Singleton
@Named("addressLabelResourcev2")
public class AddressLabelResourceImplV2 implements AddressLabelResourceV2 {

    private static final Logger LOG = LoggerFactory.getLogger(AddressLabelResourceImplV2.class);

    @Override
    public Response requestAddressLabelFor(Request request, AddressLabelDTO addressLabelDTO) {
        LOG.debug("Generating Label for version v2");
        LOG.debug("Request Data = {}", addressLabelDTO);
        AddressLabel addressLabel = AddressLabelAssembler.toAddressLabel(addressLabelDTO, "v2");
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
