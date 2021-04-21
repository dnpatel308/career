/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.device.reservation;

import com.brodos.test.TestNGRunner;
import com.brodos.test.components.JDBCComponent;
import com.brodos.test.components.JMSComponent;
import java.util.List;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.json.JSONObject;

/**
 *
 * @author padhaval
 */
public class Utils {

    public static synchronized Long publishVoucherImportEvent(String articleNumber) throws Exception {
        String voucherImportEventBody = "{\"id\":112501,\"eventBody\":\"{\\\"voucherno\\\":\\\"XX-0000028\\\",\\\"vouchertype\\\":\\\"D\\\",\\\"voucherrefno\\\":[],\\\"voucherdate\\\":\\\"2019-01-23\\\",\\\"valutadate\\\":\\\"2019-01-23\\\",\\\"vatrated\\\":true,\\\"tenantid\\\":\\\"1\\\",\\\"payment\\\":{},\\\"currency\\\":\\\"EUR\\\",\\\"dispatch\\\":{},\\\"officer\\\":\\\"PKASSANDRA\\\",\\\"fromwarehouse\\\":\\\"1\\\",\\\"towarehouse\\\":\\\"40\\\",\\\"weight\\\":\\\"0.00\\\",\\\"generatepdf\\\":true,\\\"addresses\\\":[{},{},{}],\\\"monetaryamout\\\":[{\\\"type\\\":\\\"lineitems\\\",\\\"amount\\\":120.5}],\\\"lineitems\\\":[{\\\"lineitemno\\\":\\\"1.00\\\",\\\"poscode\\\":\\\"A\\\",\\\"parcelnumbers\\\":[],\\\"references\\\":{\\\"lineitemreference\\\":[],\\\"voucherreference\\\":[],\\\"externalreference\\\":[]},\\\"articleno\\\":\\\"%s\\\",\\\"stockkey\\\":\\\"1\\\",\\\"quantity\\\":1.0,\\\"delivered\\\":1.0,\\\"backlog\\\":0.0,\\\"pfactor\\\":1.0,\\\"qfactor\\\":1.0,\\\"sumtype\\\":\\\"7\\\",\\\"purchaseprice\\\":{\\\"averageprice\\\":0.0,\\\"ratedprice\\\":120.5,\\\"balanceprice\\\":0.0},\\\"retailprice\\\":{\\\"retailprice\\\":120.5,\\\"discount1\\\":0.0,\\\"discount2\\\":0.0},\\\"vat\\\":[{\\\"rate\\\":0.0}],\\\"kto\\\":\\\"3400\\\",\\\"descriptions\\\":[{\\\"lang\\\":\\\"DEU\\\",\\\"pos\\\":\\\"1\\\",\\\"value\\\":\\\"Samsung Galaxy Tab E 9.6 Zoll, Wi-Fi,\\\"},{\\\"lang\\\":\\\"DEU\\\",\\\"pos\\\":\\\"2\\\",\\\"value\\\":\\\"white, 1,3 GHz QC, 1,5GB RAM, 8GB ROM\\\"}],\\\"serials\\\":[{\\\"key\\\":\\\"imei\\\",\\\"value\\\":\\\"testimei%d\\\"}]}]}\",\"voucherType\":\"D\",\"eventName\":\"com.brodos.voucher.producer.entity.VoucherEvent\",\"occuredOn\":1584596914304,\"systemId\":\"1\",\"voucherNo\":\"XX-0000028\",\"version\":0,\"createdDttm\":null}";

        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();

        Long eventId = null;        
        String findNextEventId = "SELECT IF(MAX(`id`) + 1 IS NULL, 1,  MAX(`id`) + 1) FROM `voucher_events`";
        List<JSONObject> eventIdResult = JDBCComponent.instance().executeQuery(findNextEventId);
        if (!eventIdResult.isEmpty()) {
            eventId = eventIdResult.get(0).getLong("1");
            eventId += com.brodos.test.Utils.getUniqueId();
        }

        textMessage.setProperty("eventId", Integer.valueOf(String.valueOf(eventId)));
        textMessage.setText(String.format(voucherImportEventBody, articleNumber, Integer.valueOf(String.valueOf(eventId))));

        if (TestNGRunner.instance().isDebugEnabled()) {
            System.out.println("Publishing voucher import event..." + textMessage);
            System.out.println(Thread.currentThread().getId() + ": " + eventId);
            System.out.println(Thread.currentThread().getId() + ": " + articleNumber);
        }
        
        JMSComponent.instance().getMessageProducer().send(textMessage);
   //   JDBCComponent.instance().executeQuery(String.format("SELECT * FROM `serial_number` WHERE NUMBER = \"testimei%d\" AND reservable = FALSE AND is_archived = FALSE", eventId) , 30, 1000);
        
        return eventId;
    }
    
     public static synchronized Long publishVoucherSentoutEvent(String articleNumber, String serialNumber) throws Exception {
        String voucherImportEventBody = "{\"id\":112501,\"eventBody\":\"{\\\"voucherno\\\":\\\"XX-0000028\\\",\\\"vouchertype\\\":\\\"L\\\",\\\"voucherrefno\\\":[],\\\"voucherdate\\\":\\\"2019-01-23\\\",\\\"valutadate\\\":\\\"2019-01-23\\\",\\\"vatrated\\\":true,\\\"tenantid\\\":\\\"1\\\",\\\"payment\\\":{},\\\"currency\\\":\\\"EUR\\\",\\\"dispatch\\\":{},\\\"officer\\\":\\\"PKASSANDRA\\\",\\\"fromwarehouse\\\":\\\"1\\\",\\\"towarehouse\\\":\\\"40\\\",\\\"weight\\\":\\\"0.00\\\",\\\"generatepdf\\\":true,\\\"addresses\\\":[{},{},{}],\\\"monetaryamout\\\":[{\\\"type\\\":\\\"lineitems\\\",\\\"amount\\\":120.5}],\\\"lineitems\\\":[{\\\"lineitemno\\\":\\\"1.00\\\",\\\"poscode\\\":\\\"A\\\",\\\"parcelnumbers\\\":[],\\\"references\\\":{\\\"lineitemreference\\\":[],\\\"voucherreference\\\":[],\\\"externalreference\\\":[]},\\\"articleno\\\":\\\"%s\\\",\\\"stockkey\\\":\\\"1\\\",\\\"quantity\\\":1.0,\\\"delivered\\\":1.0,\\\"backlog\\\":0.0,\\\"pfactor\\\":1.0,\\\"qfactor\\\":1.0,\\\"sumtype\\\":\\\"7\\\",\\\"purchaseprice\\\":{\\\"averageprice\\\":0.0,\\\"ratedprice\\\":120.5,\\\"balanceprice\\\":0.0},\\\"retailprice\\\":{\\\"retailprice\\\":120.5,\\\"discount1\\\":0.0,\\\"discount2\\\":0.0},\\\"vat\\\":[{\\\"rate\\\":0.0}],\\\"kto\\\":\\\"3400\\\",\\\"descriptions\\\":[{\\\"lang\\\":\\\"DEU\\\",\\\"pos\\\":\\\"1\\\",\\\"value\\\":\\\"Samsung Galaxy Tab E 9.6 Zoll, Wi-Fi,\\\"},{\\\"lang\\\":\\\"DEU\\\",\\\"pos\\\":\\\"2\\\",\\\"value\\\":\\\"white, 1,3 GHz QC, 1,5GB RAM, 8GB ROM\\\"}],\\\"serials\\\":[{\\\"key\\\":\\\"imei\\\",\\\"value\\\":\\\"%s\\\"}]}]}\",\"voucherType\":\"D\",\"eventName\":\"com.brodos.voucher.producer.entity.VoucherEvent\",\"occuredOn\":1584596914304,\"systemId\":\"1\",\"voucherNo\":\"XX-0000028\",\"version\":0,\"createdDttm\":null}";

        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();

        Long eventId = null;        
        String findNextEventId = "SELECT IF(MAX(`id`) + 1 IS NULL, 1,  MAX(`id`) + 1) FROM `voucher_events`";
        List<JSONObject> eventIdResult = JDBCComponent.instance().executeQuery(findNextEventId);
        if (!eventIdResult.isEmpty()) {
            eventId = eventIdResult.get(0).getLong("1");
            eventId += com.brodos.test.Utils.getUniqueId();
        }

        textMessage.setProperty("eventId", Integer.valueOf(String.valueOf(eventId)));
        textMessage.setText(String.format(voucherImportEventBody, articleNumber, serialNumber));

        if (TestNGRunner.instance().isDebugEnabled()) {
            System.out.println("Publishing voucher import event..." + textMessage);
            System.out.println(Thread.currentThread().getId() + ": " + eventId);
            System.out.println(Thread.currentThread().getId() + ": " + articleNumber);
        }

        JMSComponent.instance().getMessageProducer().send(textMessage);
        return eventId;
    }
    
     public static synchronized void publishVoucherRelocationEvent(String articleNumber, Long eventID) throws Exception {
         String voucherRelocationEventBody = "{\"id\":112501,\"eventBody\":\"{\\\"voucherno\\\":\\\"XX-0000028\\\",\\\"vouchertype\\\":\\\"D\\\",\\\"voucherrefno\\\":[],\\\"voucherdate\\\":\\\"2019-01-23\\\",\\\"valutadate\\\":\\\"2019-01-23\\\",\\\"vatrated\\\":true,\\\"tenantid\\\":\\\"1\\\",\\\"payment\\\":{},\\\"currency\\\":\\\"EUR\\\",\\\"dispatch\\\":{},\\\"officer\\\":\\\"PKASSANDRA\\\",\\\"fromwarehouse\\\":\\\"40\\\",\\\"towarehouse\\\":\\\"1\\\",\\\"weight\\\":\\\"0.00\\\",\\\"generatepdf\\\":true,\\\"addresses\\\":[{},{},{}],\\\"monetaryamout\\\":[{\\\"type\\\":\\\"lineitems\\\",\\\"amount\\\":120.5}],\\\"lineitems\\\":[{\\\"lineitemno\\\":\\\"1.00\\\",\\\"poscode\\\":\\\"A\\\",\\\"parcelnumbers\\\":[],\\\"references\\\":{\\\"lineitemreference\\\":[],\\\"voucherreference\\\":[],\\\"externalreference\\\":[]},\\\"articleno\\\":\\\"%s\\\",\\\"stockkey\\\":\\\"1\\\",\\\"quantity\\\":1.0,\\\"delivered\\\":1.0,\\\"backlog\\\":0.0,\\\"pfactor\\\":1.0,\\\"qfactor\\\":1.0,\\\"sumtype\\\":\\\"7\\\",\\\"purchaseprice\\\":{\\\"averageprice\\\":0.0,\\\"ratedprice\\\":120.5,\\\"balanceprice\\\":0.0},\\\"retailprice\\\":{\\\"retailprice\\\":120.5,\\\"discount1\\\":0.0,\\\"discount2\\\":0.0},\\\"vat\\\":[{\\\"rate\\\":0.0}],\\\"kto\\\":\\\"3400\\\",\\\"descriptions\\\":[{\\\"lang\\\":\\\"DEU\\\",\\\"pos\\\":\\\"1\\\",\\\"value\\\":\\\"Samsung Galaxy Tab E 9.6 Zoll, Wi-Fi,\\\"},{\\\"lang\\\":\\\"DEU\\\",\\\"pos\\\":\\\"2\\\",\\\"value\\\":\\\"white, 1,3 GHz QC, 1,5GB RAM, 8GB ROM\\\"}],\\\"serials\\\":[{\\\"key\\\":\\\"imei\\\",\\\"value\\\":\\\"testimei%d\\\"}]}]}\",\"voucherType\":\"D\",\"eventName\":\"com.brodos.voucher.producer.entity.VoucherEvent\",\"occuredOn\":1584596914304,\"systemId\":\"1\",\"voucherNo\":\"XX-0000028\",\"version\":0,\"createdDttm\":null}";

         ActiveMQTextMessage textMessage = new ActiveMQTextMessage();

         Long eventId = null;        
         String findNextEventId = "SELECT IF(MAX(`id`) + 1 IS NULL, 1,  MAX(`id`) + 1) FROM `voucher_events`";
         List<JSONObject> eventIdResult = JDBCComponent.instance().executeQuery(findNextEventId);
         if (!eventIdResult.isEmpty()) {
             eventId = eventIdResult.get(0).getLong("1");
             eventId += com.brodos.test.Utils.getUniqueId();
         }

         textMessage.setProperty("eventId", Integer.valueOf(String.valueOf(eventId)));
         textMessage.setText(String.format(voucherRelocationEventBody, articleNumber, eventID));

         if (TestNGRunner.instance().isDebugEnabled()) {
             System.out.println("Publishing voucher import event..." + textMessage);
             System.out.println(Thread.currentThread().getId() + ": " + eventId);
             System.out.println(Thread.currentThread().getId() + ": " + articleNumber);
         }
         
         JMSComponent.instance().getMessageProducer().send(textMessage);
    //   JDBCComponent.instance().executeQuery(String.format("SELECT * FROM `serial_number` WHERE NUMBER = \"testimei%d\" AND reservable = FALSE AND is_archived = FALSE", eventId) , 30, 1000);
     }
     
     public static synchronized void publishVoucherRelocationEventUsingIMEI(String articleNumber, String imei) throws Exception {
         String voucherRelocationEventBody = "{\"id\":112501,\"eventBody\":\"{\\\"voucherno\\\":\\\"XX-0000028\\\",\\\"vouchertype\\\":\\\"D\\\",\\\"voucherrefno\\\":[],\\\"voucherdate\\\":\\\"2019-01-23\\\",\\\"valutadate\\\":\\\"2019-01-23\\\",\\\"vatrated\\\":true,\\\"tenantid\\\":\\\"1\\\",\\\"payment\\\":{},\\\"currency\\\":\\\"EUR\\\",\\\"dispatch\\\":{},\\\"officer\\\":\\\"PKASSANDRA\\\",\\\"towarehouse\\\":\\\"1\\\",\\\"weight\\\":\\\"0.00\\\",\\\"generatepdf\\\":true,\\\"addresses\\\":[{},{},{}],\\\"monetaryamout\\\":[{\\\"type\\\":\\\"lineitems\\\",\\\"amount\\\":120.5}],\\\"lineitems\\\":[{\\\"lineitemno\\\":\\\"1.00\\\",\\\"poscode\\\":\\\"A\\\",\\\"parcelnumbers\\\":[],\\\"references\\\":{\\\"lineitemreference\\\":[],\\\"voucherreference\\\":[],\\\"externalreference\\\":[]},\\\"articleno\\\":\\\"%s\\\",\\\"stockkey\\\":\\\"1\\\",\\\"quantity\\\":1.0,\\\"delivered\\\":1.0,\\\"backlog\\\":0.0,\\\"pfactor\\\":1.0,\\\"qfactor\\\":1.0,\\\"sumtype\\\":\\\"7\\\",\\\"purchaseprice\\\":{\\\"averageprice\\\":0.0,\\\"ratedprice\\\":120.5,\\\"balanceprice\\\":0.0},\\\"retailprice\\\":{\\\"retailprice\\\":120.5,\\\"discount1\\\":0.0,\\\"discount2\\\":0.0},\\\"vat\\\":[{\\\"rate\\\":0.0}],\\\"kto\\\":\\\"3400\\\",\\\"descriptions\\\":[{\\\"lang\\\":\\\"DEU\\\",\\\"pos\\\":\\\"1\\\",\\\"value\\\":\\\"Samsung Galaxy Tab E 9.6 Zoll, Wi-Fi,\\\"},{\\\"lang\\\":\\\"DEU\\\",\\\"pos\\\":\\\"2\\\",\\\"value\\\":\\\"white, 1,3 GHz QC, 1,5GB RAM, 8GB ROM\\\"}],\\\"serials\\\":[{\\\"key\\\":\\\"imei\\\",\\\"value\\\":\\\"%s\\\"}]}]}\",\"voucherType\":\"D\",\"eventName\":\"com.brodos.voucher.producer.entity.VoucherEvent\",\"occuredOn\":1584596914304,\"systemId\":\"1\",\"voucherNo\":\"XX-0000028\",\"version\":0, \"createdDttm\":null}";

         ActiveMQTextMessage textMessage = new ActiveMQTextMessage();

         Long eventId = null;        
         String findNextEventId = "SELECT IF(MAX(`id`) + 1 IS NULL, 1,  MAX(`id`) + 1) FROM `voucher_events`";
         List<JSONObject> eventIdResult = JDBCComponent.instance().executeQuery(findNextEventId);
         if (!eventIdResult.isEmpty()) {
             eventId = eventIdResult.get(0).getLong("1");
             eventId += com.brodos.test.Utils.getUniqueId();
         }

         textMessage.setProperty("eventId", Integer.valueOf(String.valueOf(eventId)));
         textMessage.setText(String.format(voucherRelocationEventBody, articleNumber, imei));

         if (TestNGRunner.instance().isDebugEnabled()) {
             System.out.println("Publishing voucher import event..." + textMessage);
             System.out.println(Thread.currentThread().getId() + ": " + eventId);
             System.out.println(Thread.currentThread().getId() + ": " + articleNumber);
         }
         
         JMSComponent.instance().getMessageProducer().send(textMessage);
    //   JDBCComponent.instance().executeQuery(String.format("SELECT * FROM `serial_number` WHERE NUMBER = \"testimei%d\" AND reservable = FALSE AND is_archived = FALSE", eventId) , 30, 1000);
     }
     
    public static synchronized String createNewDeviceConfiguration (String articleNumber) {
    	
    	String deviceID = null ;
    	
		return deviceID;
    	
    }
    
    public static synchronized String findArticleNumberNotHavingAnyOpenCase () throws Exception {
		String articleNumber = null;
		String findArticleNumberNotHavingAnyOpenCase = "SELECT `article_number` FROM `article` WHERE `is_serial` = TRUE AND `article_number` NOT IN (SELECT `article_number` FROM `ticket_reference` WHERE `DTYPE` = 'SerialNumberImportTicketRef' AND (`status` = 'PENDING' OR `status` = 'OPEN')) AND `article_number` IN (SELECT `article_number` FROM `serial_number` WHERE `reservable` = FALSE) LIMIT 1";
        List<JSONObject> articleNoResult = JDBCComponent.instance().executeQuery(findArticleNumberNotHavingAnyOpenCase);
        if (!articleNoResult.isEmpty()) {
            articleNumber = articleNoResult.get(0).getString("1");
        }
        
        return articleNumber ;
       
	}
}
