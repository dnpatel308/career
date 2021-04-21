/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.data.parser;

import com.brodos.test.TestNGRunner;
import com.brodos.test.Utils;
import com.brodos.test.data.ParallelMode;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseDataGenerator;
import com.brodos.test.markers.GenerateTestCase;
import com.brodos.test.markers.ParallelTestCasesGenerator;
import com.brodos.test.markers.SyncTestLink;
import com.brodos.test.markers.TestCasesGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

/**
 *
 * @author padhaval
 */
public class DynemicTestCaseDataParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<TestCaseData> parse() throws Exception {
        List<TestCaseData> testCaseDatas = new ArrayList<>();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(TestNGRunner.instance().getCallerClass().getProtectionDomain().getCodeSource().getLocation())
                .setScanners(new MethodAnnotationsScanner(), new MethodParameterScanner(), new TypeAnnotationsScanner(), new SubTypesScanner()));
        Set<Method> generateTestCaseMethods = reflections.getMethodsAnnotatedWith(GenerateTestCase.class);
        for (Method method : generateTestCaseMethods) {
            if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(TestCaseData.class)) {
                throw new Exception("Invalid test case generator method : " + method.getDeclaringClass().getName());
            } else {
                final TestCasesGenerator testCasesGenerator = method.getDeclaringClass().getDeclaredAnnotation(TestCasesGenerator.class);

                if (testCasesGenerator == null || method.getAnnotation(GenerateTestCase.class).includeOnlyInParallel()) {
                    System.out.println("Skipping... " + method.getDeclaringClass().getName() + ", " + method.getName());
                    continue;
                }

                final Object object = method.getDeclaringClass().newInstance();
                ParallelTestCasesGenerator parallelTestCasesGenerator = method.getDeclaredAnnotation(ParallelTestCasesGenerator.class);

                int count = 1;
                boolean isParallel = (parallelTestCasesGenerator != null);
                if (isParallel) {
                    count = parallelTestCasesGenerator.numOfThreads();
                }

                SyncTestLink syncTestLink = method.getDeclaredAnnotation(SyncTestLink.class);

                for (int i = 0; i < count; i++) {
                    TestCaseData testCaseData = new TestCaseData();
                    testCaseData.setTestSuiteName(method.getDeclaringClass().getName());
                    testCaseData.setTestCaseName(method.getName() + (isParallel ? i + 1 : ""));
                    testCaseData.setParallelGroup(method.getDeclaringClass().getName() + method.getName());
                    testCaseData.setActive(Boolean.TRUE);
                    testCaseData.setParallel(isParallel);
                    testCaseData.setParallelMode(ParallelMode.METHOD);
                    testCaseData.setTestCaseDataGenerator(createTestCaseDataGenerator(testCaseData, object, method, testCasesGenerator));

                    if (syncTestLink != null && count == 1) {
                        testCaseData.setSyncTestLink(syncTestLink);
                    }

                    if (TestNGRunner.instance().isDebugEnabled()) {
                        System.out.println("Adding test case... " + testCaseData);
                    }

                    testCaseDatas.add(testCaseData);
                }
            }
        }

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(ParallelTestCasesGenerator.class);
        for (Class<?> c : classes) {
            final TestCasesGenerator testCasesGenerator = c.getDeclaredAnnotation(TestCasesGenerator.class);

            if (testCasesGenerator == null) {
                continue;
            }

            final Object object = c.newInstance();
            int count = c.getAnnotation(ParallelTestCasesGenerator.class).numOfThreads();
            for (int i = 0; i < count; i++) {
                Method[] methods = c.getDeclaredMethods();
                int size = 0;
                for (Method method : methods) {
                    GenerateTestCase annotation = method.getAnnotation(GenerateTestCase.class);
                    if (annotation != null && annotation.order() > 0) {
                        size++;
                    }
                }

                if (size > 0) {
                    final TestCaseData[] orderedTestCaseDatas = new TestCaseData[size];
                    int j = 0;
                    for (Method method : methods) {
                        GenerateTestCase annotation = method.getAnnotation(GenerateTestCase.class);
                        if (annotation != null && annotation.order() > 0) {
                            TestCaseData testCaseData = new TestCaseData();
                            testCaseData.setTestSuiteName(method.getDeclaringClass().getName() + (i + 1));
                            testCaseData.setTestCaseName(method.getName() + annotation.order());
                            testCaseData.setParallelGroup(method.getDeclaringClass().getName());
                            testCaseData.setActive(Boolean.TRUE);
                            testCaseData.setParallel(Boolean.TRUE);
                            testCaseData.setParallelMode(ParallelMode.CLASS);
                            testCaseData.setTestCaseDataGenerator(createTestCaseDataGenerator(testCaseData, object, method, testCasesGenerator));

                            if (TestNGRunner.instance().isDebugEnabled()) {
                                System.out.println("Adding parallel test case... " + testCaseData);
                            }

                            orderedTestCaseDatas[annotation.order() - 1] = testCaseData;
                        }
                    }

                    testCaseDatas.addAll(Arrays.asList(orderedTestCaseDatas));
                }
            }
        }

        return testCaseDatas;
    }

    private TestCaseDataGenerator createTestCaseDataGenerator(TestCaseData testCaseData, Object object, Method method, TestCasesGenerator testCasesGenerator) {
        return new TestCaseDataGenerator() {
            @Override
            public void generate() throws Exception {
                try {
                    GenerateTestCase generateTestCase = method.getDeclaredAnnotation(GenerateTestCase.class);
                    if (!generateTestCase.isCustomized()) {
                        testCaseData.setConfigFileName(TestNGRunner.instance().getCommonApiConfigsPath() + "/" + testCasesGenerator.configFileName());
                        if (!StringUtils.isBlank(generateTestCase.configFileName())) {
                            testCaseData.setConfigFileName(TestNGRunner.instance().getCommonApiConfigsPath() + "/" + generateTestCase.configFileName());
                        }

                        initCommonTestData(testCaseData, generateTestCase.headers());
                    }

                    method.invoke(object, testCaseData);
                } catch (Exception ex) {
                    Utils.printException(ex);
                    throw ex;
                }
            }
        };
    }

    private void initCommonTestData(TestCaseData testCaseData, String headersRawValue) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileReader(testCaseData.getConfigFileName()));
        testCaseData.setUrl(Utils.populateEnvValuesAndGet(TestNGRunner.instance().getEnvMap(), properties.getProperty("url")));
        testCaseData.setMethod(Utils.populateEnvValuesAndGet(TestNGRunner.instance().getEnvMap(), properties.getProperty("method")));
        testCaseData.setAccept(Utils.populateEnvValuesAndGet(TestNGRunner.instance().getEnvMap(), properties.getProperty("accept")));
        parseHeaders(testCaseData, Utils.populateEnvValuesAndGet(TestNGRunner.instance().getEnvMap(), properties.getProperty("headers")));
        overwriteHeadersIfNeeded(testCaseData.getHeaders(), headersRawValue);
    }

    private void parseHeaders(TestCaseData testCaseData, String rawData) throws Exception {
        try {
            testCaseData.getHeaders().putAll(TestNGRunner.instance().getCommonHeadersMap());
            if (!StringUtils.isBlank(rawData)) {
                if (rawData.contains("||")) {
                    String[] headersList = StringUtils.splitPreserveAllTokens(rawData, "||");
                    for (String headerData : headersList) {
                        testCaseData.getHeaders().put(StringUtils.substringBefore(headerData, ":"), StringUtils.substringAfter(headerData, ":"));
                    }
                } else {
                    testCaseData.getHeaders().put(StringUtils.substringBefore(rawData, ":"), StringUtils.substringAfter(rawData, ":"));
                }
            }
        } catch (Exception ex) {
            Utils.printException(ex);
            throw new Exception("Invalid headers in: " + testCaseData.getConfigFileName());
        }
    }

    private void overwriteHeadersIfNeeded(Map<String, String> headers, String headersRawValue) {
        try {
            JsonNode jsonNode = objectMapper.readTree(headersRawValue);
            if (jsonNode.isObject()) {
                Iterator<String> headerNames = jsonNode.fieldNames();
                while (headerNames.hasNext()) {
                    String headerName = headerNames.next();
                    String headerValue = jsonNode.get(headerName).asText();
                    headers.put(headerName, headerValue);
                }
            }
        } catch (Exception ex) {
            Utils.printException(ex);
        }
    }
}
