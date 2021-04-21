/*
 * Copyright (C) Brodos AG - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package com.brodos.alg.application.boundary.impl;

import com.brodos.alg.application.assembler.AddressLabelAssembler;
import com.brodos.alg.domain.entity.AddressLabel;
import com.brodos.article.domain.service.DomainRegistryService;
import com.brodos.commons.domain.model.HealthCheck;
import com.brodos.commons.domain.model.health.HealthReport;
import com.brodos.commons.jaxrs.resource.HealthCheckResource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.ops4j.pax.cdi.api.OsgiService;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import org.json.JSONObject;

/**
 *
 * @author Alexander Sahler <alexander.sahler at brodos.de>
 */
@Singleton
@Named("healthCheck")
public class HealthCheckImpl implements HealthCheckResource {

    @Inject
    @OsgiService(filter = "(mainHealthCheck=true)")
    HealthCheck healthCheck;

    @Override
    public @Valid
    Response checkHealth() {
        HealthReport report = healthCheck.checkHealth();
        if (report.isSuccessful()) {
            return Response.ok(report).build();
        } else {
            return Response
                    .status(Response.Status.GONE)
                    .entity(report)
                    .build();
        }
    }

    @GET
    @Path("/checkdownload")
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response checkPdfDownloadHealth(@Context Request request) throws UnirestException, IOException  {        
        AddressLabel addressLabel = DomainRegistryService.instance().addressLabelRepository().findByMaxId();            
        
        String requestURLBasePath = AddressLabelAssembler.getRequestURLBasePath(request);
        requestURLBasePath = requestURLBasePath.replace("/health/checkdownload", "/addresslabel/" + addressLabel.getId() + ".pdf");                
        long start = System.currentTimeMillis();
        GetRequest getRequest = Unirest.get(requestURLBasePath);        
        int downloadLength = getRequest.asBinary().getBody().available();
        long end = System.currentTimeMillis();
        
        JSONObject jsono = new JSONObject();
        jsono.put("serviceName", "addresslabelgenerator-download-service");
        jsono.put("timestamp", System.currentTimeMillis());
        jsono.put("latestDocumentId", addressLabel.getId());
        jsono.put("checkedDownloadRequest", requestURLBasePath);        
        jsono.put("downloadedDocumentLength (In bytes)", downloadLength);
        jsono.put("documentDownloadTime (In ms)", end - start);
        return Response.ok(jsono.toString()).build();
    }
}
