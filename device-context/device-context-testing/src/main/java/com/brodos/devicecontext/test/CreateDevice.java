/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.test;

import com.brodos.devicecontext.test.dto.CreateOrUpdateDeviceConfigDTO;
import com.brodos.devicecontext.test.dto.DeviceConfigFieldDTO;
import com.brodos.devicecontext.test.dto.CreateOrUpdateDeviceDTO;
import com.brodos.devicecontext.test.dto.DeviceFieldDTO;
import com.brodos.devicecontext.test.dto.DeviceFieldNameEnum;
import com.brodos.test.Utils;
import com.brodos.test.components.JDBCComponent;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseResponseValidator;
import com.brodos.test.markers.GenerateTestCase;
import com.brodos.test.markers.ParallelTestCasesGenerator;
import com.brodos.test.markers.TestCasesGenerator;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.restassured.response.Response;

import static com.brodos.test.Utils.writeValueAsString;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.testng.Assert;

/**
 *
 * @author padhaval
 */
//@TestCasesGenerator(configFileName = "CreateDevice.properties")
@ParallelTestCasesGenerator(numOfThreads = 1)
public class CreateDevice {
	
	private CreateOrUpdateDeviceConfigDTO createDeviceConfigIfNotExists(String articleNo) throws Exception {
		String findDeviceConfigForGivenArticleNo = "SELECT * FROM `device_config` WHERE `articlenumber` = '" + articleNo
				+ "'";
		List<JSONObject> deviceConfigResult = JDBCComponent.instance()
				.executeQuery(findDeviceConfigForGivenArticleNo);

		if (deviceConfigResult.isEmpty()) {
			CreateOrUpdateDeviceConfigDTO createOrUpdateDeviceConfigDTO = new CreateOrUpdateDeviceConfigDTO();
			createOrUpdateDeviceConfigDTO.setArticleNo(articleNo);
			createOrUpdateDeviceConfigDTO.setFields(new HashSet<>());
			DeviceConfigFieldDTO imei1DeviceConfigFieldDTO = new DeviceConfigFieldDTO();
			imei1DeviceConfigFieldDTO.setName("imei1");
			imei1DeviceConfigFieldDTO.setType("imei");
			DeviceConfigFieldDTO imei2DeviceConfigFieldDTO = new DeviceConfigFieldDTO();
			//code to add imei2 into device config
			imei2DeviceConfigFieldDTO.setName("imei2");
			imei1DeviceConfigFieldDTO.setType("imei");
			DeviceConfigFieldDTO serialDeviceConfigFieldDTO = new DeviceConfigFieldDTO();
			serialDeviceConfigFieldDTO.setName("serial");
			imei1DeviceConfigFieldDTO.setType("string(40)");
			createOrUpdateDeviceConfigDTO.getFields().add(imei1DeviceConfigFieldDTO);
			createOrUpdateDeviceConfigDTO.getFields().add(imei2DeviceConfigFieldDTO);
			createOrUpdateDeviceConfigDTO.getFields().add(serialDeviceConfigFieldDTO);
			return createOrUpdateDeviceConfigDTO;
		}

		return null;
	}
	
	@GenerateTestCase(includeOnlyInParallel = true, order = 1, configFileName = "CreateDeviceConfig.properties")
	public void testDeviceConfigurationAvailability(TestCaseData testCaseData) throws Exception {

		String articleNumberSQL = "SELECT `article_number` FROM `article` WHERE `is_serial` IS TRUE LIMIT 1";

		List<JSONObject> articleNo = JDBCComponent.instance("jdbc1.properties").executeQuery(articleNumberSQL, 30, 100);
		
		String articleNumber = null;
		
		if(!articleNo.isEmpty()) {
			articleNumber = articleNo.get(0).get("1").toString();
		}
		
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
	
    @GenerateTestCase (includeOnlyInParallel = true, order = 2)
    public void testCreateDeviceSuccess(TestCaseData testCaseData) throws Exception {
    	TestCaseData dependentOnTestCaseData = testCaseData.getDependentOnTestCaseData();
    	
    	String articleNumber = (String) dependentOnTestCaseData.getMetadata().get("articleNo");
    	
        CreateOrUpdateDeviceDTO createOrUpdateDeviceDTO = new CreateOrUpdateDeviceDTO();
        createOrUpdateDeviceDTO.setArticleNo(articleNumber);
        createOrUpdateDeviceDTO.setFields(new HashSet<>());

        long currentTimeMillis = System.currentTimeMillis();

        DeviceFieldDTO imei1 = new DeviceFieldDTO();
        imei1.setName(DeviceFieldNameEnum.imei1);
        imei1.setValue(currentTimeMillis + "1");
        createOrUpdateDeviceDTO.getFields().add(imei1);

        DeviceFieldDTO imei2 = new DeviceFieldDTO();
        imei2.setName(DeviceFieldNameEnum.imei2);
        imei2.setValue(currentTimeMillis + "2");
        createOrUpdateDeviceDTO.getFields().add(imei2);

        DeviceFieldDTO serial = new DeviceFieldDTO();
        serial.setName(DeviceFieldNameEnum.serial);
        serial.setValue(currentTimeMillis + "3");
        createOrUpdateDeviceDTO.getFields().add(serial);
        
        testCaseData.setBody(Utils.writeValueAsString(createOrUpdateDeviceDTO, JsonInclude.Include.NON_NULL));
        testCaseData.setStatusCode(200);

        TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {

            @Override
            public void validateResponse(Response response) {
                try {                    
                    Assert.assertTrue(response.getStatusCode() == 200);
                    Assert.assertEquals(response.jsonPath().getString("status"), "COMPLETE");
                    Assert.assertEquals(response.jsonPath().getString("articlenumber"), createOrUpdateDeviceDTO.getArticleNo());
                    String sql = "SELECT `articlenumber` FROM `device` WHERE `articlenumber` = '%s'";
                    List<JSONObject> dbresult = JDBCComponent.instance().executeQuery(String.format(sql, createOrUpdateDeviceDTO.getArticleNo()));
                    Assert.assertEquals(createOrUpdateDeviceDTO.getArticleNo(), dbresult.get(0).get("1"));
                } catch (Exception ex) {
                    Assert.fail(ex.getMessage());
                }
            }
        };

        testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
    }
}
