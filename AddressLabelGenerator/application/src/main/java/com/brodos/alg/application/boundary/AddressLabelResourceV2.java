/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.application.boundary;

import com.brodos.alg.application.dto.AddressLabelDTO;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

/**
 *
 * @author padhaval
 */
@Path("/v2/addresslabel")
public interface AddressLabelResourceV2 {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestAddressLabelFor(@Context Request request, @Valid AddressLabelDTO addressLabelDTO);

    @GET
    @Path("/{id}.pdf")
    @Produces("application/pdf")
    public Response generateAndDownloadAddressLabelPdf(@PathParam("id") Long id);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAddressLabelInfoById(Request request, @PathParam("id") Long id);

    @GET
    @Path("/{trackingcode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAddressLabelInfoByTrackingCode(Request request, @PathParam("trackingcode") String trackingCode);
}
