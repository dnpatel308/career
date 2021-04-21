/**
 * 
 */
package com.brodos.device.reservation.test;

import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONObject;
import org.testng.Assert;

import com.brodos.test.Utils;
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
//@TestCasesGenerator(configFileName = "GetSerialById.properties")
public class GetSerialById {
	
	@GenerateTestCase
	public void testGetSerialById (TestCaseData testCaseData) throws Exception
	{
		List<JSONObject> latestSerialNo = JDBCComponent.instance().executeQuery("SELECT `id`, `number` FROM `serial_number` ORDER BY `id` DESC LIMIT 1", 30, 1000);
		
		String tempSerialNumber = null;
		
		if (!latestSerialNo.isEmpty()) {
			testCaseData.setUrl(String.format(testCaseData.getUrl(),latestSerialNo.get(0).get("1")));
			tempSerialNumber = latestSerialNo.get(0).get("2").toString();
		}
		
		final String serialNumber = tempSerialNumber ;
		
		testCaseData.setStatusCode(200);
		testCaseData.getAssertionMethodList().add(Utils.findMethodByName("assertEquals(java.lang.Object,java.lang.Object)"));
		
		Utils.parseAssertionMethodArgs(testCaseData.getAssertionArgsTypesList(), testCaseData.getAssertionArgsList(), "(java.lang.Object,java.lang.Object)", serialNumber + "@.serial.number");
	    
		 TestCaseResponseValidator testCaseResponseValidator = new TestCaseResponseValidator() {
	            @Override
	            public void validateResponse(Response response) {
	                Assert.assertTrue(response.getStatusCode() == 200);
	                LinkedHashMap serial = response.jsonPath().getJsonObject("serial") ;
					Assert.assertEquals(serial.get("number"), serialNumber);
	            }
	};
	
	 testCaseData.setTestCaseResponseValidator(testCaseResponseValidator);
}
}
