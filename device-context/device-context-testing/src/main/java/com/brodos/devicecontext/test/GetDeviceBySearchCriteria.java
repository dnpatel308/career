package com.brodos.devicecontext.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import com.brodos.test.components.JDBCComponent;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseResponseValidator;
import com.brodos.test.markers.GenerateTestCase;
import com.brodos.test.markers.TestCasesGenerator;

import io.restassured.response.Response;

@TestCasesGenerator(configFileName = "GetDeviceBySearchCriteria.properties")
public class GetDeviceBySearchCriteria {

    @GenerateTestCase
    public void testGetDeviceBySCSuccess(TestCaseData testCaseData) {
        testCaseData.setUrl(String.format(testCaseData.getUrl(), "_page=0&_size=10"));
        testCaseData.setStatusCode(200);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }

    @GenerateTestCase
    public void testGetDeviceBySCArticleNoSuccess(TestCaseData testCaseData) throws Exception {
        String findArticleNumber = "SELECT `articlenumber` FROM `device` LIMIT 1";
        List<JSONObject> articleNoResult = JDBCComponent.instance().executeQuery(findArticleNumber);
        if (!articleNoResult.isEmpty()) {
            testCaseData.setUrl(String.format(testCaseData.getUrl(),
                "_page=0&_size=10&articlenumber=" + articleNoResult.get(0).getString("1")));
            testCaseData.setStatusCode(200);
            TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
                @Override
                public void validateResponse(Response response) {
                    Assert.assertTrue(response.getStatusCode() == 200);
                    JSONObject jObject = new JSONObject(response.getBody().asString());
                    JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("devices");
                    if (jArray.length() > 0) {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonOb = jArray.getJSONObject(i);
                            if (jsonOb.getJSONObject("device").getJSONArray("fields").length() == 4) {
                                assertEquals("COMPLETE", jsonOb.getJSONObject("device").get("status"));
                            } else {
                                assertEquals("INCOMPLETE", jsonOb.getJSONObject("device").get("status"));
                            }
                            assertNotNull(jsonOb.getJSONObject("device").get("id"));
                            assertNotNull(jsonOb.getJSONObject("_links").get("self"));
                        }
                    } else {
                        assertTrue(jArray.isEmpty());
                    }
                }
            };
            testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
        }
    }

    @GenerateTestCase
    public void testGetDeviceByAllParamSuccess(TestCaseData testCaseData) throws Exception {
        String findArticleNumber =
            "SELECT `articlenumber`, `name`, `value` FROM `device_field` df join device d on d.id = df.device_id where id = (select id from device limit 1)";
        List<JSONObject> articleNoResult = JDBCComponent.instance().executeQuery(findArticleNumber);
        if (!articleNoResult.isEmpty()) {
            testCaseData.setUrl(String.format(testCaseData.getUrl(),
                "_page=0&_size=10&articlenumber=" + articleNoResult.get(0).getString("1") + "&fields.imei1="
                    + articleNoResult.get(0).getString("3") + "&fields.imei2=" + articleNoResult.get(1).getString("3")
                    + "&fields.serial=" + articleNoResult.get(2).getString("3")));
            testCaseData.setStatusCode(200);
            TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
                @Override
                public void validateResponse(Response response) {
                    Assert.assertTrue(response.getStatusCode() == 200);
                    JSONObject jObject = new JSONObject(response.getBody().asString());
                    JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("devices");
                    if (jArray.length() > 0) {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonOb = jArray.getJSONObject(i);
                            if (jsonOb.getJSONObject("device").getJSONArray("fields").length() == 3) {
                                assertEquals("COMPLETE", jsonOb.getJSONObject("device").get("status"));
                            } else {
                                assertEquals("INCOMPLETE", jsonOb.getJSONObject("device").get("status"));
                            }
                            assertNotNull(jsonOb.getJSONObject("device").get("id"));
                            assertNotNull(jsonOb.getJSONObject("_links").get("self"));
                        }
                    } else {
                        assertTrue(jArray.isEmpty());
                    }
                }
            };
            testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
        }
    }

    @GenerateTestCase
    public void testGetDeviceByIMEI1Success(TestCaseData testCaseData) throws Exception {
        String findIMEI1 = "SELECT `value` FROM `device_field` where name = 'imei1' limit 1";
        List<JSONObject> articleNoResult = JDBCComponent.instance().executeQuery(findIMEI1);
        if (!articleNoResult.isEmpty()) {
            testCaseData.setUrl(String.format(testCaseData.getUrl(),
                "_page=0&_size=10&fields.imei1=" + articleNoResult.get(0).getString("1")));
            testCaseData.setStatusCode(200);
            TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
                @Override
                public void validateResponse(Response response) {
                    Assert.assertTrue(response.getStatusCode() == 200);
                    JSONObject jObject = new JSONObject(response.getBody().asString());
                    JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("devices");
                    if (jArray.length() > 0) {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonOb = jArray.getJSONObject(i);
                            if (jsonOb.getJSONObject("device").getJSONArray("fields").length() == 3 || jsonOb.getJSONObject("device").getJSONArray("fields").length() == 4) {
                                assertEquals("COMPLETE", jsonOb.getJSONObject("device").get("status"));
                            } else {
                                assertEquals("INCOMPLETE", jsonOb.getJSONObject("device").get("status"));
                            }
                            assertNotNull(jsonOb.getJSONObject("device").get("id"));
                            assertNotNull(jsonOb.getJSONObject("_links").get("self"));
                        }
                    } else {
                        assertTrue(jArray.isEmpty());
                    }
                }
            };
            testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
        }
    }

    @GenerateTestCase
    public void testGetDeviceByIMEI2Success(TestCaseData testCaseData) throws Exception {
        String findIMEI1 = "SELECT `value` FROM `device_field` where name = 'imei2' limit 1";
        List<JSONObject> articleNoResult = JDBCComponent.instance().executeQuery(findIMEI1);
        if (!articleNoResult.isEmpty()) {
            testCaseData.setUrl(String.format(testCaseData.getUrl(),
                "_page=0&_size=10&fields.imei2=" + articleNoResult.get(0).getString("1")));
            testCaseData.setStatusCode(200);
            TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
                @Override
                public void validateResponse(Response response) {
                    Assert.assertTrue(response.getStatusCode() == 200);
                    JSONObject jObject = new JSONObject(response.getBody().asString());
                    JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("devices");
                    if (jArray.length() > 0) {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonOb = jArray.getJSONObject(i);
                            if (jsonOb.getJSONObject("device").getJSONArray("fields").length() == 4) {
                                assertEquals("COMPLETE", jsonOb.getJSONObject("device").get("status"));
                            } else {
                                assertEquals("INCOMPLETE", jsonOb.getJSONObject("device").get("status"));
                            }
                            assertNotNull(jsonOb.getJSONObject("device").get("id"));
                            assertNotNull(jsonOb.getJSONObject("_links").get("self"));
                        }
                    } else {
                        assertTrue(jArray.isEmpty());
                    }
                }
            };
            testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
        }
    }

    @GenerateTestCase
    public void testGetDeviceBySerialSuccess(TestCaseData testCaseData) throws Exception {
        String findSerial = "SELECT `value` FROM `device_field` where `name` = 'serial' limit 1";
        List<JSONObject> articleNoResult = JDBCComponent.instance().executeQuery(findSerial);
        if (!articleNoResult.isEmpty()) {
            testCaseData.setUrl(String.format(testCaseData.getUrl(),
                "_page=0&_size=10&fields.serial=" + articleNoResult.get(0).getString("1")));
            testCaseData.setStatusCode(200);
            TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
                @Override
                public void validateResponse(Response response) {
                    Assert.assertTrue(response.getStatusCode() == 200);
                    JSONObject jObject = new JSONObject(response.getBody().asString());
                    JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("devices");
                    if (jArray.length() > 0) {
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonOb = jArray.getJSONObject(i);
                            if (jsonOb.getJSONObject("device").getJSONArray("fields").length() == 3) {
                                assertEquals("COMPLETE", jsonOb.getJSONObject("device").get("status"));
                            } else {
                                assertEquals("INCOMPLETE", jsonOb.getJSONObject("device").get("status"));
                            }
                            assertNotNull(jsonOb.getJSONObject("device").get("id"));
                            assertNotNull(jsonOb.getJSONObject("_links").get("self"));
                        }
                    } else {
                        assertTrue(jArray.isEmpty());
                    }
                }
            };
            testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
        }
    }

    @GenerateTestCase
    public void testGetDeviceByArticleFail(TestCaseData testCaseData) {
        testCaseData.setUrl(String.format(testCaseData.getUrl(), "_page=0&_size=10&articlenumber=invalidarticle"));
        testCaseData.setStatusCode(200);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                JSONObject jObject = new JSONObject(response.getBody().asString());
                JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("devices");
                assertTrue(jArray.isEmpty());
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }

    @GenerateTestCase
    public void testGetDeviceByIMEI1Fail(TestCaseData testCaseData) {
        testCaseData.setUrl(String.format(testCaseData.getUrl(), "_page=0&_size=10&fields.imei1=invalid"));
        testCaseData.setStatusCode(200);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                JSONObject jObject = new JSONObject(response.getBody().asString());
                JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("devices");
                assertTrue(jArray.isEmpty());
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }

    @GenerateTestCase
    public void testGetDeviceByIMEI2Fail(TestCaseData testCaseData) {
        testCaseData.setUrl(String.format(testCaseData.getUrl(), "_page=0&_size=10&fields.imei2=invalid"));
        testCaseData.setStatusCode(200);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                JSONObject jObject = new JSONObject(response.getBody().asString());
                JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("devices");
                assertTrue(jArray.isEmpty());
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }

    @GenerateTestCase
    public void testGetDeviceBySerialFail(TestCaseData testCaseData) {
        testCaseData.setUrl(String.format(testCaseData.getUrl(), "_page=0&_size=10&fields.serial=invalid"));
        testCaseData.setStatusCode(200);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                JSONObject jObject = new JSONObject(response.getBody().asString());
                JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("devices");
                assertTrue(jArray.isEmpty());
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }
}
