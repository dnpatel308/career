/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.device.reservation.test;

import com.brodos.test.Utils;
import com.brodos.test.components.JDBCComponent;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseResponseValidator;
import com.brodos.test.markers.GenerateTestCase;
import com.brodos.test.markers.ParallelTestCasesGenerator;
import com.brodos.test.markers.SyncTestLink;
import com.brodos.test.markers.TestCasesGenerator;
import io.restassured.response.Response;
import java.util.List;
import org.json.JSONObject;
import org.testng.Assert;

/**
 *
 * @author padhaval
 */
//@TestCasesGenerator(configFileName = "GetDeviceReservationById.properties")
public class GetDeviceReservationById {

    @GenerateTestCase
//  @SyncTestLink(fullTestCaseExternalId = "IMEI-2454", testStepNumber = 1, testPlanName = "TestDeviceRes_Automation", testBuildName = "TestDeviceReservationBuild", projectName = "IMEI Reservation")
    public void testGetReservationByIdSuccess(TestCaseData testCaseData) throws Exception {
        String findLatestReservationId = "SELECT snr.id FROM `serial_number` sn, `serial_number_reservation` snr WHERE sn.id = snr.`serial_number_id` AND sn.`reservable` = FALSE AND snr.`status` = 'RESERVED' ORDER BY snr.id DESC LIMIT 1";

        List<JSONObject> rowList = JDBCComponent.instance().executeQuery(findLatestReservationId);
        if (!rowList.isEmpty()) {
            testCaseData.setUrl(String.format(testCaseData.getUrl(), rowList.get(0).getInt("1")));
        }

        // method 1 to validate reponse using framework methods 
        // generally this method used by framework internally for static test cases
        testCaseData.setStatusCode(200);
        testCaseData.getAssertionMethodList().add(Utils.findMethodByName("assertEquals(java.lang.Object,java.lang.Object)"));
        Utils.parseAssertionMethodArgs(testCaseData.getAssertionArgsTypesList(), testCaseData.getAssertionArgsList(), "(java.lang.Object,java.lang.Object)", "RESERVED@.status");
        
        // method 2 to validate reponse by implemeting own
        // generally this method used for dynamic test cases
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                Assert.assertEquals(response.jsonPath().getString("status"), "RESERVED");
            }
        };
        
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }
    
    @GenerateTestCase
    @ParallelTestCasesGenerator(numOfThreads = 5)
    public void testParalleGetReservationByIdSuccess(TestCaseData testCaseData) throws Exception {
        testGetReservationByIdSuccess(testCaseData);
    }
}
