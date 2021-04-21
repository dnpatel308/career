/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dhl;

import com.brodos.alg.dhl.service.DhlRoutingCodeService;
import com.brodos.alg.dhl.service.DhlTrackingCodeService;
import com.brodos.alg.domain.LabelGenerator;
import java.util.List;

/**
 *
 * @author padhaval
 */
public abstract class DHLLabelGenerator extends LabelGenerator {
     
    private final DhlTrackingCodeService trackingCodeService;    
    private final DhlRoutingCodeService routingCodeService;

    public DHLLabelGenerator(DhlTrackingCodeService trackingCodeService, DhlRoutingCodeService routingCodeService, List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        super(stringKeys, integerKeys, excludeFieldsFromValidation);
        this.trackingCodeService = trackingCodeService;
        this.routingCodeService = routingCodeService;
    }       

    public DhlTrackingCodeService getTrackingCodeService() {
        return trackingCodeService;
    }

    public DhlRoutingCodeService getRoutingCodeService() {
        return routingCodeService;
    }        
}
