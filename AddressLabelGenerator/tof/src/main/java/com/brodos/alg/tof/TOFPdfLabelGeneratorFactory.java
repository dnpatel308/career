/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.tof;

import com.brodos.alg.domain.LabelGenerator;
import com.brodos.alg.domain.LabelGeneratorFactoryRegistry;
import com.brodos.alg.domain.NamedLabelGeneratorFactory;
import com.brodos.commons.config.Configuration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
@OsgiServiceProvider(classes = {NamedLabelGeneratorFactory.class})
@Singleton
public class TOFPdfLabelGeneratorFactory implements NamedLabelGeneratorFactory {
    
    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(TOFPdfLabelGeneratorFactory.class);

    @Inject
    @OsgiService(filter = "(pid=com.brodos.context)")
    Configuration config;

    @Inject
    @OsgiService
    LabelGeneratorFactoryRegistry registry;
    
    private final List<String> tofStringKeys = new ArrayList<>();
    private final List<String> tofIntegerKeys = new ArrayList<>();
    private final List<String> tofExcludeFieldsFromValidation = new ArrayList<>();            

    @Override
    public String getFreightForwarder() {
        return "TOF";
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

    @Override
    public LabelGenerator createLabelGenerator() {                
        if (tofStringKeys.isEmpty()) {
            tofStringKeys.addAll(Arrays.asList(config.getString("application.request.fields.common.strings").split("\\|")));
            tofStringKeys.addAll(Arrays.asList(config.getString("application.request.fields.tof.strings").split("\\|")));
        }

        if (tofIntegerKeys.isEmpty()) {
            tofIntegerKeys.addAll(Arrays.asList(config.getString("application.request.fields.common.integers").split("\\|")));
            tofIntegerKeys.addAll(Arrays.asList(config.getString("application.request.fields.tof.integers").split("\\|")));
        }

        if (tofExcludeFieldsFromValidation.isEmpty()) {
            tofExcludeFieldsFromValidation.addAll(Arrays.asList(config.getString("application.common.exclude.fields.validation").split("\\|")));
            tofExcludeFieldsFromValidation.addAll(Arrays.asList(config.getString("application.tof.exclude.fields.validation").split("\\|")));
        }

        LOG.debug("tofStringKeys={}", tofStringKeys);
        LOG.debug("tofIntegerKeys={}", tofIntegerKeys);
        LOG.debug("tofExcludeFieldsFromValidation={}", tofExcludeFieldsFromValidation);
        
        return new TOFPdfLabelGenerator(config.getInteger("tof.qrcode.version"), tofStringKeys, tofIntegerKeys, tofExcludeFieldsFromValidation);                
    }
}
