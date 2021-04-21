/*
 * Copyright (C) Brodos AG - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.brodos.alg.service.impl;

import com.brodos.commons.domain.model.HealthCheck;
import com.brodos.commons.domain.model.health.HealthReport;
import javax.inject.Singleton;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.ops4j.pax.cdi.api.Properties;
import org.ops4j.pax.cdi.api.Property;

/**
 *
 * @author Alexander Sahler <alexander.sahler at brodos.de>
 */
@OsgiServiceProvider(classes = {HealthCheck.class})
@Properties({
    @Property(name = "mainHealthCheck", value = "true")
})
@Singleton
public class MainHealthCheckImpl implements HealthCheck {

    @Override
    public HealthReport checkHealth() {
        return HealthReport.builder().serviceName("addresslabelgenerator").success(Boolean.TRUE).build();
    }
}
