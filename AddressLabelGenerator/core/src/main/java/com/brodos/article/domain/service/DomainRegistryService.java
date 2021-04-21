/*
 * Copyright (C) Brodos AG - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.brodos.article.domain.service;

import com.brodos.article.domain.persistence.TofRouteFigureKeyRepository;
import com.brodos.article.domain.persistence.TofCustomerNumbersRepository;
import com.brodos.article.domain.persistence.TofSevicecodesRepository;
import com.brodos.article.domain.persistence.SequenceFormatterRepository;
import com.brodos.article.domain.persistence.AddressLabelRepository;
import com.brodos.article.domain.persistence.DHLTimerangeCodesRepository;
import com.brodos.article.domain.persistence.FreightForwarderClientConfigRepository;
import com.brodos.article.domain.persistence.FreightForwarderRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ServiceScope;

/**
 *
 * @author Alexander Sahler <alexander.sahler at brodos.de>
 */
@Component(scope = ServiceScope.SINGLETON, immediate = true)
public class DomainRegistryService implements DomainRegistry {

    static DomainRegistryService instance;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile AddressLabelRepository addressLabelRepository;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile FreightForwarderRepository freightForwarderRepository;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile AddressLabelService addressLabelService;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile SequenceFormatterRepository sequenceFormatterRepository;
    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile CountrykeysRepository countrykeysRepository;
    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile TofCustomerNumbersRepository tofCustomerNumbersRepository;
    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile TofRouteFigureKeyRepository tofRouteFigureKeyRepository;
    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile TofSevicecodesRepository tofSevicecodesRepository;
    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile DHLTimerangeCodesRepository dHLTimerangeCodesRepository;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile FreightForwarderClientConfigRepository freightForwarderClientConfigRepository;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    volatile TofRouteFigureKeyService tofRouteFigureKeyService;

    @Activate
    void init() {
        instance = this; // NOSONAR
    }

    @Deactivate
    void unbind() {
        instance = null; // NOSONAR
    }

    static public DomainRegistry instance() {
        return instance;
    }

    @Override
    public AddressLabelRepository addressLabelRepository() {
        return addressLabelRepository;
    }

    @Override
    public FreightForwarderRepository freightForwarderRepository() {
        return freightForwarderRepository;
    }

    @Override
    public AddressLabelService addressLabelService() {
        return addressLabelService;
    }

    @Override
    public SequenceFormatterRepository sequenceFormatterRepository() {
        return sequenceFormatterRepository;
    }

    @Override
    public CountrykeysRepository countrykeysRepository() {
        return countrykeysRepository;
    }

    @Override
    public TofCustomerNumbersRepository tofCustomerNumbersRepository() {
        return tofCustomerNumbersRepository;
    }

    @Override
    public TofRouteFigureKeyRepository tofRouteFigureKeyRepository() {
        return tofRouteFigureKeyRepository;
    }

    @Override
    public TofSevicecodesRepository tofSevicecodesRepository() {
        return tofSevicecodesRepository;
    }

    @Override
    public DHLTimerangeCodesRepository dhlTimerangeCodesRepository() {
        return dHLTimerangeCodesRepository;
    }

    @Override
    public FreightForwarderClientConfigRepository freightForwarderClientConfigRepository() {
        return freightForwarderClientConfigRepository;
    }

    @Override
    public TofRouteFigureKeyService tofRouteFigureKeyService() {
        return tofRouteFigureKeyService;
    }

}
