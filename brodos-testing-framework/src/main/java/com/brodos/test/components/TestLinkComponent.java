/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.components;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.TestLinkMethods;
import br.eti.kinoshita.testlinkjavaapi.constants.TestLinkParams;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.ReportTCResultResponse;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestCaseStepResult;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import br.eti.kinoshita.testlinkjavaapi.util.Util;
import static br.eti.kinoshita.testlinkjavaapi.util.Util.getTestCaseStepsMap;
import com.brodos.test.Utils;
import com.brodos.test.markers.SyncTestLink;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
 * @author padhaval
 */
public class TestLinkComponent {

    private static final String DEFAULT_CONFIG_FILE_NAME = "testlink.properties";

    private static TestLinkComponent testLinkComponent = null;

    private final String configFileName;
    private final Properties properties = new Properties();

    private TestLinkAPI testLinkAPI;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private TestLinkComponent() throws Exception {
        configFileName = DEFAULT_CONFIG_FILE_NAME;
        loadProperties(configFileName);
        threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(Integer.valueOf(properties.getProperty("threads.core")));
        threadPoolTaskExecutor.setMaxPoolSize(Integer.valueOf(properties.getProperty("threads.max")));
        threadPoolTaskExecutor.initialize();
    }

    synchronized public static TestLinkComponent instance() throws Exception {
        if (testLinkComponent == null) {
            testLinkComponent = new TestLinkComponent();
        }

        return testLinkComponent;
    }

    private void loadProperties(String configFileName) throws Exception {
        properties.load(new FileReader(configFileName));
    }

    public TestLinkAPI getTestLinkAPI() throws MalformedURLException {
        if (testLinkAPI == null) {
            testLinkAPI = new TestLinkAPI(new URL(properties.getProperty("url")), properties.getProperty("key"));
        }

        return testLinkAPI;
    }

    public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        return threadPoolTaskExecutor;
    }        

    public Future<Map<String, Object>> syncTestCaseExecutionType(SyncTestLink syncTestLink, ExecutionType executionType) {
        return threadPoolTaskExecutor.submit(() -> {
            try {
                TestCase testCase = getTestLinkAPI().getTestCaseByExternalId(syncTestLink.fullTestCaseExternalId(), syncTestLink.version());
                System.out.println("Updating TestLink Automation Status... " + testCase.getName());
                testCase.getSteps().get(syncTestLink.testStepNumber() - 1).setExecutionType(ExecutionType.AUTOMATED);
                if (testCase.getSteps().stream().allMatch((t) -> t.getExecutionType() == ExecutionType.AUTOMATED)) {
                    testCase.setExecutionType(executionType);
                }

                return updateTestCase(testCase);
            } catch (Exception ex) {
                Utils.printException(ex);
                return null;
            }
        });
    }

    public Future<ReportTCResultResponse> syncTestCaseResult(SyncTestLink syncTestLink, List<TestCaseStepResult> testCaseStepResults, ExecutionStatus executionStatus) {
        return threadPoolTaskExecutor.submit(() -> {
            try {
                TestCase testCase = getTestLinkAPI().getTestCaseByExternalId(syncTestLink.fullTestCaseExternalId(), syncTestLink.version());
                System.out.println("Updating TestLink TestCase Result... " + syncTestLink.fullTestCaseExternalId());
                TestProject testProject = findProjectByName(syncTestLink.projectName());
                TestPlan testPlan = getTestLinkAPI().getTestPlanByName(syncTestLink.testPlanName(), testProject.getName());
                Build build = findBuildByProjectIdAndName(testPlan.getId(), syncTestLink.testBuildName());
                Integer externalId = Integer.valueOf(StringUtils.substringAfter(syncTestLink.fullTestCaseExternalId(), "-").trim());
                return getTestLinkAPI().reportTCResult(testCase.getId(), externalId, testPlan.getId(), executionStatus, testCaseStepResults, build.getId(), build.getName(), null, null, null, null, null, null, Boolean.TRUE);
            } catch (Exception ex) {
                Utils.printException(ex);
                return null;
            } finally {
                System.out.println("Updated TestLink TestCase Result... " + syncTestLink.fullTestCaseExternalId());
            }
        });
    }

    private TestProject findProjectByName(String projectName) throws Exception {
        TestProject[] testProjects = getTestLinkAPI().getProjects();
        for (TestProject testProject : testProjects) {
            if (testProject.getName().equals(projectName)) {
                return testProject;
            }
        }

        throw new Exception("Unable to find project by name " + projectName);
    }

    private Build findBuildByProjectIdAndName(Integer id, String testBuildName) throws Exception {
        Build[] builds = getTestLinkAPI().getBuildsForTestPlan(id);
        for (Build build : builds) {
            if (build.getName().equalsIgnoreCase(testBuildName)) {
                return build;
            }
        }

        throw new Exception("Unable to find build by id " + id + " and name " + testBuildName);
    }

    private Map<String, Object> getTestCaseMap(TestCase testCase) throws MalformedURLException {
        Map<String, Object> executionData = new HashMap<>();
        executionData.put(TestLinkParams.TEST_CASE_NAME.toString(), testCase.getName());
        executionData.put(TestLinkParams.TEST_SUITE_ID.toString(), testCase.getTestSuiteId());
        executionData.put(TestLinkParams.TEST_CASE_ID.toString(), testCase.getId());
        executionData.put(TestLinkParams.TEST_PROJECT_ID.toString(), testCase.getTestProjectId());
        executionData.put(TestLinkParams.AUTHOR_LOGIN.toString(), testCase.getAuthorLogin());
        executionData.put(TestLinkParams.SUMMARY.toString(), testCase.getSummary());

        List<Map<String, Object>> steps = getTestCaseStepsMap(testCase.getSteps());
        executionData.put(TestLinkParams.STEPS.toString(), steps);

        executionData.put(TestLinkParams.PRECONDITIONS.toString(), testCase.getPreconditions());
        executionData.put(TestLinkParams.STATUS.toString(), Util.getStringValueOrNull(testCase.getTestCaseStatus()));
        executionData.put(TestLinkParams.IMPORTANCE.toString(),
                Util.getStringValueOrNull(testCase.getTestImportance()));
        executionData.put(TestLinkParams.EXECUTION_TYPE.toString(),
                Util.getStringValueOrNull(testCase.getExecutionType()));
        executionData.put(TestLinkParams.ORDER.toString(), testCase.getOrder());
        executionData.put(TestLinkParams.INTERNAL_ID.toString(), testCase.getInternalId());
        executionData.put(TestLinkParams.CHECK_DUPLICATED_NAME.toString(), testCase.getCheckDuplicatedName());
        executionData.put(TestLinkParams.ACTION_ON_DUPLICATED_NAME.toString(),
                testCase.getActionOnDuplicatedName() != null ? testCase.getActionOnDuplicatedName().toString() : null);
        executionData.put(TestLinkParams.DEV_KEY.toString(), getTestLinkAPI().getDevKey());

        return executionData;
    }

    private Map<String, Object> updateTestCase(TestCase testCase) throws TestLinkAPIException, MalformedURLException {
        try {
            Map<String, Object> responseMap = null;
            Map<String, Object> executionData = getTestCaseMap(testCase);
            List<Object> objects = new ArrayList<>();
            objects.add(executionData);
            Object response = getTestLinkAPI().getXmlRpcClient().execute(TestLinkMethods.UPDATE_TEST_CASE.toString(), objects);
            if (response instanceof Object[]) {
                Object[] arr = (Object[]) response;
                if (arr.length > 0 && arr[0] instanceof Map<?, ?>) {
                    responseMap = (Map<String, Object>) arr[0];
                }
            } else {
                responseMap = (Map<String, Object>) response;
            }
            System.out.println("Updated Test Case Automation Status... " + testCase.getName());
            return responseMap;
        } catch (XmlRpcException xmlrpcex) {
            throw new TestLinkAPIException("Error updating test case: " + xmlrpcex.getMessage(), xmlrpcex);
        }
    }
}
