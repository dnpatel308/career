/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.resource.dummy;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

// This is dummy resource for device context api
/**
 *
 * @author padhaval
 */
@RestController
@RequestMapping("/devices")
public class DeviceContextDummyResource {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceContextDummyResource.class);

    @GetMapping("")
    @ResponseBody
    public String getByArticelNoAndImei1(@RequestParam(required = true) String dummyparam, @RequestParam(
        required = true) String articlenumber, @RequestParam(name = "fields.imei1", required = true) String imei1) {
        Random r = new Random();
        int low = 1;
        int high = 10;
        int result = r.nextInt(high - low) + low;
        String status = "incomplete";
        if (result > 7) {
            status = "complete";
        }

        status = "complete";

        LOG.info("Returning: {}, for articlenumber= {} and imei1 = {}", status, articlenumber, imei1);

        return "{ \"_embedded\": { \"devices\": [{ \"device\": { \"id\": \"DEV-15\", \"articlenumber\": \""
            + articlenumber
            + "\", \"status\": \""
            + status
            + "\", \"fields\": [{ \"name\": \"imei1\", \"type\": \"imei\", \"value\": \""
            + imei1
            + "\" }, { \"name\": \"imei2\", \"type\": \"imei\", \"value\": \"893671927462838\" }, { \"name\": \"serial\", \"type\": \"string(40)\", \"value\": \"F89AC39288PJC22\" } ] }, \"_links\": { \"self\": { \"href\": \"https://devices.brodos.net/v1/devices/15\" } } }] }, \"_links\": { \"next\": { \"href\": \"https://devices.brodos.net/v1/devices?q=...&page=2&size=2\" } }, \"page\": { \"size\": 2, \"totalelements\": 4, \"totalpages\": 2, \"number\": 1 } }";
    }
}
