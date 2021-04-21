/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.device.reservation.test;

import com.brodos.test.components.JDBCComponent;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseResponseValidator;
import com.brodos.device.reservation.test.dto.CreateOrUpdateDeviceConfigDTO;
import com.brodos.device.reservation.test.dto.CreateOrUpdateDeviceDTO;
import com.brodos.device.reservation.test.dto.DeviceConfigFieldDTO;
import com.brodos.device.reservation.test.dto.DeviceFieldDTO;
import com.brodos.device.reservation.test.dto.DeviceReservationRequestDTO;
import com.brodos.device.reservation.test.dto.ProductCodeDTO;
import com.brodos.device.reservation.test.dto.SerialDTO;
import com.brodos.device.reservation.test.dto.SerialNumberRequestDTO;
import com.brodos.test.markers.GenerateTestCase;
import com.brodos.test.markers.ParallelTestCasesGenerator;
import com.brodos.test.markers.TestCasesGenerator;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.restassured.response.Response;

import static com.brodos.test.Utils.writeValueAsString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import org.json.JSONObject;
import org.testng.Assert;
import com.brodos.device.reservation.Utils;

/**
 *
 * @author padhaval
 */
//@TestCasesGenerator(configFileName = "DeviceReservation.properties")
@ParallelTestCasesGenerator(numOfThreads = 1)
public class CreateHandleAndGetDeviceReservationUsingCreateSerialEndPoint {
	
	private String date() {
		Date date = new Date();  
	    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");  
	    String strDate= formatter.format(date);
	    return strDate ;
	}

	private CreateOrUpdateDeviceConfigDTO createDeviceConfigIfNotExists(String articleNo) throws Exception {
		String findDeviceConfigForGivenArticleNo = "SELECT * FROM `device_config` WHERE `articlenumber` = '" + articleNo
				+ "'";
		List<JSONObject> deviceConfigResult = JDBCComponent.instance("jdbc1.properties")
				.executeQuery(findDeviceConfigForGivenArticleNo);

		if (deviceConfigResult.isEmpty()) {
			CreateOrUpdateDeviceConfigDTO createOrUpdateDeviceConfigDTO = new CreateOrUpdateDeviceConfigDTO();
			createOrUpdateDeviceConfigDTO.setArticleNo(articleNo);
			createOrUpdateDeviceConfigDTO.setFields(new HashSet<>());
			DeviceConfigFieldDTO imei1DeviceConfigFieldDTO = new DeviceConfigFieldDTO();
			imei1DeviceConfigFieldDTO.setName("imei1");
			imei1DeviceConfigFieldDTO.setType("imei");
			// DeviceConfigFieldDTO imei2DeviceConfigFieldDTO = new DeviceConfigFieldDTO();
			// code to add imei2 into device config
			// imei2DeviceConfigFieldDTO.setName("imei2");
			// imei1DeviceConfigFieldDTO.setType("imei");
			DeviceConfigFieldDTO serialDeviceConfigFieldDTO = new DeviceConfigFieldDTO();
			serialDeviceConfigFieldDTO.setName("serial");
			imei1DeviceConfigFieldDTO.setType("string(40)");
			createOrUpdateDeviceConfigDTO.getFields().add(imei1DeviceConfigFieldDTO);
			// createOrUpdateDeviceConfigDTO.getFields().add(imei2DeviceConfigFieldDTO);
			createOrUpdateDeviceConfigDTO.getFields().add(serialDeviceConfigFieldDTO);
			return createOrUpdateDeviceConfigDTO;
		}

		return null;
	}

	private CreateOrUpdateDeviceDTO createDeviceIfNotExists(String articleNo, String imei) throws Exception {
		String findDeviceforGivenArticleNSerial = "SELECT d.* FROM device d JOIN device_field df ON df.device_id = d.id WHERE d.articlenumber = '"
				+ articleNo + "' AND (df.name = 'imei1' OR df.name = 'imei2') AND df.value = '" + imei + "'";

		List<JSONObject> deviceFindingResult = JDBCComponent.instance("jdbc1.properties")
				.executeQuery(findDeviceforGivenArticleNSerial);

		if (deviceFindingResult.isEmpty()) {
			CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO = new CreateOrUpdateDeviceDTO();
			createOrUpdateDeviceDTO.setArticleNo(articleNo);
			createOrUpdateDeviceDTO.setFields(new HashSet<>());
			DeviceFieldDTO imei1DeviceFieldDTO = new DeviceFieldDTO();
			imei1DeviceFieldDTO.setName("imei1");
			imei1DeviceFieldDTO.setType("imei");
			imei1DeviceFieldDTO.setValue(imei);
//	       	DeviceFieldDTO imei2DeviceFieldDTO = new DeviceFieldDTO();
//	        	imei1DeviceFieldDTO.setName("imei2");
//	        	imei1DeviceFieldDTO.setValue(imei + "1" );
//	        	imei1DeviceFieldDTO.setType("imei");
			DeviceFieldDTO serialDeviceFieldDTO = new DeviceFieldDTO();
			serialDeviceFieldDTO.setName("serial");
			serialDeviceFieldDTO.setType("string(40)");
			serialDeviceFieldDTO.setValue("serial" + imei);
			createOrUpdateDeviceDTO.getFields().add(imei1DeviceFieldDTO);
			createOrUpdateDeviceDTO.getFields().add(serialDeviceFieldDTO);
		}

		return null;

	}

	@GenerateTestCase(includeOnlyInParallel = true, order = 1)
	public void testCreateDeviceReservationSuccess(TestCaseData testCaseData) throws Exception {
		DeviceReservationRequestDTO deviceReservationRequestDTO = new DeviceReservationRequestDTO();

//		String articleNumber = Utils.findArticleNumberNotHavingAnyOpenCase();
		String articleNumber = "APIPH8-G64";
		
		deviceReservationRequestDTO.setArticleNo(articleNumber);
		deviceReservationRequestDTO.setGroup("40");

		testCaseData.setBody(
				com.brodos.test.Utils.writeValueAsString(deviceReservationRequestDTO, JsonInclude.Include.NON_NULL));
		testCaseData.setStatusCode(200);
		testCaseData.setDependencyOfNextTestCaseData(true);

		final String articleNo = deviceReservationRequestDTO.getArticleNo();
		TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
			@Override
			public void validateResponse(Response response) {
				Assert.assertTrue(response.getStatusCode() == 200);
				Assert.assertEquals(response.jsonPath().getString("status"), "OPEN");
				Assert.assertEquals(response.jsonPath().getString("articleNo"), articleNo);
				try {
					JDBCComponent.instance().executeQuery("SELECT * FROM `serial_number_reservation` WHERE `id` = '"
							+ response.jsonPath().getInt("id") + "' AND `status` = 'PENDING'", 30, 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);

		testCaseData.getMetadata().put("articleNo", articleNumber);
		testCaseData.setDependencyOfNextTestCaseData(true);
		testCaseData.setDependentOnTestCaseData(testCaseData);
	}

	@GenerateTestCase(includeOnlyInParallel = true, order = 2, headers = "{\"X-BRODOS-API-KEY\": \"1f0c3c55-8fd1-4fcd-b925-51320dd62f14\"}", configFileName = "CreateDeviceConfig.properties")
	public void testDeviceConfigurationAvailability(TestCaseData testCaseData) throws Exception {

		TestCaseData dependentOnTestCaseData = testCaseData.getDependentOnTestCaseData();
		String articleNumber = (String) dependentOnTestCaseData.getMetadata().get("articleNo");

		CreateOrUpdateDeviceConfigDTO createOrUpdateDeviceConfigDTO = createDeviceConfigIfNotExists(articleNumber);
		
		if (createOrUpdateDeviceConfigDTO != null) {
			testCaseData.setStatusCode(200);
			testCaseData.setBody(writeValueAsString(createOrUpdateDeviceConfigDTO, JsonInclude.Include.NON_NULL));
		} else {
			testCaseData.setActive(Boolean.FALSE);
		}
		testCaseData.getMetadata().put("articleNo", articleNumber);
		testCaseData.getMetadata().put("reservationId", dependentOnTestCaseData.getResponse().jsonPath().getInt("id"));
		testCaseData.setDependencyOfNextTestCaseData(true);
		testCaseData.setDependentOnTestCaseData(testCaseData);
	}

	@GenerateTestCase(includeOnlyInParallel = true, order = 3, headers = "{\"X-BRODOS-API-KEY\": \"1f0c3c55-8fd1-4fcd-b925-51320dd62f14\"}", configFileName = "CreateDevice.properties")
	public void testDeviceAvailability(TestCaseData testCaseData) throws Exception {

		TestCaseData dependentOnTestCaseData = testCaseData.getDependentOnTestCaseData();
		String articleNumber = (String) dependentOnTestCaseData.getMetadata().get("articleNo");
		
		List<JSONObject> latestSerialNo = JDBCComponent.instance().executeQuery("SELECT `id` FROM `serial_number` ORDER BY `id` DESC LIMIT 1", 30, 1000);
		
		String latestSerialNoId = null;
		
		if (!latestSerialNo.isEmpty()) {
			latestSerialNoId = latestSerialNo.get(0).get("1").toString();
		}
		
		String imei = date() + latestSerialNoId + "1" ;

		CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO = createDeviceIfNotExists(articleNumber, imei);
		if (createOrUpdateDeviceDTO != null) {
			testCaseData.setStatusCode(200);
			testCaseData.setBody(writeValueAsString(createOrUpdateDeviceDTO, JsonInclude.Include.NON_NULL));
		} else {
			testCaseData.setActive(Boolean.FALSE);
		}
		testCaseData.getMetadata().put("articleNo", articleNumber);
		testCaseData.getMetadata().put("reservationId", dependentOnTestCaseData.getMetadata().get("reservationId"));
		testCaseData.getMetadata().put("imei", imei);
		testCaseData.setDependencyOfNextTestCaseData(true);
		testCaseData.setDependentOnTestCaseData(testCaseData);
	}
	
	@GenerateTestCase(includeOnlyInParallel = true, order = 4, configFileName = "CreateSerialNumberEntry.properties")
	public void testCreateSerialNumber(TestCaseData testCaseData) throws Exception {
		TestCaseData dependentOnTestCaseData = testCaseData.getDependentOnTestCaseData();
		
		SerialDTO serialDTO = new SerialDTO();
		
		serialDTO.setProductCodeDTOs(new ArrayList<>());
		String articleNumber = (String) dependentOnTestCaseData.getMetadata().get("articleNo");
		String imei = (String) dependentOnTestCaseData.getMetadata().get("imei");
		String EAN = null;
		List<JSONObject> ean = JDBCComponent.instance().executeQuery("SELECT `code` FROM `product_code` WHERE `article_number` = '"+ articleNumber +"' LIMIT 1", 30, 1000);
		
		if (!ean.isEmpty()) {
			EAN = ean.get(0).get("1").toString();
			
			ProductCodeDTO productCodeDTO = new ProductCodeDTO();
			productCodeDTO.setType("EAN");
			productCodeDTO.setValue(EAN);
			serialDTO.getProductCodeDTOs().add(productCodeDTO);
		}
		
		serialDTO.setArticleNumber(articleNumber);
		serialDTO.setNumber(imei);
		serialDTO.setWarehouseId(40);
		serialDTO.setTenantId((long) 1);
		
		SerialNumberRequestDTO serialNumberRequestDTO = new SerialNumberRequestDTO();
		
		serialNumberRequestDTO.setSerialDTO(serialDTO);
		
		testCaseData.setStatusCode(201);
		testCaseData.setBody(writeValueAsString(serialNumberRequestDTO, JsonInclude.Include.NON_NULL));
			
		TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
			@Override
			public void validateResponse(Response response) {
				
				List<JSONObject> id = null;
				try {
					id = JDBCComponent.instance().executeQuery("SELECT `id` FROM `serial_number` ORDER BY `id` DESC LIMIT 1", 30, 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				String latestSerialNoId = null;
				int latestSerialNoID = 0 ;
				if (!id.isEmpty()) {
					latestSerialNoId = id.get(0).get("1").toString();
					latestSerialNoID = Integer.parseInt(latestSerialNoId);	
				}
				
				Assert.assertTrue(response.getStatusCode() == 201);
				LinkedHashMap serial = response.jsonPath().getJsonObject("serial") ;
				Assert.assertEquals(serial.get("id"), latestSerialNoID);
			}
			} ;
			
			testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
			testCaseData.setDependencyOfNextTestCaseData(true);
			testCaseData.getMetadata().put("reservationId", dependentOnTestCaseData.getMetadata().get("reservationId"));
	}

	@GenerateTestCase(configFileName = "GetDeviceReservationById.properties", includeOnlyInParallel = true, order = 5)
	public void testDeviceReservationSuccess(TestCaseData testCaseData) throws Exception {
		TestCaseData dependentOnTestCaseData = testCaseData.getDependentOnTestCaseData();
		
		testCaseData.setUrl(String.format(testCaseData.getUrl(), dependentOnTestCaseData.getMetadata().get("reservationId")));
		testCaseData.setStatusCode(200);
		TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
			@Override
			public void validateResponse(Response response) {
				Assert.assertTrue(response.getStatusCode() == 200);
				Assert.assertEquals(response.jsonPath().getString("status"), "RESERVED");
			}
		};
		testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
	}
}
