package com.brodos.device.reservation.test;

import static org.junit.Assert.assertNotNull;

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

//@TestCasesGenerator(configFileName = "GetDeviceReservationBySearchCriteria.properties")
public class GetDeviceReservationBySearchCriteria {

    @GenerateTestCase
    public void testGetReservationBySCSuccess(TestCaseData testCaseData) {
        testCaseData.setUrl(String.format(testCaseData.getUrl(), "page=0&size=10"));
        testCaseData.setStatusCode(200);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                List<String> reservations = response.getBody().jsonPath().getList("_embedded.reservations");
                Assert.assertEquals(reservations.size(), 10);
                JSONObject jObject = new JSONObject(response.getBody().asString());
                JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("reservations");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonOb = jArray.getJSONObject(i);
                    assertNotNull(jsonOb.get("id"));
                    assertNotNull(jsonOb.get("articleNo"));
                    assertNotNull(jsonOb.getJSONObject("_links").get("self"));
//                    assertNotNull(jsonOb.getJSONObject("_embedded").getJSONObject("device").get("id"));
                }
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }

    @GenerateTestCase
    public void testGetReservationBySCReservedSuccess(TestCaseData testCaseData) {
        testCaseData.setUrl(String.format(testCaseData.getUrl(), "page=0&size=10&status=RESERVED"));
        testCaseData.setStatusCode(200);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                JSONObject jObject = new JSONObject(response.getBody().asString());
                JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("reservations");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonOb = jArray.getJSONObject(i);
                    Assert.assertEquals(jsonOb.get("status"), "RESERVED");
                    assertNotNull(jsonOb.get("id"));
                    assertNotNull(jsonOb.get("articleNo"));
                    assertNotNull(jsonOb.getJSONObject("_links").get("self"));
                    assertNotNull(jsonOb.getJSONObject("_links").get("cancel"));
                    assertNotNull(jsonOb.getJSONObject("_links").get("sendout"));
                    assertNotNull(jsonOb.getJSONObject("_embedded").getJSONObject("device").get("id"));
                }
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }

    @GenerateTestCase
    public void testGetReservationBySCSendoutSuccess(TestCaseData testCaseData) {
        testCaseData.setUrl(String.format(testCaseData.getUrl(), "page=0&size=10&status=SENTOUT"));
        testCaseData.setStatusCode(200);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                JSONObject jObject = new JSONObject(response.getBody().asString());
                JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("reservations");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonOb = jArray.getJSONObject(i);
                    Assert.assertEquals(jsonOb.get("status"), "SENTOUT");
                    assertNotNull(jsonOb.get("id"));
                    assertNotNull(jsonOb.get("articleNo"));
                    assertNotNull(jsonOb.getJSONObject("_links").get("self"));
                    assertNotNull(jsonOb.getJSONObject("_embedded").getJSONObject("device").get("id"));
                }
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }

    @GenerateTestCase
    public void testGetReservationBySCCancelledSuccess(TestCaseData testCaseData) {
        testCaseData.setUrl(String.format(testCaseData.getUrl(), "page=0&size=10&status=CANCELLED"));
        testCaseData.setStatusCode(200);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                JSONObject jObject = new JSONObject(response.getBody().asString());
                JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("reservations");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonOb = jArray.getJSONObject(i);
                    Assert.assertEquals(jsonOb.get("status"), "CANCELLED");
                    assertNotNull(jsonOb.get("id"));
                    assertNotNull(jsonOb.get("articleNo"));
                    assertNotNull(jsonOb.getJSONObject("_links").get("self"));
//                    assertNotNull(jsonOb.getJSONObject("_embedded").getJSONObject("device").get("id"));
                }
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }

    @GenerateTestCase
    public void testGetReservationBySCOpencaseSuccess(TestCaseData testCaseData) {
        testCaseData.setUrl(String.format(testCaseData.getUrl(), "page=0&size=10&status=OPEN"));
        testCaseData.setStatusCode(200);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                JSONObject jObject = new JSONObject(response.getBody().asString());
                JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("reservations");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonOb = jArray.getJSONObject(i);
                    Assert.assertEquals(jsonOb.get("status"), "OPEN");
                    assertNotNull(jsonOb.get("id"));
                    assertNotNull(jsonOb.get("articleNo"));
                    assertNotNull(jsonOb.getJSONObject("_embedded").getJSONObject("device").get("id"));
                }
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }

    @GenerateTestCase
    public void testGetReservationBySCArticleSuccess(TestCaseData testCaseData) throws Exception {
        String articleNumber = "SELECT article_number from article LIMIT 1";
        List<JSONObject> rowList = JDBCComponent.instance().executeQuery(articleNumber);
        if (!rowList.isEmpty()) {
            testCaseData.setUrl(
                String.format(testCaseData.getUrl(), "page=0&size=10&articleno=" + rowList.get(0).getString("1")));
        }
        testCaseData.setStatusCode(200);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                JSONObject jObject = new JSONObject(response.getBody().asString());
                JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("reservations");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonOb = jArray.getJSONObject(i);
                    Assert.assertEquals(jsonOb.get("articleNo"), rowList.get(0).getString("1"));
                    assertNotNull(jsonOb.get("id"));
                    assertNotNull(jsonOb.get("articleNo"));
                    assertNotNull(jsonOb.getJSONObject("_embedded").getJSONObject("device").get("id"));
                }
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }

    @GenerateTestCase
    public void testGetReservationBySCAllCriteriaSuccess(TestCaseData testCaseData) throws Exception {
        String articleNumber = "SELECT article_number from article LIMIT 1";
        List<JSONObject> rowList = JDBCComponent.instance().executeQuery(articleNumber);
        if (!rowList.isEmpty()) {
            testCaseData.setUrl(String.format(testCaseData.getUrl(),
                "page=0&size=10&status=RESERVED&articleno=" + rowList.get(0).getString("1")));
        }
        testCaseData.setStatusCode(200);
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                JSONObject jObject = new JSONObject(response.getBody().asString());
                JSONArray jArray = jObject.getJSONObject("_embedded").getJSONArray("reservations");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject jsonOb = jArray.getJSONObject(i);
                    Assert.assertEquals(jsonOb.get("articleNo"), rowList.get(0).getString("1"));
                    Assert.assertEquals(jsonOb.get("status"), "RESERVED");
                    assertNotNull(jsonOb.get("id"));
                    assertNotNull(jsonOb.get("articleNo"));
                    assertNotNull(jsonOb.getJSONObject("_embedded").getJSONObject("device").get("id"));
                }
            }
        };
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }
}
