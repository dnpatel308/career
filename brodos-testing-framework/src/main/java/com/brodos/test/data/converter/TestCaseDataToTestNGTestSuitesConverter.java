/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.data.converter;

import com.brodos.test.TestNGRunner;
import static com.brodos.test.Utils.distinctByKey;
import com.brodos.test.data.ParallelMode;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseStatus;
import com.brodos.test.executor.MultipleTestCaseExecutor;
import com.brodos.test.executor.SingleTestCaseExecutor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

/**
 *
 * @author padhaval
 */
public class TestCaseDataToTestNGTestSuitesConverter {

    public void convert() {
        List<TestCaseData> testCaseDatas = TestNGRunner.instance().getTestCaseDatas();
        List<XmlSuite> xmlSuites = new ArrayList<>();

        // ====================================================
        List<String> testSuiteNames = testCaseDatas.stream()
                .filter(td -> td.getParallelMode() == null)
                .filter(distinctByKey(td -> td.getTestSuiteName()))
                .map(TestCaseData::getTestSuiteName)
                .collect(Collectors.toList());

        for (String testSuiteName : testSuiteNames) {
            XmlSuite suite = new XmlSuite();
            suite.setName(testSuiteName);

            List<TestCaseData> caseDatas = testCaseDatas.stream()
                    .filter(td -> td.getTestSuiteName().equals(testSuiteName))
                    .collect(Collectors.toList());

            List<XmlClass> xmlClasses = new ArrayList<>();
            XmlClass xmlClass = new XmlClass();
            xmlClass.setClass(SingleTestCaseExecutor.class);
            xmlClasses.add(xmlClass);

            for (TestCaseData testCaseData : caseDatas) {
                if (!testCaseData.isActive()) {
                    testCaseData.setResult(TestCaseStatus.SKIPPED);
                    continue;
                }

                XmlTest test = new XmlTest(suite);
                test.setName(testCaseData.getTestCaseName());
                test.setSuite(suite);

                test.setXmlClasses(xmlClasses);

                Map<String, String> parametersMap = new HashMap<>();
                parametersMap.put("testSuiteName", testCaseData.getTestSuiteName());
                parametersMap.put("testCaseName", testCaseData.getTestCaseName());

                test.setParameters(parametersMap);
            }

            xmlSuites.add(suite);
        }

        // ====================================================
        List<String> testParallelGroups = testCaseDatas.stream()
                .filter(td -> td.getParallelMode() == ParallelMode.METHOD)
                .filter(distinctByKey(td -> td.getParallelGroup()))
                .map(TestCaseData::getParallelGroup)
                .collect(Collectors.toList());

        for (String parallelGroup : testParallelGroups) {
            XmlSuite suite = new XmlSuite();
            suite.setName(parallelGroup);

            List<TestCaseData> caseDatas = testCaseDatas.stream()
                    .filter(td -> td.getParallelGroup() != null)
                    .filter(td -> td.getParallelGroup().equals(parallelGroup))
                    .collect(Collectors.toList());

            List<XmlClass> xmlClasses = new ArrayList<>();
            XmlClass xmlClass = new XmlClass();
            xmlClass.setClass(SingleTestCaseExecutor.class);
            xmlClasses.add(xmlClass);

            for (TestCaseData testCaseData : caseDatas) {
                if (!testCaseData.isActive()) {
                    testCaseData.setResult(TestCaseStatus.SKIPPED);
                    continue;
                }

                XmlTest test = new XmlTest(suite);
                test.setName(testCaseData.getTestCaseName());
                test.setSuite(suite);

                test.setXmlClasses(xmlClasses);

                Map<String, String> parametersMap = new HashMap<>();
                parametersMap.put("testSuiteName", testCaseData.getTestSuiteName());
                parametersMap.put("testCaseName", testCaseData.getTestCaseName());

                test.setParameters(parametersMap);
            }

            suite.setParallel(XmlSuite.ParallelMode.TESTS);
            suite.setThreadCount(caseDatas.size());

            xmlSuites.add(suite);
        }

        // ====================================================
        testParallelGroups = testCaseDatas.stream()
                .filter(td -> td.getParallelMode() == ParallelMode.CLASS)
                .filter(td -> td.getParallelGroup() != null)
                .filter(distinctByKey(td -> td.getParallelGroup()))
                .map(TestCaseData::getParallelGroup)
                .collect(Collectors.toList());

        for (String parallelGroup : testParallelGroups) {
            XmlSuite suite = new XmlSuite();
            suite.setName(parallelGroup);

            List<TestCaseData> caseDatas = testCaseDatas.stream()
                    .filter(td -> td.getParallelGroup() != null)
                    .filter(td -> td.getParallelGroup().equals(parallelGroup))
                    .filter(distinctByKey(td -> td.getTestSuiteName()))
                    .collect(Collectors.toList());

            List<XmlClass> xmlClasses = new ArrayList<>();
            XmlClass xmlClass = new XmlClass();
            xmlClass.setClass(MultipleTestCaseExecutor.class);
            xmlClasses.add(xmlClass);

            for (TestCaseData testCaseData : caseDatas) {
                if (!testCaseData.isActive()) {
                    testCaseData.setResult(TestCaseStatus.SKIPPED);
                    continue;
                }

                XmlTest test = new XmlTest(suite);
                test.setName(testCaseData.getTestSuiteName());
                test.setSuite(suite);

                test.setXmlClasses(xmlClasses);

                Map<String, String> parametersMap = new HashMap<>();
                parametersMap.put("testSuiteName", testCaseData.getTestSuiteName());

                test.setParameters(parametersMap);
            }

            suite.setParallel(XmlSuite.ParallelMode.TESTS);
            suite.setThreadCount(caseDatas.size());

            xmlSuites.add(suite);
        }

        TestNGRunner.instance().getTestNG().setXmlSuites(xmlSuites);
    }
}
