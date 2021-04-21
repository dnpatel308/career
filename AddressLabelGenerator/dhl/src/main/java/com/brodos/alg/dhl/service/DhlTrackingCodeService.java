/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dhl.service;

import org.json.JSONObject;


/**
 *
 * @author padhaval
 */
public interface DhlTrackingCodeService {
          
    public String getTrackingCode(JSONObject trackingCodeData);        
}
