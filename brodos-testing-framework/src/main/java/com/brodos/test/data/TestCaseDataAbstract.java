/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.data;

import com.brodos.test.markers.SyncTestLink;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public abstract class TestCaseDataAbstract {

    private String configFileName;
    private List<String[]> assertionArgsTypesList;
    private TestCaseStatus result;
    private String failureReason;
    private String fullFailureReason;
    private Response response;
    private TestCaseResponseValidator testCaseResponseValidator;
    private TestCaseDataGenerator testCaseDataGenerator;
    private boolean parallel;
    private ParallelMode parallelMode;
    private boolean dependencyOfNextTestCaseData;
    private TestCaseData dependentOnTestCaseData;
    private String parallelGroup;
    private CustomizedTestMethod customizedTestMethod;
    private final Map<String, Object> metadata = new HashMap<>();

    // for test link support
    private SyncTestLink syncTestLink;

    public String getConfigFileName() {
        return configFileName;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public List<String[]> getAssertionArgsTypesList() {
        if (assertionArgsTypesList == null) {
            assertionArgsTypesList = new ArrayList<>();
        }

        return assertionArgsTypesList;
    }

    public void setAssertionArgsTypesList(List<String[]> assertionArgsTypesList) {
        this.assertionArgsTypesList = assertionArgsTypesList;
    }

    public TestCaseStatus getResult() {
        return result;
    }

    public void setResult(TestCaseStatus result) {
        this.result = result;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getFullFailureReason() {
        return fullFailureReason;
    }

    public void setFullFailureReason(String fullFailureReason) {
        this.fullFailureReason = fullFailureReason;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public TestCaseResponseValidator getTestCaseResponseValidator() {
        return testCaseResponseValidator;
    }

    public void setTestCaseResponseValidator(TestCaseResponseValidator testCaseResponseValidator) {
        this.testCaseResponseValidator = testCaseResponseValidator;
    }

    public TestCaseDataGenerator getTestCaseDataGenerator() {
        return testCaseDataGenerator;
    }

    public void setTestCaseDataGenerator(TestCaseDataGenerator testCaseDataGenerator) {
        this.testCaseDataGenerator = testCaseDataGenerator;
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    public ParallelMode getParallelMode() {
        return parallelMode;
    }

    public void setParallelMode(ParallelMode parallelMode) {
        this.parallelMode = parallelMode;
    }

    public boolean isDependencyOfNextTestCaseData() {
        return dependencyOfNextTestCaseData;
    }

    public void setDependencyOfNextTestCaseData(boolean dependencyOfNextTestCaseData) {
        this.dependencyOfNextTestCaseData = dependencyOfNextTestCaseData;
    }

    public TestCaseData getDependentOnTestCaseData() {
        return dependentOnTestCaseData;
    }

    public void setDependentOnTestCaseData(TestCaseData dependentOnTestCaseData) {
        this.dependentOnTestCaseData = dependentOnTestCaseData;
    }

    public String getParallelGroup() {
        return parallelGroup;
    }

    public void setParallelGroup(String parallelGroup) {
        this.parallelGroup = parallelGroup;
    }

    public SyncTestLink getSyncTestLink() {
        if (syncTestLink == null && getTestLinkSettings() != null) {
            JsonNode jsonNode = getTestLinkSettings();
            syncTestLink = new SyncTestLink() {
                @Override
                public String fullTestCaseExternalId() {
                    return jsonNode.has("fullTestCaseExternalId") ? jsonNode.get("fullTestCaseExternalId").asText() : null;
                }

                @Override
                public int testStepNumber() {
                    return jsonNode.has("testStepNumber") ? jsonNode.get("testStepNumber").asInt() : null;
                }

                @Override
                public int version() {
                    return jsonNode.has("version") ? jsonNode.get("version").asInt() : null;
                }

                @Override
                public String projectName() {
                    return jsonNode.has("projectName") ? jsonNode.get("projectName").asText() : null;
                }

                @Override
                public String testPlanName() {
                    return jsonNode.has("testPlanName") ? jsonNode.get("testPlanName").asText() : null;
                }

                @Override
                public String testBuildName() {
                    return jsonNode.has("testBuildName") ? jsonNode.get("testBuildName").asText() : null;
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return SyncTestLink.class;
                }
            };
        }

        return syncTestLink;
    }

    public void setSyncTestLink(SyncTestLink syncTestLink) {
        this.syncTestLink = syncTestLink;
    }

    public CustomizedTestMethod getCustomizedTestMethod() {
        return customizedTestMethod;
    }

    public void setCustomizedTestMethod(CustomizedTestMethod customizedTestMethod) {
        this.customizedTestMethod = customizedTestMethod;
    }

    public abstract JsonNode getTestLinkSettings();

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
