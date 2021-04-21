/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.device.reservation.test;

import com.brodos.device.reservation.test.dto.DeviceReservationActionArgumentsDTO;
import com.brodos.device.reservation.test.dto.DeviceReservationActionDTO;
import com.brodos.device.reservation.test.dto.DeviceReservationReferenceDTO;
import com.brodos.device.reservation.test.dto.DeviceReservationRequestDTO;
import com.brodos.test.Utils;
import com.brodos.test.components.JDBCComponent;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseResponseValidator;
import com.brodos.test.markers.GenerateTestCase;
import com.brodos.test.markers.ParallelTestCasesGenerator;
import com.brodos.test.markers.TestCasesGenerator;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import org.json.JSONObject;
import org.testng.Assert;

/**
 *
 * @author padhaval
 */
//@TestCasesGenerator(configFileName = "DeviceReservation.properties")
@ParallelTestCasesGenerator(numOfThreads = 1)
public class DeviceReservationReferences {

    @GenerateTestCase (includeOnlyInParallel = true, order = 1)
    public void testDeviceReservationReferences(TestCaseData testCaseData) throws Exception {
        DeviceReservationRequestDTO deviceReservationRequestDTO = new DeviceReservationRequestDTO();

        String articleNumber = com.brodos.device.reservation.Utils.findArticleNumberNotHavingAnyOpenCase();
            
        deviceReservationRequestDTO.setArticleNo(articleNumber);
        deviceReservationRequestDTO.setGroup("40");
        DeviceReservationReferenceDTO deviceReservationReferenceDTO = new DeviceReservationReferenceDTO();
        deviceReservationReferenceDTO.setLabel("label1");
        deviceReservationReferenceDTO.setLabel("value1");
        deviceReservationRequestDTO.setReferences(new HashSet<>());
        deviceReservationRequestDTO.getReferences().add(deviceReservationReferenceDTO);

        testCaseData.setBody(Utils.writeValueAsString(deviceReservationRequestDTO, JsonInclude.Include.NON_NULL));
        testCaseData.setStatusCode(200);
        testCaseData.setDependencyOfNextTestCaseData(true);

        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
                Assert.assertTrue(response.getStatusCode() == 200);
                Assert.assertEquals(response.jsonPath().getString("status"), "OPEN");
                Assert.assertEquals(response.jsonPath().getString("articleNo"), deviceReservationRequestDTO.getArticleNo());
                ArrayList references = response.jsonPath().get("references");
                Assert.assertEquals(references.size(), deviceReservationRequestDTO.getReferences().size());
                LinkedHashMap ref = (LinkedHashMap) references.get(0);
                Assert.assertEquals(ref.get("label"), deviceReservationReferenceDTO.getLabel());
                Assert.assertEquals(ref.get("value"), deviceReservationReferenceDTO.getValue());
                testCaseData.getMetadata().put("reservationId", response.jsonPath().getInt("id"));
            }
        };
        
        Thread.sleep(2000);
        testCaseData.setDependencyOfNextTestCaseData(true);
        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }
    
    @GenerateTestCase (configFileName = "DeviceReservationActions.properties", includeOnlyInParallel = true, order = 2)
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
