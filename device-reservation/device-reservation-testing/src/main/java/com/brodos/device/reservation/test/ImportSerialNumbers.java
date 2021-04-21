/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.device.reservation.test;

import com.brodos.device.reservation.Utils;
import com.brodos.test.components.JDBCComponent;
import com.brodos.test.data.CustomizedTestMethod;
import com.brodos.test.data.TestCaseData;
import com.brodos.test.markers.GenerateTestCase;
import com.brodos.test.markers.ParallelTestCasesGenerator;
import com.brodos.test.markers.TestCasesGenerator;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author padhaval
 */
//@TestCasesGenerator
public class ImportSerialNumbers {
    
    private Long publishVoucherImportEvent() throws Exception {
        String articleNumber = Utils.findArticleNumberNotHavingAnyOpenCase();

        return Utils.publishVoucherImportEvent(articleNumber);
    }

    @ParallelTestCasesGenerator(numOfThreads = 10)
    @GenerateTestCase(isCustomized = true)
    public void testImportSerialNumber(TestCaseData testCaseData) {
        CustomizedTestMethod customizedTestMethod = new CustomizedTestMethod() {
            @Override
            public void test() throws Exception {
                Long eventId = publishVoucherImportEvent();
                String sql = String.format("SELECT `number` FROM `serial_number` WHERE `number` = 'testimei%d'", eventId);
                JDBCComponent.instance().executeQuery(sql, 30, 1000);
                List<JSONObject> serialNumberDbResult = JDBCComponent.instance().executeQuery(String.format("SELECT `number`, `article_number` FROM `serial_number` WHERE `number` = 'testimei%d'", eventId), 30, 1000);
                String articleNumber = serialNumberDbResult.get(0).getString("2");
                Utils.publishVoucherRelocationEvent(articleNumber, eventId);
            }
        };
        
        testCaseData.setCustomizedTestMethod(customizedTestMethod);
    }
}
