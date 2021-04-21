/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.component;

import javax.annotation.PostConstruct;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.PublicationError;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author padhaval
 */
@Component
public class MBassadorComponent {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MBassadorComponent.class);

    private MBassador mBassador;

    @PostConstruct
    public void postConstruct() {
        IBusConfiguration config = new BusConfiguration()
                .addFeature(Feature.SyncPubSub.Default())
                .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                .addFeature(Feature.AsynchronousMessageDispatch.Default())
                .addPublicationErrorHandler((PublicationError error) -> {                    
                    LOG.error("Error in publishing event={}, {}", error.getPublishedMessage(), error.getMessage());
                    LOG.error("Cause={}", error.getCause());
                });

        mBassador = new MBassador(config);
    }    public MBassador getMBassador() {
        return mBassador;
    }
}
