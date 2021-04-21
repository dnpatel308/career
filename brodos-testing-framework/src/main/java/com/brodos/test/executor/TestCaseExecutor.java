/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.executor;

import com.brodos.test.TestNGRunner;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseStatus;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

/**
 *
 * @author padhaval
 */
public abstract class TestCaseExecutor {

    public void execute(TestCaseData testCaseData) throws Exception {
        System.out.println("Executng... " + testCaseData.getTestSuiteName() + ", " + testCaseData.getTestCaseName());
        if (testCaseData.getTestCaseDataGenerator() != null) {
            testCaseData.getTestCaseDataGenerator().generate();
            if (!testCaseData.isActive()) {
                System.out.println("Skipping... " + testCaseData.getTestSuiteName() + ", " + testCaseData.getTestCaseName());
                testCaseData.setResult(TestCaseStatus.SKIPPED);                
                return;
            }
        }

        if (testCaseData.getCustomizedTestMethod() == null) {
            System.out.println(testCaseData.getMethod() + " " + testCaseData.getUrl());
            if (TestNGRunner.instance().isDebugEnabled()) {
                System.out.println("testCaseData: " + testCaseData);
            }

            RequestSpecification requestSpecification = generateRequestSpecification(testCaseData);
            Response response = requestSpecification.when().request(testCaseData.getMethod(), new URL(testCaseData.getUrl()));
            testCaseData.setResponse(response);
            validateResponse(testCaseData, response);
            testCaseData.setResult(TestCaseStatus.PASSED);
        } else {
            testCaseData.getCustomizedTestMethod().test();
        }
    }

    private void validateResponse(TestCaseData testCaseData, Response response) throws Exception {
        Assert.assertEquals(response.statusCode(), testCaseData.getStatusCode());
        Assert.assertTrue(response.getContentType().contains(testCaseData.getAccept()), "Content type doesn't match. Expected: " + testCaseData.getAccept() + ", Actual: " + response.getContentType());

        List<Method> assertionMethodList = testCaseData.getAssertionMethodList();
        List<String[]> assertionArgsTypesList = testCaseData.getAssertionArgsTypesList();
        List<String[]> assertionArgsList = testCaseData.getAssertionArgsList();
        for (int i = 0; i < assertionMethodList.size(); i++) {
            Method assertionMethod = assertionMethodList.get(i);
            Object[] objects = parseAssertionMethodArgs(testCaseData, response, assertionArgsList.get(i), assertionArgsTypesList.get(i));
            assertionMethod.invoke(null, objects);
        }

        if (testCaseData.getTestCaseResponseValidator() != null) {
            testCaseData.getTestCaseResponseValidator().validateResponse(response);
        }
    }

    private Object[] parseAssertionMethodArgs(TestCaseData testCaseData, Response response, String[] values, String[] types) {
        Object[] objects = new Object[types.length];
        for (int i = 0; i < values.length; i++) {
            String argData = values[i];
            if (argData.startsWith(".")) {
                argData = StringUtils.substringAfter(argData, ".");
                objects[i] = extractValue(testCaseData, response, argData).toString();
                objects[i] = parseValueByFieldType(objects[i].toString(), types[i]);
            } else {
                objects[i] = parseValueByFieldType(values[i], types[i]);
            }
        }

        return objects;
    }

    private Object extractValue(TestCaseData testCaseData, Response response, String path) {
        switch (testCaseData.getAccept().toLowerCase()) {
            case "application/json": {
                return response.jsonPath().get(path);
            }

            case "text/xml":
            case "application/xml": {
                return response.xmlPath().get(path);
            }

            default: {
                return null;
            }
        }
    }

    private Object parseValueByFieldType(String value, String typeName) {
        if (typeName.contains(".")) {
            typeName = StringUtils.substringAfterLast(typeName, ".");
        }

        switch (typeName) {
            case "double": {
                return Double.valueOf(value);
            }

            case "Object": {
                return value;
            }

            case "String": {
                return value;
            }

            case "boolean": {
                return Boolean.valueOf(value);
            }

            case "float": {
                return Float.valueOf(value);
            }

            case "long": {
                return Long.valueOf(value);
            }

//            case "Object[]": {
//                break;
//            }
//
//            case "long[]": {
//                break;
//            }
//
//            case "short[]": {
//                break;
//            }
//
//            case "int[]": {
//                break;
//            }
//
//            case "char[]": {
//                break;
//            }
//
//            case "float[]": {
//                break;
//            }
//
//            case "byte[]": {
//                break;
//            }
//
//            case "boolean[]": {
//                break;
//            }
//
//            case "double[]": {
//                break;
//            }            
        }

        return null;
    }

    public RequestSpecification generateRequestSpecification(TestCaseData testCaseData) {
        RequestSpecification requestSpecification = RestAssured.given().relaxedHTTPSValidation();
        requestSpecification = requestSpecification.baseUri(testCaseData.getUrl().trim());
        requestSpecification = requestSpecification.relaxedHTTPSValidation();
        requestSpecification = requestSpecification.headers(testCaseData.getHeaders());
        if (!StringUtils.isEmpty(testCaseData.getBody())) {
            requestSpecification = requestSpecification.body(testCaseData.getBody());
        }

        return requestSpecification;
    }
}
