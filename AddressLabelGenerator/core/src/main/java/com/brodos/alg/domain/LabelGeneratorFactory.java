/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.domain;

import com.brodos.alg.domain.entity.FreightForwarder;
import com.brodos.commons.AssertionConcern;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
@OsgiServiceProvider(classes = {LabelGeneratorFactoryRegistry.class})
@Singleton
public class LabelGeneratorFactory implements LabelGeneratorFactoryRegistry {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(LabelGeneratorFactory.class);
    private static final Map<String, NamedLabelGeneratorFactory> LABEL_GENERATOR_FACTORIES_MAP = new HashMap<>();
    
    synchronized public static LabelGenerator getLabelGenerator(String outputFormat, FreightForwarder forwarder) {
        LOG.debug("getLabelGenerator for outputFormat={}, forwarder={}", outputFormat, forwarder.getKey());
        NamedLabelGeneratorFactory namedLabelGeneratorFactory = LABEL_GENERATOR_FACTORIES_MAP.get(getLabelGeneratorName(forwarder.getKey(), outputFormat));
        AssertionConcern.assertStateTrue(namedLabelGeneratorFactory != null,
                String.format("Label generator not found for outputFormat=%s and freightForwarder=%s registered.",
                        outputFormat, forwarder.getKey()));
        return namedLabelGeneratorFactory.createLabelGenerator();
    }

    @Override
    public void registerLabelGeneratorFactory(NamedLabelGeneratorFactory factory) {               
        LOG.debug("Registering LabelGenerator Factory={}", getLabelGeneratorFactoryName(factory));
        LABEL_GENERATOR_FACTORIES_MAP.putIfAbsent(getLabelGeneratorFactoryName(factory), factory);        
        LOG.debug("registered LabelGenerator Factories={}", LABEL_GENERATOR_FACTORIES_MAP);
    }

    @Override
    public void unRegisterLabelGeneratorFactory(NamedLabelGeneratorFactory factory) {
        LABEL_GENERATOR_FACTORIES_MAP.remove(getLabelGeneratorFactoryName(factory), factory);
    }

    private static String getLabelGeneratorFactoryName(NamedLabelGeneratorFactory factory) {
        return getLabelGeneratorName(factory.getFreightForwarder(), factory.getOutFormat());
    }

    private static String getLabelGeneratorName(String freightForwarderName, String outputFormat) {
        return String.format("%s/%s", freightForwarderName.toUpperCase(), outputFormat.toUpperCase());
    }
}
