/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dhl.service.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.json.JSONObject;

import com.brodos.alg.dhl.service.DHLTrackingCodeGenerationStrategy;
import com.brodos.alg.dhl.service.DhlTrackingCodeService;

/**
 *
 * @author padhaval
 */
@Singleton
@Named("dhlTrackingCodeService")
public class DhlTrackingCodeServiceImpl implements DhlTrackingCodeService {

    @Inject
    DHLNumberRangeTrackingCodeGenerationStrategy dhlNumberRangeTrackingCodeGenerationStrategy;
    
    @Inject
    DHLPPUTrackingCodeGenerationStrategy dhlPPUTrackingCodeGenerationStrategy;
    
    DHLTrackingCodeGenerationStrategy dhlTrackingCodeGenerationStrategy;
                
    @Override
    public String getTrackingCode(JSONObject trackingCodeData) {
        initializeStrategy(trackingCodeData.getString("trackingCodeType"));
        return dhlTrackingCodeGenerationStrategy.getTrackingCode(trackingCodeData);
    }
    
    private void setTrackingCodeGenerationStrategy(DHLTrackingCodeGenerationStrategy dhlTrackingCodeGenerationStrategy) {
        this.dhlTrackingCodeGenerationStrategy = dhlTrackingCodeGenerationStrategy;
    }

    public void initializeStrategy(String trackingCodeType) {
        switch (trackingCodeType) {            
            case "PPU":
                this.setTrackingCodeGenerationStrategy(dhlPPUTrackingCodeGenerationStrategy);
                break;
            case "NUMBER_RANGE":
                this.setTrackingCodeGenerationStrategy(dhlNumberRangeTrackingCodeGenerationStrategy);
                break;
            default:
                break;
        }
    }

}
