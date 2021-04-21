/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.data;

import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author padhaval
 */
public class TestCaseData extends TestCaseDataAbstract {

    @NotBlank
    private String testSuiteName;
    @NotBlank
    private String testCaseName;
    @NotNull
    private Boolean active;
    @NotBlank
    private String url;
    @NotBlank
    private String method;
    private Map<String, String> headers;
    private String body;
    @NotNull
    private Integer statusCode;
    @NotBlank
    private String accept;
    private List<Method> assertionMethodList;
    private List<String[]> assertionArgsList;
    private JsonNode testLinkSettings;

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }

        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        getHeaders().put("Content-Type", accept);
        this.accept = accept;
    }

    public List<Method> getAssertionMethodList() {
        if (assertionMethodList == null) {
            assertionMethodList = new ArrayList<>();
        }

        return assertionMethodList;
    }

    public void setAssertionMethodList(List<Method> assertionMethodList) {
        this.assertionMethodList = assertionMethodList;
    }

    public List<String[]> getAssertionArgsList() {
        if (assertionArgsList == null) {
            assertionArgsList = new ArrayList<>();
        }

        return assertionArgsList;
    }

    public void setAssertionArgsList(List<String[]> assertionArgsList) {
        this.assertionArgsList = assertionArgsList;
    }

    @Override
    public JsonNode getTestLinkSettings() {
        return testLinkSettings;
    }

    public void setTestLinkSettings(JsonNode testLinkSettings) {
        this.testLinkSettings = testLinkSettings;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
