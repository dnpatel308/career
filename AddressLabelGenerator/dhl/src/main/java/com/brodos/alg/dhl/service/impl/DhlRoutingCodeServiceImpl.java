/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dhl.service.impl;

import com.brodos.alg.dhl.command.GenerateGS1RoutingCodeCommand;
import com.brodos.alg.domain.exception.ALGException;
import com.brodos.alg.dhl.command.GenerateSimpleRoutingCodeCommand;
import com.brodos.alg.dhl.service.DhlRoutingCodeService;
import com.brodos.commons.config.Configuration;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.json.JSONObject;
import org.ops4j.pax.cdi.api.OsgiService;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
@Singleton
@Named("dhlRoutingCodeService")
public class DhlRoutingCodeServiceImpl implements DhlRoutingCodeService {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(DhlRoutingCodeServiceImpl.class);

    @Inject
    @OsgiService(filter = "(pid=com.brodos.context)")
    Configuration config;    

    @Override
    public String getRoutingCodeFor(String routingCodeType, JSONObject routingCodeData) {
        try {
            switch (routingCodeType) {                
                case "STANDARD": {
                    return new GenerateSimpleRoutingCodeCommand(config.getString("routingcode.api.url"), routingCodeData).execute();
                }

                case "GS1": {
                    return new GenerateGS1RoutingCodeCommand(config.getString("routingcode.gs1.api.url"), routingCodeData).execute();
                }

                default: {
                    throw new ALGException(10033, "Routing code generation strategy (" + routingCodeType + ") not supported.");
                }
            }
        } catch (Exception exception) {
            LOG.trace(exception.getMessage(), exception);
            if (!(exception instanceof ALGException || exception.getCause() instanceof ALGException)) {
                throw new ALGException(10003, "Unable to generate routing code", exception);
            } else {
                if (exception instanceof ALGException) {
                    throw (ALGException) exception;
                } else if (exception.getCause() instanceof ALGException) {
                    throw (ALGException) exception.getCause();
                } else {
                    throw new ALGException(10003, "Unable to generate routing code", exception);
                }
            }
        }
    }
}
