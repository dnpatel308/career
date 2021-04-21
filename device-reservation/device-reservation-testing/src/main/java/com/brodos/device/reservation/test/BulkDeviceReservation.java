/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.device.reservation.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.Assert;

import com.brodos.device.reservation.test.dto.DeviceReservationRequestDTO;
import com.brodos.test.Utils;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseResponseValidator;
import com.brodos.test.markers.GenerateTestCase;
import com.brodos.test.markers.ParallelTestCasesGenerator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.brodos.test.markers.TestCasesGenerator;

import io.restassured.response.Response;

/**
 *
 * @author padhaval
 */
//@TestCasesGenerator(configFileName = "BulkDeviceReservation.properties")
@ParallelTestCasesGenerator(numOfThreads = 1)
public class BulkDeviceReservation {

    @GenerateTestCase(includeOnlyInParallel = true, order = 1)
    public void testBulkDeviceReservationSuccess(TestCaseData testCaseData) throws Exception {
        DeviceReservationRequestDTO deviceReservationRequestDTO = new DeviceReservationRequestDTO();

        String articleNo = com.brodos.device.reservation.Utils.findArticleNumberNotHavingAnyOpenCase();

        deviceReservationRequestDTO.setGroup("40");

        List<DeviceReservationRequestDTO> deviceReservationRequestDTOs = new ArrayList<>();

        deviceReservationRequestDTOs.add(deviceReservationRequestDTO);
        deviceReservationRequestDTOs.add(deviceReservationRequestDTO);

        testCaseData.setBody(Utils.writeValueAsString(deviceReservationRequestDTOs, JsonInclude.Include.NON_NULL));
        testCaseData.setStatusCode(200);
        testCaseData.setDependencyOfNextTestCaseData(true);

        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                Assert.assertEquals(response.jsonPath().getList("reservations").size(), deviceReservationRequestDTOs.size());                
            }
        };

        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }

    @GenerateTestCase(configFileName = "GetDeviceReservationsByBulkId.properties", includeOnlyInParallel = true, order = 2)
    public void testGetBulkDeviceReservationSuccess(TestCaseData testCaseData) throws Exception {
        TestCaseData dependentOnTestCaseData = testCaseData.getDependentOnTestCaseData();
        testCaseData.setUrl(String.format(testCaseData.getUrl(), ((LinkedHashMap)dependentOnTestCaseData.getResponse().jsonPath().getList("reservations").get(0)).get("bulkid")));
        testCaseData.setStatusCode(200);

        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                LinkedHashMap _embedded = response.jsonPath().get("_embedded");
                Assert.assertNotNull(_embedded);                
                ArrayList reservations = (ArrayList) _embedded.get("reservations");
                Assert.assertEquals(2, reservations.size());                
            }
        };

        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }
}
