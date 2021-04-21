/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.data.parser;

import com.brodos.test.TestNGRunner;
import com.brodos.test.Utils;
import com.brodos.test.data.TestCaseData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author padhaval
 */
public class StaticTestCaseDataParser {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<TestCaseData> parseFile(String fileName, Properties commonProperties) throws Exception {
        List<TestCaseData> testCaseDatas = new ArrayList<>();

        if (!new File(fileName).exists()) {
            return testCaseDatas;
        }

        List<String> lines = FileUtils.readLines(new File(fileName), StandardCharsets.UTF_8);
        String[] headers = null;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (commonProperties != null) {
                line = checkAndAppendCommonProperties(commonProperties, line, i == 0);
            }

            line = Utils.populateEnvValuesAndGet(TestNGRunner.instance().getEnvMap(), line);

            if (i == 0) {
                headers = validateHeaders(line);
            } else {
                testCaseDatas.add(parseTestCaseData(line, i, headers));
            }
        }

        return testCaseDatas;
    }

    private String checkAndAppendCommonProperties(Properties commonProperties, String line, boolean isHeader) {
        if (isHeader) {
            return "TestSuite;TestCase;Active;URL;Method;Headers;Body;StatusCode;Accept;AssertionMethod;AssertionArgs;TestLinkSettings";
        } else {
            String urlParameters = line.substring(StringUtils.ordinalIndexOf(line, ";", 3) + 1, StringUtils.ordinalIndexOf(line, ";", 4));
            String[] urlParametersArray = StringUtils.splitPreserveAllTokens(urlParameters, "||");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(line.substring(0, StringUtils.ordinalIndexOf(line, ";", 3))).append(";");
            stringBuilder.append(String.format(commonProperties.getProperty("url").replace("%d", "%s"), (Object[]) urlParametersArray)).append(";");
            stringBuilder.append(commonProperties.getProperty("method")).append(";");
            stringBuilder.append(commonProperties.getProperty("headers"));
            stringBuilder.append(line.substring(StringUtils.ordinalIndexOf(line, ";", 4), StringUtils.ordinalIndexOf(line, ";", 6))).append(";");
            stringBuilder.append(commonProperties.getProperty("accept"));
            stringBuilder.append(line.substring(StringUtils.ordinalIndexOf(line, ";", 6)));
            return stringBuilder.toString();
        }
    }

    private String[] validateHeaders(String line) throws Exception {
        String[] headers = StringUtils.splitPreserveAllTokens(line, ";");
        Field[] fields = TestCaseData.class.getDeclaredFields();

        if (headers.length > 0 && headers.length == fields.length) {
            for (int i = 0; i < headers.length; i++) {
                String header = headers[i].replaceAll("\"", "");
                if (!StringUtils.containsIgnoreCase(fields[i].getName(), header)) {
                    throw new Exception("Invalid headers.");
                }
            }
        } else {
            throw new Exception("Invalid headers.");
        }

        return headers;
    }

    private TestCaseData parseTestCaseData(String line, int rowNum, String[] headers) throws Exception {
        TestCaseData testCaseData = new TestCaseData();
        String[] testCaseDataStrings = StringUtils.splitPreserveAllTokens(line, ";");
        if (testCaseDataStrings.length != headers.length) {
            throw new Exception("Invalid test case data in row: " + rowNum);
        }

        for (int i = 0; i < testCaseDataStrings.length; i++) {
            if (testCaseDataStrings[i].startsWith("\"")
                    && testCaseDataStrings[i].endsWith("\"")) {
                testCaseDataStrings[i] = testCaseDataStrings[i].replace("\"\"", "\"");
                testCaseDataStrings[i] = StringUtils.substringAfter(testCaseDataStrings[i], "\"");
                testCaseDataStrings[i] = StringUtils.substringBeforeLast(testCaseDataStrings[i], "\"");
                testCaseDataStrings[i] = testCaseDataStrings[i].trim();
            }
        }

        testCaseData.setTestSuiteName(testCaseDataStrings[0]);
        testCaseData.setTestCaseName(testCaseDataStrings[1]);
        testCaseData.setActive(Boolean.parseBoolean(testCaseDataStrings[2]));
        testCaseData.setUrl(testCaseDataStrings[3]);
        testCaseData.setMethod(testCaseDataStrings[4]);
        parseHeaders(rowNum, testCaseData.getHeaders(), testCaseDataStrings[5]);
        testCaseData.setBody(testCaseDataStrings[6]);
        if (!StringUtils.isBlank(testCaseDataStrings[7])
                && StringUtils.isNumeric(testCaseDataStrings[7])) {
            testCaseData.setStatusCode(Integer.valueOf(testCaseDataStrings[7]));
        }

        testCaseData.setAccept(testCaseDataStrings[8]);

        if (StringUtils.countMatches(testCaseDataStrings[9], "||")
                != StringUtils.countMatches(testCaseDataStrings[10], "||")) {
            throw new Exception("Invalid assertion settings in row: " + rowNum);
        }

        String testLinkSettingsJsonString = testCaseDataStrings[11];
        if (!StringUtils.isBlank(testLinkSettingsJsonString)) {
            try {
                testCaseData.setTestLinkSettings(objectMapper.readTree(testLinkSettingsJsonString));
            } catch (Exception ex) {
                throw new Exception("Invalid test link settings in row: " + rowNum);
            }
        }

        parseMethods(rowNum, testCaseData.getAssertionMethodList(), testCaseDataStrings[9]);
        parseAssertionMethodsArgs(rowNum, testCaseData.getAssertionArgsTypesList(), testCaseData.getAssertionArgsList(), testCaseDataStrings[9], testCaseDataStrings[10]);

        Set<ConstraintViolation<TestCaseData>> constraintViolations = validator.validate(testCaseData);
        if (!constraintViolations.isEmpty()) {
            throw new Exception("Invalid test case data in row: " + rowNum + ", constraintViolations: " + constraintViolations);
        }

        return testCaseData;
    }

    private void parseHeaders(int rowNum, Map<String, String> headers, String rawData) throws Exception {
        try {
            headers.putAll(TestNGRunner.instance().getCommonHeadersMap());
            if (!StringUtils.isBlank(rawData)) {
                if (rawData.contains("||")) {
                    String[] headersList = StringUtils.splitPreserveAllTokens(rawData, "||");
                    for (String headerData : headersList) {
                        headers.put(StringUtils.substringBefore(headerData, ":"), StringUtils.substringAfter(headerData, ":"));
                    }
                } else {
                    headers.put(StringUtils.substringBefore(rawData, ":"), StringUtils.substringAfter(rawData, ":"));
                }
            }
        } catch (Exception ex) {
            Utils.printException(ex);
            throw new Exception("Invalid headers in row: " + rowNum);
        }
    }

    private void parseMethods(int rowNum, List<Method> list, String rawData) throws Exception {
        if (!StringUtils.isBlank(rawData)) {
            if (rawData.contains("||")) {
                String[] methodNames = StringUtils.split(rawData, "||");
                for (String methodName : methodNames) {
                    list.add(findMethodByName(rowNum, methodName));
                }
            } else {
                list.add(findMethodByName(rowNum, rawData));
            }
        }
    }

    private Method findMethodByName(int rowNum, String methodName) throws Exception {
        Method method = Utils.findMethodByName(methodName);

        if (method == null) {
            throw new Exception("Invalid assertion method '" + methodName + "' in row: " + rowNum);
        }

        return method;
    }

    private void parseAssertionMethodsArgs(int rowNum, List<String[]> assertionArgsTypesList, List<String[]> assertionArgsList, String rawAssertionArgsTypes, String rawAssertionArgs) throws Exception {
        if (!StringUtils.isBlank(rawAssertionArgs) && !StringUtils.isBlank(rawAssertionArgsTypes)) {
            if (rawAssertionArgs.contains("||") && rawAssertionArgsTypes.contains("||")) {
                String[] rawAssertionArgsTypesStrings = StringUtils.split(rawAssertionArgsTypes, "||");
                String[] rawAssertionArgsStrings = StringUtils.split(rawAssertionArgs, "||");
                for (int i = 0; i < rawAssertionArgsStrings.length; i++) {
                    if (!Utils.parseAssertionMethodArgs(assertionArgsTypesList, assertionArgsList, rawAssertionArgsTypesStrings[i], rawAssertionArgsStrings[i])) {
                        throw new Exception("Invalid assertion settings in row: " + rowNum);
                    }
                }
            } else {
                if (!Utils.parseAssertionMethodArgs(assertionArgsTypesList, assertionArgsList, rawAssertionArgsTypes, rawAssertionArgs)) {
                    throw new Exception("Invalid assertion settings in row: " + rowNum);
                }
            }
        }
    }
}
