package com.brodos.devicecontext.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.json.JSONObject;
import org.testng.Assert;

import com.brodos.test.components.JDBCComponent;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseResponseValidator;
import com.brodos.test.markers.GenerateTestCase;
import com.brodos.test.markers.TestCasesGenerator;

import io.restassured.response.Response;

//@TestCasesGenerator(configFileName = "GetDeviceById.properties")
public class GetDeviceById {
	
    @GenerateTestCase
    public void testGetDeviceByIdSuccess(TestCaseData testCaseData) throws Exception {
        String findDeviceId = "SELECT `id` FROM `device` LIMIT 1";
        List<JSONObject> iDNoResult = JDBCComponent.instance().executeQuery(findDeviceId);
        if (!iDNoResult.isEmpty()) {
            testCaseData.setUrl(String.format(testCaseData.getUrl(), iDNoResult.get(0).getInt("1")));
            testCaseData.setStatusCode(200);
            TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
                @Override
                public void validateResponse(Response response) {
                    Assert.assertTrue(response.getStatusCode() == 200);
                    JSONObject jObject = new JSONObject(response.getBody().asString());
                    if (jObject.getJSONArray("fields").length() == 4) {
                        assertEquals("COMPLETE", jObject.get("status"));
                    } else {
                        assertEquals("INCOMPLETE", jObject.get("status"));
                    }
                    assertNotNull(jObject.get("id"));
                    assertNotNull(jObject.getJSONObject("_links").get("self"));
                }
            };
            testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
        }
    }

    @GenerateTestCase
    public void testGetDeviceByIdFailed(TestCaseData testCaseData) throws Exception {
        String findDeviceId = "SELECT `id` FROM `device` order by id desc LIMIT 1";
        List<JSONObject> iDNoResult = JDBCComponent.instance().executeQuery(findDeviceId);
        if (!iDNoResult.isEmpty()) {
            testCaseData.setUrl(String.format(testCaseData.getUrl(), iDNoResult.get(0).getInt("1") + 1));
            testCaseData.setStatusCode(404);
            TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
                @Override
                public void validateResponse(Response response) {
                    Assert.assertEquals(404, response.getStatusCode());
                    JSONObject jObject = new JSONObject(response.getBody().asString());
                    assertEquals("Device not found.", jObject.get("message"));
                }
            };
            testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
        }
    }

    @GenerateTestCase
    public void testGetDeviceByInvalidIdFailed(TestCaseData testCaseData) throws Exception {
        testCaseData
            .setUrl(String.format(testCaseData.getUrl().substring(0, testCaseData.getUrl().length() - 2), "invalid"));
        testCaseData.setStatusCode(400);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertEquals(400, response.getStatusCode());
                JSONObject jObject = new JSONObject(response.getBody().asString());
                assertEquals("Bad Request", jObject.get("error"));
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }
}
