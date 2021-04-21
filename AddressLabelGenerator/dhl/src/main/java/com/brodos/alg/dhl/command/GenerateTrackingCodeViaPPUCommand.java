/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dhl.command;

import com.brodos.alg.domain.exception.ALGException;
import com.brodos.alg.dhl.adapter.LoginAdapter;
import com.brodos.alg.dhl.adapter.PORDeliveryAdapter;
import com.brodos.alg.dhl.adapter.PORReservationAdapter;
import com.brodos.alg.dto.ContentReservation;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import java.util.List;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
public class GenerateTrackingCodeViaPPUCommand extends HystrixCommand<String> {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(GenerateTrackingCodeViaPPUCommand.class);

    private final LoginAdapter loginAdapter;
    private final PORReservationAdapter reservationAdapter;
    private final PORDeliveryAdapter deliveryAdapter;
    private final String barcode;

    public GenerateTrackingCodeViaPPUCommand(String ppu, String trackingCodeAPILoginUrl, String trackingCodeAPIUrl, String userName, String password, String barcode) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("TrackingCodeGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutEnabled(true)
                        .withExecutionTimeoutInMilliseconds(45000)
                        .withFallbackEnabled(false))
                .andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withMaxQueueSize(100)
                                .withQueueSizeRejectionThreshold(100)
                                .withCoreSize(10)));
        try {
            this.loginAdapter = new LoginAdapter(trackingCodeAPILoginUrl, userName, password);
            this.reservationAdapter = new PORReservationAdapter(ppu, trackingCodeAPIUrl, password);
            this.deliveryAdapter = new PORDeliveryAdapter(ppu, trackingCodeAPIUrl, password);
            this.barcode = barcode;

            LOG.info("ppu={}", ppu);
            LOG.info("loginAdapter={}", loginAdapter.toString());
            LOG.info("reservationAdapter={}", reservationAdapter.toString());
            LOG.info("deliveryAdapter={}", deliveryAdapter.toString());
        } catch (Exception ex) {
            LOG.trace(ex.getMessage(), ex);
            throw new ALGException(10004, "Unable to generate tracking code", ex);
        }
    }

    @Override
    protected String run() throws Exception {
        try {
            JSONObject jsono = loginAdapter.doLogin();
            JSONObject dataObjects = (JSONObject) ((JSONObject) jsono.get("body")).get("dataObjects");

            LOG.debug("bmcno={}", dataObjects.getString("bmcno"));
            LOG.debug("cspid={}", dataObjects.getInt("cspid"));
            LOG.debug("barcode={}", barcode);

            List<ContentReservation> contentReservations = reservationAdapter.doPORReservation(dataObjects.getString("bmcno"),
                    dataObjects.getInt("cspid"),
                    barcode).getContentReservations();
            LOG.debug("contentReservations={}", contentReservations);

            String tan = contentReservations.get(0).getTan();

            return deliveryAdapter.doPORDelivery(dataObjects.getString("bmcno"),
                    dataObjects.getInt("cspid"),
                    tan).getContentDeliveries().get(0).getPin();
        } catch (Exception exception) {
            LOG.trace(exception.getMessage(), exception);
            throw new ALGException(10004, "Unable to generate tracking code", exception);
        }
    }
}
