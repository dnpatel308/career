/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;

/**
 *
 * @author padhaval
 */
@Component
@PropertySource("classpath:device.context.properties")
public class DeviceContextAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceContextAdapter.class);

    @Autowired
    private Environment deviceContextEnvironment;

    public String getDeviceContextInfoByArticleNoAndSerialNo(String articleNo, String serialNo) throws Exception {
        String deviceContextAPIURLWithParams = buildAndGetDeviceContextInfoUrl(articleNo, serialNo);
        LOG.info("deviceContextAPIURLWithParams={}", deviceContextAPIURLWithParams);
        GetRequest request = Unirest.get(buildAndGetDeviceContextInfoUrl(articleNo, serialNo));
        addAuthHeader(request);

        return request.asString().getBody();
    }

    private String buildAndGetDeviceContextInfoUrl(String articleNo, String serialNo) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(deviceContextEnvironment.getProperty("deviceContextAPIURL"));
        urlBuilder.append("&articlenumber=");
        urlBuilder.append(articleNo);
        urlBuilder.append("&fields.imei1=");
        urlBuilder.append(serialNo);
        return urlBuilder.toString();
    }

    private void addAuthHeader(GetRequest request) {
        request.header(deviceContextEnvironment.getProperty("authHeaderKey"),
            deviceContextEnvironment.getProperty("authHeaderValue"));
    }
}
