/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test;

import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.model.ReportTCResultResponse;
import br.eti.kinoshita.testlinkjavaapi.model.TestCaseStepResult;
import com.brodos.test.components.TestLinkComponent;
import com.brodos.test.data.parser.StaticTestCaseDataParser;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseStatus;
import com.brodos.test.reports.TestReportGenerator;
import com.brodos.test.data.converter.TestCaseDataToTestNGTestSuitesConverter;
import com.brodos.test.data.parser.DynemicTestCaseDataParser;
import com.brodos.test.markers.SyncTestLink;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.testng.TestNG;

/**
 *
 * @author padhaval
 */
public class TestNGRunner {

    private static TestNGRunner testNGRunner = null;

    public static synchronized TestNGRunner instance() {
        if (testNGRunner == null) {
            testNGRunner = new TestNGRunner();
        }

        return testNGRunner;
    }

    private final StaticTestCaseDataParser staticTestCaseDataParser = new StaticTestCaseDataParser();
    private final DynemicTestCaseDataParser dynemicTestCaseDataParser = new DynemicTestCaseDataParser();
    private final TestCaseDataToTestNGTestSuitesConverter testCaseDataToTestNGTestSuitesConverter = new TestCaseDataToTestNGTestSuitesConverter();

    private final TestReportGenerator testReportGenerator = new TestReportGenerator();
    private final TestNG testNG = new TestNG();
    private final List<TestCaseData> testCaseDatas = new ArrayList<>();
    private final List<Object> testLinkSyncFutureObjects = new ArrayList<>();
    private final Map<String, String> envMap = new HashMap<>();
    private final Map<String, String> commonHeadersMap = new HashMap<>();

    private final Properties properties = new Properties();
    private Class callerClass;

    public TestNGRunner() {
        loadProperties();
    }

    public Class getCallerClass() {
        return callerClass;
    }

    public TestNGRunner setCallerClass(Class callerClass) {
        this.callerClass = callerClass;
        return testNGRunner;
    }

    private void loadProperties() {
        try {
            properties.load(new FileReader("config.properties"));
        } catch (IOException ex) {
            Utils.printException(ex);
        }
    }

    private void initTestNg() throws Exception {
        getTestNG().setVerbose(this.isDebugEnabled() ? -1 : 0);
        initStaticTestCases();
        initDynemicTestCases();
        validateTestCases();
        testCaseDataToTestNGTestSuitesConverter.convert();
    }

    public TestNG getTestNG() {
        return testNG;
    }

    public List<TestCaseData> getTestCaseDatas() {
        return testCaseDatas;
    }

    public List<Object> getTestLinkSyncFutureObjects() {
        return testLinkSyncFutureObjects;
    }

    public Map<String, String> getEnvMap() {
        return envMap;
    }

    public Map<String, String> getCommonHeadersMap() {
        return commonHeadersMap;
    }

    public TestNGRunner initEnvMapAndCommonHeadersMap(String[] args) throws Exception {
        int size = 0;
        if (args != null) {
            size += args.length;
        }

        String usingEnvVariables = properties.getProperty("using.env.variables");
        if (!StringUtils.isBlank(usingEnvVariables)) {
            size += StringUtils.countMatches(usingEnvVariables, ",") + 1;
        }

        if (size > 0) {
            String[] variables = new String[size];
            int i = 0;
            if (!StringUtils.isBlank(usingEnvVariables)) {
                String[] usingEnvVariablesStrings = StringUtils.splitPreserveAllTokens(usingEnvVariables, ",");
                for (String usingEnvVariable : usingEnvVariablesStrings) {
                    String envValue = System.getenv(usingEnvVariable);
                    if (envValue.trim().startsWith("CommonHeader:")) {
                        variables[i] = envValue;
                    } else {
                        variables[i] = usingEnvVariable + "=" + envValue;
                    }

                    i++;
                }
            }

            if (args != null) {
                for (String arg : args) {
                    variables[i] = arg;
                    i++;
                }
            }

            for (String variable : variables) {
                if (!variable.contains("=")) {
                    throw new Exception("Invalid argumnet: " + variable + ", format should be key=val");
                }

                String[] argKeyVal = variable.split("=");
                if (argKeyVal[0].trim().startsWith("CommonHeader:")) {
                    argKeyVal[0] = StringUtils.substringAfter(argKeyVal[0], "CommonHeader:").trim();
                    getCommonHeadersMap().put(argKeyVal[0], argKeyVal[1]);
                } else {
                    getEnvMap().put(argKeyVal[0], argKeyVal[1]);
                }
            }
        }

        System.out.println("envMap = " + getEnvMap());
        System.out.println("commonHeadersMap = " + getCommonHeadersMap());
        return this;
    }

    public boolean isDebugEnabled() {
        return properties.getProperty("debug").equalsIgnoreCase("true");
    }

    public List<TestCaseData> findByTestSuiteName(String testSuiteName) {
        return testCaseDatas.stream()
                .filter(td -> td.getTestSuiteName().equals(testSuiteName))
                .collect(Collectors.toList());
    }

    public TestCaseData findByTestSuiteNameAndTestCaseName(String testSuiteName, String testCaseName) {
        return testCaseDatas.stream()
                .filter(td -> td.getTestSuiteName().equals(testSuiteName) && td.getTestCaseName().equals(testCaseName))
                .findFirst().get();

    }

    public void run() throws Exception {
        System.out.println("Execution started. " + new Date());
        if (callerClass == null) {
            throw new Exception("Please call setCallerClass() first.");
        } else {
            System.out.println("Scanning " + this.callerClass.getProtectionDomain().getCodeSource().getLocation());
        }

        initTestNg();
        printAllTest();
        testNGRunner.getTestNG().run();
        testNGRunner.testReportGenerator.generateReport(testCaseDatas);

        if (Boolean.valueOf(properties.getProperty("testlink.sync.automation.status"))) {
            testLinkSyncFutureObjects.addAll(checkAndSyncAutomationStatusWithTestLink());
        }

        if (Boolean.valueOf(properties.getProperty("testlink.sync.result"))) {
            testLinkSyncFutureObjects.addAll(checkAndSyncTestResultWithTestLink());
        }

        if (!testLinkSyncFutureObjects.isEmpty()) {
            TestLinkComponent.instance().getThreadPoolTaskExecutor().shutdown();
            long testlinkSyncTimeout = Long.valueOf(properties.getProperty("testlink.sync.timeout"));
            if (testlinkSyncTimeout > 0) {
                TimeLimitedCodeBlock.runWithTimeout(() -> {
                    try {
                        waitForTestLinkSync();
                    } catch (Exception ex) {
                        Utils.printException(ex);
                    }
                }, testlinkSyncTimeout, TimeUnit.MILLISECONDS);
            } else {
                waitForTestLinkSync();
            }
        }

        System.out.println("Execution ended. " + new Date());
    }

    private void waitForTestLinkSync() throws Exception {
        for (Object object : testLinkSyncFutureObjects) {
            ((Future) object).get();
        }
    }

    public String getCommonApiConfigsPath() {
        return properties.getProperty("test.common.api.configs.folder");
    }

    private void initStaticTestCases() throws Exception {
        if (Boolean.valueOf(properties.getProperty("test.static"))) {
            testCaseDatas.addAll(staticTestCaseDataParser.parseFile(properties.getProperty("test.static.data.file"), null));
            File file = new File(properties.getProperty("test.static.data.files.folder"));
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        if (name.toLowerCase().endsWith(".csv")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                for (File testFile : files) {
                    testCaseDatas.addAll(staticTestCaseDataParser.parseFile(testFile.getAbsolutePath(), Utils.findRelavantPropertyFile(getCommonApiConfigsPath(), testFile)));
                }
            }
        }
    }

    private void initDynemicTestCases() throws Exception {
        if (Boolean.valueOf(properties.getProperty("test.dynemic"))) {
            testCaseDatas.addAll(dynemicTestCaseDataParser.parse());
        }
    }

    private void validateTestCases() throws Exception {
        if (testCaseDatas.isEmpty()) {
            throw new Exception("No test suite found. Nothing to run.");
        }
    }

    private void printAllTest() {
        if (isDebugEnabled()) {
            for (TestCaseData testCaseData : testCaseDatas) {
                System.out.println(testCaseData);
            }
        }
    }

    public List<Future<Map<String, Object>>> checkAndSyncAutomationStatusWithTestLink() {
        List<Future<Map<String, Object>>> futures = new ArrayList<>();
        for (TestCaseData testCaseData : testCaseDatas) {
            if (testCaseData != null && testCaseData.getSyncTestLink() != null) {
                try {
                    SyncTestLink syncTestLink = testCaseData.getSyncTestLink();
                    if (StringUtils.isBlank(syncTestLink.fullTestCaseExternalId())) {
                        System.out.println("Skipping test link automation sync sync... " + testCaseData.getTestSuiteName() + ", " + testCaseData.getTestCaseName());
                        continue;
                    }

                    futures.add(TestLinkComponent.instance().syncTestCaseExecutionType(syncTestLink, ExecutionType.AUTOMATED));
                } catch (Exception ex) {
                    Utils.printException(ex);
                }
            }
        }

        return futures;
    }

    public List<Future<ReportTCResultResponse>> checkAndSyncTestResultWithTestLink() {
        List<Future<ReportTCResultResponse>> futures = new ArrayList<>();
        List<String> disitnctFullTestCaseExternalId = testCaseDatas.stream().filter((t) -> t.getSyncTestLink() != null).filter(Utils.distinctByKey((t) -> t.getSyncTestLink().fullTestCaseExternalId()))
                .map((t) -> t.getSyncTestLink().fullTestCaseExternalId())
                .collect(Collectors.toList());
        for (String fullTestCaseExternalId : disitnctFullTestCaseExternalId) {
            try {
                List<TestCaseData> caseDatas = testCaseDatas.stream()
                        .filter(td -> td.getSyncTestLink() != null
                        && td.getSyncTestLink().fullTestCaseExternalId().equals(fullTestCaseExternalId)
                        && td.getResult() != null
                        && (td.getResult() == TestCaseStatus.PASSED || td.getResult() == TestCaseStatus.FAILED)
                        )
                        .collect(Collectors.toList());

                if (caseDatas == null || caseDatas.isEmpty()) {
                    continue;
                }

                TestCaseData firstTestCaseData = caseDatas.stream().findFirst().get();
                if (StringUtils.isBlank(firstTestCaseData.getSyncTestLink().fullTestCaseExternalId())
                        || StringUtils.isBlank(firstTestCaseData.getSyncTestLink().projectName())
                        || StringUtils.isBlank(firstTestCaseData.getSyncTestLink().testPlanName())
                        || StringUtils.isBlank(firstTestCaseData.getSyncTestLink().testBuildName())) {
                    System.out.println("Skipping test link automation sync sync... " + firstTestCaseData.getTestSuiteName() + ", " + firstTestCaseData.getTestCaseName());
                    continue;
                }

                ExecutionStatus executionStatus = ExecutionStatus.PASSED;
                if (caseDatas.stream().anyMatch((t) -> t.getResult() == TestCaseStatus.FAILED)) {
                    executionStatus = ExecutionStatus.FAILED;
                }

                List<TestCaseStepResult> testCaseStepResults = Utils.toTestCaseStepResults(caseDatas);
                futures.add(TestLinkComponent.instance().syncTestCaseResult(caseDatas.stream().findFirst().get().getSyncTestLink(), testCaseStepResults, executionStatus));
            } catch (Exception ex) {
                Utils.printException(ex);
            }
        }

        return futures;
    }
}
