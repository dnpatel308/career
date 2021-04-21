/*
 * Copyright (c) 2013, Brodos AG. All rights reserved.
 */
package com.brodos.alg.application.filter;

import com.brodos.alg.domain.exception.ALGException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brodos.commons.domain.resource.entity.StatusType;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("rawtypes")
public class ALGExceptionMapper implements ExceptionMapper {
    
    private static final Logger LOG = LoggerFactory.getLogger(ALGExceptionMapper.class);
    
    @Override
    public javax.ws.rs.core.Response toResponse(Throwable exception) {        
        LOG.debug("Exception type={}", exception.getClass());
        LOG.error(exception.getMessage(), exception);
        com.brodos.commons.domain.resource.entity.Response brodosResponse
                = new com.brodos.commons.domain.resource.entity.Response();
        
        Response.Status status = Response.Status.OK;
        if (exception instanceof ALGException) {
            brodosResponse.setCode(((ALGException) exception).getCode());
            brodosResponse.setMessage(exception.getMessage());
            brodosResponse.setStatus(StatusType.FAIL);
            int code = ((ALGException) exception).getCode();
            if ((code >= 10005 && code <= 10006) || code == 10035) {
                status = Response.Status.NOT_FOUND;
            } else {
                status = Response.Status.INTERNAL_SERVER_ERROR;
            }
        } else if (exception instanceof InvalidTypeIdException) {
            brodosResponse.setCode(10005);
            brodosResponse.setMessage("You have entered an unsupported Freight Forwarder");
            brodosResponse.setStatus(StatusType.FAIL);
            status = Response.Status.BAD_REQUEST;
        } else if (exception instanceof InvalidFormatException) {
            brodosResponse.setCode(10013);
            brodosResponse.setMessage(extractJSONErrorMessage((Exception) exception));
            brodosResponse.setStatus(StatusType.FAIL);
            status = Response.Status.BAD_REQUEST;
        } else if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        } else if (exception instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException unrecognizedPropertyException = (UnrecognizedPropertyException) exception;
            brodosResponse.setCode(10029);
            brodosResponse.setMessage(unrecognizedPropertyException.getMessage());
            brodosResponse.setMessage(StringUtils.substringBefore(brodosResponse.getMessage(), "(").trim().replace("\"", "'"));
            brodosResponse.setStatus(StatusType.FAIL);
            status = Response.Status.BAD_REQUEST;
        } else {
            brodosResponse.setCode(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
            brodosResponse.setMessage(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
            brodosResponse.setStatus(StatusType.ERROR);
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }
        
        LOG.debug("Sending Error Response={}", brodosResponse);
        return javax.ws.rs.core.Response.status(status).entity(brodosResponse).type(MediaType.APPLICATION_JSON).build();
    }
    
    private String extractJSONErrorMessage(Exception exception) {
        StringBuilder messageBuilder = new StringBuilder();
        String message = exception.getMessage();
        if (message.contains("through reference chain:")) {
            message = StringUtils.substringAfter(message, "through reference chain:");
            String[] jsonKeys = org.apache.commons.lang3.StringUtils.substringsBetween(message, "[\"", "\"]");
            if (jsonKeys != null && jsonKeys.length > 0) {
                messageBuilder.append("Fields contains an invalid entry: ");
                for (int i = 0; i < jsonKeys.length; i++) {
                    if (i == 0) {
                        messageBuilder.append(jsonKeys[i]);
                    } else {
                        messageBuilder.append(".").append(jsonKeys[i]);
                    }
                }
            } else {
                messageBuilder.append(message);
            }
        }
        
        return messageBuilder.toString();
    }
}
