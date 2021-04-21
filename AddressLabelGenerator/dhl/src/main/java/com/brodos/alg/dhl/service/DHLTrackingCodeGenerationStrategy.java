package com.brodos.alg.dhl.service;

import org.json.JSONObject;

public interface DHLTrackingCodeGenerationStrategy {
    
    public String getTrackingCode(JSONObject trackingCodeData);
}
