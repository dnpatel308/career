/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.service.impl;

import com.brodos.alg.domain.LabelGenerator;
import com.brodos.alg.domain.LabelGeneratorFactory;
import com.brodos.alg.domain.entity.AddressLabel;
import com.brodos.alg.domain.exception.ALGException;
import com.brodos.article.domain.service.AddressLabelService;
import com.brodos.article.domain.service.DomainRegistryService;
import java.util.List;
import javax.inject.Singleton;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
@OsgiServiceProvider(classes = {AddressLabelService.class})
@Singleton
public class AddressLabelServiceImpl implements AddressLabelService {

    private static final Logger LOG = LoggerFactory.getLogger(AddressLabelServiceImpl.class);

    @Override
    public AddressLabel storeAddressLabelRequest(AddressLabel addressLabel) {
        LOG.debug("Generating label for format = {}, FreightForwarder = {}", addressLabel.getRequestJson().getString("documentFormat"), addressLabel.getFreightForwarder());
        LabelGenerator labelGenerator = LabelGeneratorFactory.getLabelGenerator(addressLabel.getRequestJson().getString("documentFormat"), addressLabel.getFreightForwarder());
        LOG.debug("Got LabelGenerator of Type = {}", labelGenerator.getClass());
        LOG.debug("RequestJson={}", addressLabel.getRequestJson());
        labelGenerator.validateRequest(addressLabel);
        return DomainRegistryService.instance().addressLabelRepository().store(addressLabel);
    }

    @Override
    public byte[] generateLabel(Long id, String formatType) {
        AddressLabel addressLabel = DomainRegistryService.instance().addressLabelRepository().findByPk(id);

        if (addressLabel == null) {
            throw new ALGException(10005, "Address label not generated for given id");
        }

        LabelGenerator labelGenerator = LabelGeneratorFactory.getLabelGenerator(addressLabel.getRequestJson().getString("documentFormat"), addressLabel.getFreightForwarder());
        addressLabel = labelGenerator.generateLabel(addressLabel);

        return addressLabel.getLabelRepresentation();
    }

    @Override
    public AddressLabel getAddressLabelInfoById(Long id) {
        LOG.info("Getting label info by id={}", id);
        AddressLabel addressLabel = DomainRegistryService.instance().addressLabelRepository().findByPk(id);

        if (addressLabel == null) {
            throw new ALGException(10005, "Address label not generated for given id");
        }
        
        return addressLabel;
    }

    @Override
    public List<AddressLabel> getAddressLabelInfoByTrackingCode(String trackingCode) {
        LOG.info("Getting label info by trackingCode={}", trackingCode);
        List<AddressLabel> addressLabels = DomainRegistryService.instance().addressLabelRepository().findByTrackingCode(trackingCode);
        
        if (addressLabels == null || addressLabels.isEmpty()) {
            throw new ALGException(10035, "No Address label generated for given tracking code");
        }
        
        return addressLabels;
    }
}
