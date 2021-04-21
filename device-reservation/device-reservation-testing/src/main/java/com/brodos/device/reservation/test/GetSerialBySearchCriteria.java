/**
 * 
 */
package com.brodos.device.reservation.test;

import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONObject;
import org.testng.Assert;

import com.brodos.test.components.JDBCComponent;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.data.TestCaseResponseValidator;
import com.brodos.test.markers.GenerateTestCase;
import com.brodos.test.markers.TestCasesGenerator;

import io.restassured.response.Response;

/**
 * @author snihit
 *
 * 
 */
//@TestCasesGenerator(configFileName = "GetSerialBySearchCriteria.properties")
public class GetSerialBySearchCriteria {

	@GenerateTestCase
	public void testGetSerialUsingImeiArticleNo(TestCaseData testCaseData) throws Exception {
		List<JSONObject> randomSerial = JDBCComponent.instance().executeQuery("SELECT `number`, `article_number` FROM `serial_number` ORDER BY RAND() LIMIT 1", 30, 1000);
		
		String serialNo = null;
		String articleNo = null;
		
		if(!randomSerial.isEmpty()) {
			serialNo = randomSerial.get(0).get("1").toString();
			articleNo = randomSerial.get(0).get("2").toString();
		}
		
		String queryParam = String.format("articlenumber=%s&serialnumber=%s", articleNo, serialNo) ;
		
		testCaseData.setUrl(String.format(testCaseData.getUrl(), queryParam));
		testCaseData.setStatusCode(200);
		TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
            @Override
            public void validateResponse(Response response) {
            	Assert.assertTrue(response.getStatusCode() == 200);
            	LinkedHashMap serial = response.jsonPath().getJsonObject("serial");
            	String id = serial.get("id").toString();
            	
            	List<JSONObject> serialData = null;
            	
            	try {
					 serialData = JDBCComponent.instance().executeQuery("SELECT * FROM `serial_number` WHERE `id` ='" + id +"'", 30, 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}

            	if(!serialData.isEmpty()) {
                	Assert.assertEquals(serial.get("number").toString(), serialData.get(0).get("2").toString());
                	Assert.assertEquals(serial.get("warehouse-id").toString(), serialData.get(0).get("17").toString());
                	Assert.assertEquals(serial.get("article-number").toString(), serialData.get(0).get("6").toString());
            	}
            }
		};
		testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
	}
}
