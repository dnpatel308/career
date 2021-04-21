/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.TestCaseStepResult;
import com.brodos.test.data.TestCaseData;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

/**
 *
 * @author padhaval
 */
public class Utils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final AtomicLong ATOMIC_LONG = new AtomicLong();

    public static synchronized long getUniqueId() {
        return ATOMIC_LONG.incrementAndGet();
    }

    public static synchronized Method findMethodByName(String methodName) throws Exception {
        Method[] methods = Assert.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.toString().contains(methodName)) {
                return method;
            }
        }

        return null;
    }

    public static synchronized boolean parseAssertionMethodArgs(List<String[]> assertionArgsTypesList, List<String[]> assertionArgsList, String rawAssertionArgsTypes, String rawAssertionArgs) throws Exception {
        rawAssertionArgsTypes = StringUtils.substringBetween(rawAssertionArgsTypes, "(", ")");
        if (StringUtils.countMatches(rawAssertionArgsTypes, ",")
                != StringUtils.countMatches(rawAssertionArgs, "@")) {
            return false;
        } else {
            assertionArgsTypesList.add(StringUtils.splitPreserveAllTokens(rawAssertionArgsTypes, ","));
            assertionArgsList.add(StringUtils.splitPreserveAllTokens(rawAssertionArgs, "@"));
            return true;
        }
    }

    public static synchronized String writeValueAsString(Object object, JsonInclude.Include include) throws JsonProcessingException {
        OBJECT_MAPPER.setSerializationInclusion(include);
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    public static synchronized <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static synchronized TestCaseStepResult toTestCaseStepResult(TestCaseData testCaseData) {
        TestCaseStepResult testCaseStepResult = new TestCaseStepResult();
        testCaseStepResult.setNumber(testCaseData.getSyncTestLink().testStepNumber());
        testCaseStepResult.setResult(ExecutionStatus.valueOf(testCaseData.getResult().toString()));
        return testCaseStepResult;
    }

    public static synchronized List<TestCaseStepResult> toTestCaseStepResults(List<TestCaseData> testCaseDatas) {
        List<TestCaseStepResult> testCaseStepResults = new ArrayList<>();
        if (testCaseDatas != null) {
            for (TestCaseData testCaseData : testCaseDatas) {
                testCaseStepResults.add(toTestCaseStepResult(testCaseData));
            }
        }

        return testCaseStepResults;
    }

    public static synchronized void printException(Exception ex) {
        if (TestNGRunner.instance().isDebugEnabled()) {
            ex.printStackTrace();
        } else {
            System.out.println(ex.getMessage());
        }
    }

    public static synchronized Properties findRelavantPropertyFile(String apiConfigDir, File testFile) throws Exception {
        String name = testFile.getName();
        File file = new File(apiConfigDir, name.replace(".csv", ".properties"));
        if (!file.exists()) {
            throw new Exception("File not found " + file.getAbsolutePath());
        }

        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        return properties;
    }

    public static synchronized String populateEnvValuesAndGet(Map<String, String> envMap, String value) {
        for (Map.Entry<String, String> envEntry : envMap.entrySet()) {
            value = value.replace("$" + envEntry.getKey(), envEntry.getValue());
        }

        return value;
    }
}
