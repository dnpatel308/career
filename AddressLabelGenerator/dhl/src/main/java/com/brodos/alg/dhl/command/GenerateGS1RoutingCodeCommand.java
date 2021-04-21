/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dhl.command;

import com.brodos.alg.dhl.adapter.GS1RoutingCodeAdapter;
import com.brodos.alg.domain.exception.ALGException;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
public class GenerateGS1RoutingCodeCommand extends HystrixCommand<String> {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(GenerateGS1RoutingCodeCommand.class);
    private final GS1RoutingCodeAdapter gs1RoutingCodeAdapter;

    public GenerateGS1RoutingCodeCommand(String routingcodeAPIUrl, JSONObject routingCodeData) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GS1RoutingCodeGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutEnabled(true)
                        .withExecutionTimeoutInMilliseconds(45000)
                        .withFallbackEnabled(false))
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withMaxQueueSize(100)
                                .withQueueSizeRejectionThreshold(100)
                                .withCoreSize(10)));
        this.gs1RoutingCodeAdapter = new GS1RoutingCodeAdapter(routingcodeAPIUrl, routingCodeData);;
    }

    @Override
    protected String run() throws Exception {
        try {
            return gs1RoutingCodeAdapter.generateRoutingCode();
        } catch (Exception exception) {
            LOG.trace(exception.getMessage(), exception);
            if (!(exception instanceof ALGException)) {
                throw new ALGException(10003, "Unable to generate routing code", exception);
            } else {
                throw (ALGException) exception;
            }
        }
    }
}
