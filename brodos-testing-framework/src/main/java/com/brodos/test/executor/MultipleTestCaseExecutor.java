/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.executor;

import com.brodos.test.TestNGRunner;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseStatus;
import com.google.common.base.Throwables;
import java.util.List;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 *
 * @author padhaval
 */
public class MultipleTestCaseExecutor extends TestCaseExecutor {

    @Parameters({"testSuiteName"})
    @Test
    public void test(String testSuiteName) throws Exception {
        TestCaseData testCaseData = null;
        try {
            List<TestCaseData> testCaseDatas = TestNGRunner.instance().findByTestSuiteName(testSuiteName);
            for (TestCaseData data : testCaseDatas) {
                if (testCaseData != null && testCaseData.isDependencyOfNextTestCaseData()) {
                    data.setDependentOnTestCaseData(testCaseData);
                }
                
                testCaseData = data;
                execute(testCaseData);
            }
        } catch (AssertionError | Exception error) {
            if (testCaseData != null) {
                testCaseData.setResult(TestCaseStatus.FAILED);
                Throwable cause = error;
                while (cause.getCause() != null) {
                    cause = error.getCause();
                }

                testCaseData.setFailureReason(cause.getMessage());
                testCaseData.setFullFailureReason(Throwables.getStackTraceAsString(error));
            }

            throw error;
        }
    }
}
