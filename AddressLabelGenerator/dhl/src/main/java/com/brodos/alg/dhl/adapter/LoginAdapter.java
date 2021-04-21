/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dhl.adapter;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author padhaval
 */
public class LoginAdapter {

    private final String trackingCodeAPILoginUrl;
    private final String userName; 
    private final String password;

    public LoginAdapter(String trackingCodeAPILoginUrl, String userName, String password) {
        this.trackingCodeAPILoginUrl = trackingCodeAPILoginUrl;
        this.userName = userName;
        this.password = password;
    }
                
    // Logn Request 
    public JSONObject doLogin() throws UnirestException {
        HttpRequestWithBody httpRequestWithBody = Unirest.post(trackingCodeAPILoginUrl);
        Map<String, Object> requestParameters = generateLoginRequestPostParameters(userName, password, "");
        for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
            httpRequestWithBody.field(entry.getKey(), entry.getValue().toString());
        }

        HttpResponse<JsonNode> response = httpRequestWithBody.asJson();
        return response.getBody().getObject();
    }

    private Map<String, Object> generateLoginRequestPostParameters(String userName, String password, String token) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("request", new JSONObject());
        JSONObject jsono = new JSONObject();
        JSONArray jsona = new JSONArray();
        jsono.put("name", "USERNAME");
        jsono.put("value", userName);
        jsona.put(jsono);
        jsono = new JSONObject();
        jsono.put("name", "PASSWORD");
        jsono.put("value", password);
        jsona.put(jsono);
        jsono = new JSONObject();
        jsono.put("name", "TOKEN");
        jsono.put("value", token);
        jsona.put(jsono);
        jsono = (JSONObject) parameters.get("request");
        jsono.put("brodosParameters", jsona);
        jsono.put("header", new JSONObject());
        jsono = (JSONObject) jsono.get("header");
        jsono.put("actionName", "LOGIN");
        return parameters;
    }

    @Override
    public String toString() {
        return "LoginAdapter{" + "trackingCodeAPILoginUrl=" + trackingCodeAPILoginUrl + ", userName=" + userName + ", password=" + password + '}';
    }        
}
