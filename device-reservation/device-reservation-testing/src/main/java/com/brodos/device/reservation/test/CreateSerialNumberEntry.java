/**
 * 
 */
package com.brodos.device.reservation.test;

import static com.brodos.test.Utils.writeValueAsString;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONObject;
import org.testng.Assert;

import com.brodos.device.reservation.Utils;
import com.brodos.device.reservation.test.dto.CreateOrUpdateDeviceConfigDTO;
import com.brodos.device.reservation.test.dto.CreateOrUpdateDeviceDTO;
import com.brodos.device.reservation.test.dto.DeviceConfigFieldDTO;
import com.brodos.device.reservation.test.dto.DeviceFieldDTO;
import com.brodos.device.reservation.test.dto.ProductCodeDTO;
import com.brodos.device.reservation.test.dto.SerialDTO;
import com.brodos.device.reservation.test.dto.SerialNumberRequestDTO;
import com.brodos.test.components.JDBCComponent;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseResponseValidator;
import com.brodos.test.markers.GenerateTestCase;
import com.brodos.test.markers.ParallelTestCasesGenerator;
import com.brodos.test.markers.TestCasesGenerator;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.text.SimpleDateFormat;  
import java.util.Date;  

import io.restassured.response.Response;

/**
 * @author snihit
 *
 * 
 */
@TestCasesGenerator(configFileName = "CreateSerialNumberEntry.properties")
@ParallelTestCasesGenerator(numOfThreads = 1)
public class CreateSerialNumberEntry {
	
	private String date() {
		Date date = new Date();  
	    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");  
	    String strDate= formatter.format(date);
	    return strDate ;
	}
	
	//Method to generate random number
	
//	private String randomNumber() {
//		ArrayList<Integer> list = new ArrayList<Integer>();
//        for (int i=1000; i<1100; i++) {
//            list.add(new Integer(i));
//        }
//        Collections.shuffle(list);
//        
//        String randomNumber = list.get(0).toString();
//		return randomNumber;
//	}
	
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
	
	@GenerateTestCase(includeOnlyInParallel = true, order = 1, headers = "{\"X-BRODOS-API-KEY\": \"1f0c3c55-8fd1-4fcd-b925-51320dd62f14\"}", configFileName = "CreateDeviceConfig.properties")
	public void testDeviceConfigurationAvailability(TestCaseData testCaseData) throws Exception {

		String articleNumber = Utils.findArticleNumberNotHavingAnyOpenCase();

		CreateOrUpdateDeviceConfigDTO createOrUpdateDeviceConfigDTO = createDeviceConfigIfNotExists(articleNumber);
		if (createOrUpdateDeviceConfigDTO != null) {
			testCaseData.setStatusCode(200);
			testCaseData.setBody(writeValueAsString(createOrUpdateDeviceConfigDTO, JsonInclude.Include.NON_NULL));
		} else {
			testCaseData.setActive(Boolean.FALSE);
		}
		testCaseData.getMetadata().put("articleNo", articleNumber);
		testCaseData.setDependencyOfNextTestCaseData(true);
		testCaseData.setDependentOnTestCaseData(testCaseData);
	}

	@GenerateTestCase(includeOnlyInParallel = true, order = 2, headers = "{\"X-BRODOS-API-KEY\": \"1f0c3c55-8fd1-4fcd-b925-51320dd62f14\"}", configFileName = "CreateDevice.properties")
	public void testDeviceAvailability(TestCaseData testCaseData) throws Exception {

		TestCaseData dependentOnTestCaseData = testCaseData.getDependentOnTestCaseData();
		String articleNumber = (String) dependentOnTestCaseData.getMetadata().get("articleNo");
		
		List<JSONObject> latestSerialNo = JDBCComponent.instance().executeQuery("SELECT `id` FROM `serial_number` ORDER BY `id` DESC LIMIT 1", 30, 1000);
		
		String latestSerialNoId = null;
		
		if (!latestSerialNo.isEmpty()) {
			latestSerialNoId = latestSerialNo.get(0).get("1").toString();
		}
		
		String imei = date() + latestSerialNoId + "1" ;
		
//		String imei = date() + randomNumber() + "1" ; //imei in case need to use for parallel execution to avoid failures

		CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO = createDeviceIfNotExists(articleNumber, imei);
		if (createOrUpdateDeviceDTO != null) {
			testCaseData.setStatusCode(200);
			testCaseData.setBody(writeValueAsString(createOrUpdateDeviceDTO, JsonInclude.Include.NON_NULL));
		} else {
			testCaseData.setActive(Boolean.FALSE);
		}
		testCaseData.getMetadata().put("articleNo", articleNumber);
		testCaseData.setDependencyOfNextTestCaseData(true);
		testCaseData.setDependentOnTestCaseData(testCaseData);
		testCaseData.getMetadata().put("imei", imei);
	}
	
	@GenerateTestCase(includeOnlyInParallel = true, order = 3)
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
				try {
					Utils.publishVoucherRelocationEventUsingIMEI(articleNumber, imei); //comment this line if you want to import imei for reservation purpose
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			} ;
			
			testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
	}

}
