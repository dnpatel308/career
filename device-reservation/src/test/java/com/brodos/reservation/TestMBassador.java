/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation;

import com.brodos.reservation.entity.DeviceReservationDomainevents;
import com.brodos.reservation.events.Opened;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.PublicationError;
import net.engio.mbassy.listener.Filter;
import net.engio.mbassy.listener.Handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author padhaval
 */
public class TestMBassador {

    private String messageString;

    @Test
    public void publishTestEvent() throws InterruptedException {
        IBusConfiguration config = new BusConfiguration()
                .addFeature(Feature.SyncPubSub.Default())
                .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                .addFeature(Feature.AsynchronousMessageDispatch.Default())
                .addPublicationErrorHandler((PublicationError error) -> {
                    System.out.println(error.getMessage());
                });

        MBassador mBassador = new MBassador(config);

        mBassador.subscribe(this);
        mBassador.post("TestString").now();

        assertNotNull(messageString);
        assertEquals("TestString", messageString);
    }

    @Handler
    public void handle(String string) {
        this.messageString = string;
        System.out.println(string);
    }
}
