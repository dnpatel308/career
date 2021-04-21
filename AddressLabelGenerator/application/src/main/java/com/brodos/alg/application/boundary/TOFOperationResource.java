package com.brodos.alg.application.boundary;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import com.brodos.alg.application.dto.TofRouteFigureKeyDTO;

@Path("/tofoperation")
public interface TOFOperationResource {

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    TofRouteFigureKeyDTO uploadTofRouteFigureKeys(MultipartBody fileInputStream) throws IOException;
}
