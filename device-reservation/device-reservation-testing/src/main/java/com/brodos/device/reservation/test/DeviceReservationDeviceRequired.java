/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.device.reservation.test;

import com.brodos.test.components.JDBCComponent;
import com.brodos.device.reservation.test.dto.DeviceReservationActionArgumentsDTO;
import com.brodos.device.reservation.test.dto.DeviceReservationActionDTO;
import com.brodos.device.reservation.test.dto.DeviceReservationRequestDTO;
import com.brodos.test.Utils;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseResponseValidator;
import com.brodos.test.markers.GenerateTestCase;
import com.brodos.test.markers.ParallelTestCasesGenerator;
import com.brodos.test.markers.TestCasesGenerator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.response.Response;
import java.util.List;
import org.json.JSONObject;
import org.testng.Assert;

/**
 *
 * @author padhaval
 */
//@TestCasesGenerator(configFileName = "DeviceReservation.properties")
@ParallelTestCasesGenerator(numOfThreads = 1)
public class DeviceReservationDeviceRequired {
    
    @GenerateTestCase (includeOnlyInParallel = true, order = 1)
    public void testDeviceRequiredTrueWtihSerialNumberUnavailable(TestCaseData testCaseData) throws Exception {
        DeviceReservationRequestDTO deviceReservationRequestDTO = new DeviceReservationRequestDTO();
        
	    String articleNumber = com.brodos.device.reservation.Utils.findArticleNumberNotHavingAnyOpenCase();
        
        deviceReservationRequestDTO.setArticleNo(articleNumber);
        deviceReservationRequestDTO.setGroup("40");
        deviceReservationRequestDTO.setDeviceRequired(Boolean.TRUE);
        testCaseData.setBody(Utils.writeValueAsString(deviceReservationRequestDTO, JsonInclude.Include.NON_NULL));
        testCaseData.setStatusCode(200);
        
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                Assert.assertEquals(response.jsonPath().getString("status"), "OPEN");
                Assert.assertEquals(response.jsonPath().getString("articleNo"), deviceReservationRequestDTO.getArticleNo());
                try {
                    List<JSONObject> serialNumberReservationDbResult = JDBCComponent.instance().executeQuery("SELECT `id`, `status` FROM `serial_number_reservation` WHERE `id` = " + response.jsonPath().getInt("id") + " AND `status` = 'CANCELLED'", 30, 1000);
                    Assert.assertTrue(!serialNumberReservationDbResult.isEmpty());                    
                } catch (Exception ex) {
                    Assert.fail(ex.getMessage());
                }
            }
        };
                
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }
    
    @GenerateTestCase (includeOnlyInParallel = true, order = 2)
    public void testDeviceRequiredFalseWtihSerialNumberUnavailable(TestCaseData testCaseData) throws Exception {
        DeviceReservationRequestDTO deviceReservationRequestDTO = new DeviceReservationRequestDTO();
        
        String articleNumber = com.brodos.device.reservation.Utils.findArticleNumberNotHavingAnyOpenCase();
        
        deviceReservationRequestDTO.setArticleNo(articleNumber);
        deviceReservationRequestDTO.setGroup("40");
        deviceReservationRequestDTO.setDeviceRequired(Boolean.FALSE);
        testCaseData.setBody(Utils.writeValueAsString(deviceReservationRequestDTO, JsonInclude.Include.NON_NULL));
        testCaseData.setStatusCode(200);
        
        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                Assert.assertEquals(response.jsonPath().getString("status"), "OPEN");
                Assert.assertEquals(response.jsonPath().getString("articleNo"), deviceReservationRequestDTO.getArticleNo());
                try {
                    List<JSONObject> serialNumberReservationDbResult = JDBCComponent.instance().executeQuery("SELECT `id`, `status` FROM `serial_number_reservation` WHERE `id` = " + response.jsonPath().getInt("id") + " AND `status` = 'PENDING'", 30, 1000);
                    Assert.assertTrue(!serialNumberReservationDbResult.isEmpty());
                    testCaseData.getMetadata().put("reservationId", response.jsonPath().getInt("id"));
                } catch (Exception ex) {
                    Assert.fail(ex.getMessage());
                }
            }
        };
        testCaseData.setDependencyOfNextTestCaseData(true);
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }
    
	@GenerateTestCase (configFileName = "DeviceReservationActions.properties", includeOnlyInParallel = true, order = 3)
    public void cancelPendingReservation(TestCaseData testCaseData) throws Exception {
		
		testCaseData.setUrl(String.format(testCaseData.getUrl(), testCaseData.getDependentOnTestCaseData().getMetadata().get("reservationId") ));
		
		DeviceReservationActionDTO deviceReservationActionDTO = new DeviceReservationActionDTO();
		deviceReservationActionDTO.setType("cancel");
		deviceReservationActionDTO
				.setArguments(new DeviceReservationActionArgumentsDTO("Contract was declined by provider"));
		
		testCaseData.setBody(Utils.writeValueAsString(deviceReservationActionDTO, JsonInclude.Include.NON_NULL));
		testCaseData.setStatusCode(200);
		TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
			@Override
			public void validateResponse(Response response) {
				Assert.assertTrue(response.getStatusCode() == 200);
				Assert.assertEquals(response.jsonPath().getString("status"), "CANCELLED");
			}
		};
		testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
	}
}
