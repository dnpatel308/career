/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.camel.route;

import org.apache.camel.builder.RouteBuilder;

/**
 *
 * @author padhaval
 */
public class EmailRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:sendEmail").routeId("sendEmail").autoStartup(true).to("smtp://amadeus.brodos.net?port=25");
    }
}
