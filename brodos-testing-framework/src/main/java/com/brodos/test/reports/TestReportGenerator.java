/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.test.reports;

import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseStatus;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author padhaval
 */
public class TestReportGenerator {
    
    private final String REPORT_PROPERTIES_PATH = "test-report/report.properties";    

    public void generateReport(List<TestCaseData> testCaseDatas) throws Exception {
        Properties properties = new Properties();
        
        if (new File(REPORT_PROPERTIES_PATH).exists()) {
            properties.load(new FileInputStream(REPORT_PROPERTIES_PATH));
        } else {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(REPORT_PROPERTIES_PATH));
        }

        String reportFilePath = properties.getProperty("report.fileName");
        String reportTemplateFilePath = properties.getProperty("report.template.fileName");
        String[] fields = StringUtils.splitPreserveAllTokens(properties.getProperty("report.fields"), ",");
        String[] headers = StringUtils.splitPreserveAllTokens(properties.getProperty("report.headers"), ",");
        String reportHeader = properties.getProperty("report.header");

        File reportFile = new File(reportFilePath);                
        InputStream reportTemplateFileInputStream = null;
        
        if (new File(reportTemplateFilePath).exists()) {
            reportTemplateFileInputStream = new FileInputStream(reportTemplateFilePath);
        } else {
            reportTemplateFileInputStream = this.getClass().getClassLoader().getResourceAsStream(reportTemplateFilePath);
        }                

        StringBuilder reportBuilder = new StringBuilder();

        reportBuilder.append("<table><tr>");
        for (String header : headers) {
            reportBuilder.append("<th>");
            reportBuilder.append(header);
            reportBuilder.append("</th>");
        }
        reportBuilder.append("</tr>");

        for (TestCaseData testCaseData : testCaseDatas) {
            if (!testCaseData.isActive()) {
                testCaseData.setResult(TestCaseStatus.SKIPPED);
                continue;
            }

            reportBuilder.append("<tr>");
            for (String fieldName : fields) {
                Object value = null;
                try {
                    Field field = testCaseData.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    value = field.get(testCaseData);
                } catch (Exception exception) {
                    // ignore exception tring in super class
                    Field field = testCaseData.getClass().getSuperclass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    value = field.get(testCaseData);
                }

                String style = "";
                if (value != null && fieldName.equalsIgnoreCase("result")) {
                    switch (value.toString()) {
                        case "PASSED": {
                            style = "style='color: green;'";
                            break;
                        }

                        case "FAILED": {
                            style = "style='color: red;'";
                            break;
                        }

                        case "SKIPPED": {
                            style = "style='color: gray;'";
                            break;
                        }
                    }
                }

                if (fieldName.equalsIgnoreCase("fullFailureReason") && value != null) {
                    StringBuilder failureBuilder = new StringBuilder();
                    failureBuilder.append("<div style='max-height: 75px; overflow-y: scroll;'>");
                    failureBuilder.append(value);
                    failureBuilder.append("</div>");
                    value = failureBuilder.toString();
                }

                if (fieldName.equalsIgnoreCase("failureReason") && value != null) {
                    style = "style='width: 220px;'";
                }

                reportBuilder.append("<td ");
                reportBuilder.append(style);
                reportBuilder.append(">");
                reportBuilder.append(value != null ? value : "");
                reportBuilder.append("</td>");
            }

            reportBuilder.append("</tr>");
        }

        reportBuilder.append("</table>");

        String template = IOUtils.toString(reportTemplateFileInputStream, StandardCharsets.UTF_8);
        template = template.replace("${HEADER_1}", reportHeader);
        template = template.replace("${HEADER_2}", new Date().toString());
        template = template.replace("${BODY}", reportBuilder.toString());
        FileUtils.writeStringToFile(reportFile, template, StandardCharsets.UTF_8);
    }
}
