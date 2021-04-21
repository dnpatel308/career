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
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
public class SimpleRoutingCodeAdapter {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(SimpleRoutingCodeAdapter.class);
    private final String routingcodeAPIUrl;
    private final JSONObject routingCodeData;

    public SimpleRoutingCodeAdapter(String routingcodeAPIUrl, JSONObject routingCodeData) {
        this.routingcodeAPIUrl = routingcodeAPIUrl;
        this.routingCodeData = routingCodeData;
    }

    public String generateRoutingCode() throws MalformedURLException, UnirestException, UnsupportedEncodingException, IOException {
        String routingcodeAPIUrlWithParams = generateUrl();
        LOG.debug("routingcodeAPIUrlWithParams={}", routingcodeAPIUrlWithParams);
        String response = sendGet(routingcodeAPIUrlWithParams);
        LOG.debug("RoutingCodeAPI HttpURLConnection Response={}", response);

        JSONObject data = new JSONObject(response);

        if (!data.has("routingCode")) {
            throw new ALGException(10028, "Address Not valid, RoutingCode API Response: " + data);
        }

        return data.get("routingCode").toString();
    }

    private String generateUrl() throws MalformedURLException, UnsupportedEncodingException {        
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(routingcodeAPIUrl);
        if (routingCodeData.has("name1")) {
            urlBuilder.append(routingCodeData.getString("name1"));
        }
        if (routingCodeData.has("name2")) {
            urlBuilder.append(routingCodeData.getString("name2"));
        }
        if (routingCodeData.has("name3")) {
            urlBuilder.append(routingCodeData.getString("name3"));
        }
        urlBuilder.append("/");
        urlBuilder.append(routingCodeData.getString("street"));
        urlBuilder.append("/");
        urlBuilder.append(routingCodeData.getString("houseNo"));
        urlBuilder.append("/");
        urlBuilder.append(routingCodeData.getString("postalCode"));
        urlBuilder.append("/");
        urlBuilder.append(routingCodeData.getString("city"));
        urlBuilder.append("/");
        urlBuilder.append(33);        
        return convertToLatinFormat(urlBuilder.toString());
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
}
