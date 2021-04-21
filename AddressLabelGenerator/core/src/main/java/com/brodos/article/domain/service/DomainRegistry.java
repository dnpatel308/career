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

/**
 *
 * @author Alexander Sahler <alexander.sahler at brodos.de>
 */
public interface DomainRegistry {

    AddressLabelRepository addressLabelRepository();

    FreightForwarderRepository freightForwarderRepository();

    SequenceFormatterRepository sequenceFormatterRepository();

    CountrykeysRepository countrykeysRepository();

    TofCustomerNumbersRepository tofCustomerNumbersRepository();

    TofRouteFigureKeyRepository tofRouteFigureKeyRepository();

    TofSevicecodesRepository tofSevicecodesRepository();

    AddressLabelService addressLabelService();

    DHLTimerangeCodesRepository dhlTimerangeCodesRepository();

    FreightForwarderClientConfigRepository freightForwarderClientConfigRepository();

    TofRouteFigureKeyService tofRouteFigureKeyService();
}
