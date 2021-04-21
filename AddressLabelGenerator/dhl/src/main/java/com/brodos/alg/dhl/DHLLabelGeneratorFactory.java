/*
 * Copyright (C) Brodos AG - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.brodos.alg.dhl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.brodos.alg.dhl.service.DhlRoutingCodeService;
import com.brodos.alg.dhl.service.DhlTrackingCodeService;
import com.brodos.alg.domain.LabelGenerator;
import com.brodos.alg.domain.LabelGeneratorFactoryRegistry;
import com.brodos.alg.domain.NamedLabelGeneratorFactory;
import com.brodos.commons.config.Configuration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alexander Sahler <alexander.sahler at brodos.de>
 */
@OsgiServiceProvider(classes = {NamedLabelGeneratorFactory.class})
@Singleton
public class DHLLabelGeneratorFactory implements NamedLabelGeneratorFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DHLPdfLabelGenerator.class);

    @Inject
    @OsgiService(filter = "(pid=com.brodos.context)")
    Configuration config;

    @Inject
    @OsgiService
    LabelGeneratorFactoryRegistry registry;

    @Inject
    DhlTrackingCodeService dhlTrackingCodeService;

    @Inject
    DhlRoutingCodeService dhlRoutingCodeService;

    private final List<String> dhlStringKeys = new ArrayList<>();
    private final List<String> dhlIntegerKeys = new ArrayList<>();
    private final List<String> dhlExcludeFieldsFromValidation = new ArrayList<>();
    private final Map<String, List<String>> clientSpecificExcludes = new HashMap<>();

    @Override
    public LabelGenerator createLabelGenerator() {
        if (dhlStringKeys.isEmpty()) {
            dhlStringKeys.addAll(Arrays.asList(config.getString("application.request.fields.common.strings").split("\\|")));
            dhlStringKeys.addAll(Arrays.asList(config.getString("application.request.fields.dhl.strings").split("\\|")));
        }

        if (dhlIntegerKeys.isEmpty()) {
            dhlIntegerKeys.addAll(Arrays.asList(config.getString("application.request.fields.common.integers").split("\\|")));
            dhlIntegerKeys.addAll(Arrays.asList(config.getString("application.request.fields.dhl.integers").split("\\|")));
        }

        if (dhlExcludeFieldsFromValidation.isEmpty()) {
            dhlExcludeFieldsFromValidation.addAll(Arrays.asList(config.getString("application.common.exclude.fields.validation").split("\\|")));
            dhlExcludeFieldsFromValidation.addAll(Arrays.asList(config.getString("application.dhl.exclude.fields.validation").split("\\|")));
        }

        if (clientSpecificExcludes.isEmpty()) {
            List<String> strings = Arrays.asList(config.getString("application.dhl.clients").split("\\|"));
            for (String client : strings) {
                String string = config.getString("client." + client + ".exclude.fields");
                List<String> excludes = null;
                if (!StringUtils.isBlank(string)) {
                    excludes = Arrays.asList(string.split("\\|"));
                }
                clientSpecificExcludes.put(client, excludes);
            }
        }

        LOG.debug("dhlStringKeys={}", dhlStringKeys);
        LOG.debug("dhlIntegerKeys={}", dhlIntegerKeys);
        LOG.debug("dhlExcludeFieldsFromValidation={}", dhlExcludeFieldsFromValidation);
        LOG.debug("clientSpecificExcludes={}", clientSpecificExcludes);

        return new DHLPdfLabelGenerator(dhlTrackingCodeService, dhlRoutingCodeService, dhlStringKeys, dhlIntegerKeys, dhlExcludeFieldsFromValidation, clientSpecificExcludes);
    }

    @Override
    public String getFreightForwarder() {
        return "DHL";
    }

    @Override
    public String getOutFormat() {
        return "PDF";
    }

    @PostConstruct
    public void init() {
        registry.registerLabelGeneratorFactory(this);
    }

    @PreDestroy
    public void unregister() {
        registry.unRegisterLabelGeneratorFactory(this);
    }
}
