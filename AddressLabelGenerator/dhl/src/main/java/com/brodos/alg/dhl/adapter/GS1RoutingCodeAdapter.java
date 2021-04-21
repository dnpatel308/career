/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dhl.adapter;

import com.brodos.alg.domain.exception.ALGException;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
public class GS1RoutingCodeAdapter {
    
    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(GS1RoutingCodeAdapter.class);
    private final String routingcodeAPIUrl;
    private final JSONObject routingCodeData;
    
    public GS1RoutingCodeAdapter(String routingcodeAPIUrl, JSONObject routingCodeData) {
        this.routingcodeAPIUrl = routingcodeAPIUrl;
        this.routingCodeData = routingCodeData;
    }
    
    public String generateRoutingCode() throws MalformedURLException, UnirestException, UnsupportedEncodingException, IOException {
        String routingcodeAPIUrlWithParams = generateUrl();        
        LOG.debug("routingcodeAPIUrlWithParams={}", routingcodeAPIUrlWithParams);
        String response = sendGet(routingcodeAPIUrlWithParams);
        LOG.debug("RoutingCodeAPI HttpURLConnection Response={}", response);
        
        JSONObject data = new JSONObject(response);
        
        if (!data.has("data") || !data.getJSONObject("data").has("routingCode")) {
            throw new ALGException(10028, "Address Not valid, RoutingCode API Response: " + data.getJSONObject("data").getString("message"));
        }
        
        return formatRoutingCode(data.getJSONObject("data").getString("routingCode"));
    }
    
    private String generateUrl() throws MalformedURLException, UnsupportedEncodingException {        
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(routingcodeAPIUrl);
        urlBuilder.append("?country=").append(getUTF8String(routingCodeData.getString("country")));
        urlBuilder.append("&postalcode=").append(routingCodeData.getString("postalCode"));
        urlBuilder.append("&street=").append(getUTF8String(routingCodeData.getString("street")));        
        urlBuilder.append("&city=").append(getUTF8String(routingCodeData.getString("city")));
        urlBuilder.append("&productcode=").append(routingCodeData.getString("productcode"));
        if (routingCodeData.has("houseNo")) {
            urlBuilder.append("&houseno=").append(routingCodeData.getString("houseNo"));
        }
        if (routingCodeData.has("deliveryTimeRangeIdentifier")) {
            urlBuilder.append("&deliverytimerangeidentifier=").append(routingCodeData.getString("deliveryTimeRangeIdentifier"));
        }
        return convertToLatinFormat(urlBuilder.toString());
    }
    
    public String getUTF8String(String string) throws UnsupportedEncodingException{
        return URLEncoder.encode(string, "UTF8");
    }
    
    public String convertToLatinFormat(String string) {
        return string.replaceAll("[^\\x09\\x0A\\x0D\\x20-\\x7E\\xA0-\\xFF]", "").replace(" ", "%20");
    }
    
    private String sendGet(String url) throws MalformedURLException, IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        
        return response.toString();
    }
    
    private String formatRoutingCode(String routingCode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        stringBuilder.append(routingCode.substring(0, 3));
        stringBuilder.append(")");
        stringBuilder.append(routingCode.substring(3));
        return stringBuilder.toString();
    }
}
