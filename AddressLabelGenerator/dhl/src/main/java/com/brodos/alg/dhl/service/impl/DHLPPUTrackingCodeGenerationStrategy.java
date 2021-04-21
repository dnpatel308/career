/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dhl.service.impl;

import javax.inject.Named;
import javax.inject.Singleton;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brodos.alg.dhl.command.GenerateTrackingCodeViaPPUCommand;
import com.brodos.alg.dhl.service.DHLTrackingCodeGenerationStrategy;
import com.brodos.commons.config.Configuration;
import javax.inject.Inject;
import org.ops4j.pax.cdi.api.OsgiService;

/**
 *
 * @author padhaval
 */
@Singleton
@Named("dhlPPUTrackingCodeGenerationStrategy")
public class DHLPPUTrackingCodeGenerationStrategy implements DHLTrackingCodeGenerationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DHLPPUTrackingCodeGenerationStrategy.class);
   
    @Inject
    @OsgiService(filter = "(pid=com.brodos.context)")
    Configuration config;
    
    public DHLPPUTrackingCodeGenerationStrategy() {
        
    }

    private String getRequiredBarcode(JSONObject trackingCodeData) {
        double weightInKg = 0.0f;
        switch (trackingCodeData.getString("unit").toLowerCase()) {
            case "gramms":
            case "g": {
                weightInKg = (double) trackingCodeData.getInt("weightInIntegerRepresentation") / 1000f;
                break;
            }
        }

        LOG.debug("weightInKg={}", weightInKg);
        if (weightInKg <= 10) {
            return config.getString("ppu.api.parameter.barcode10kg");
        } else if (weightInKg > 10 && weightInKg <= 20) {
            return config.getString("ppu.api.parameter.barcode20kg");
        } else if (weightInKg > 20 && weightInKg <= 31.5) {
            return config.getString("ppu.api.parameter.barcode31kg");
        } else {
            return null;
        }
    }


    @Override
    public String getTrackingCode(JSONObject trackingCodeData) {
        String barcode = getRequiredBarcode(trackingCodeData);
        LOG.debug("barcode={}", barcode);
        return new GenerateTrackingCodeViaPPUCommand(
                config.getString("ppu"),
                config.getString("ppu.api.login.url"),
                config.getString("ppu.api.url"),
                trackingCodeData.getString("ppuUserName"),
                trackingCodeData.getString("ppuPassword"),
                barcode
        ).execute();
    }
}
