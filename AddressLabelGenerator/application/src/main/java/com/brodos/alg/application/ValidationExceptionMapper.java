package com.brodos.alg.application;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    com.brodos.commons.domain.resource.entity.Response brodosResponse =
            new com.brodos.commons.domain.resource.entity.Response();

    @Override
    public Response toResponse(ConstraintViolationException ex) {
        ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
        String message = constraintViolation.getMessage();
        com.brodos.commons.domain.resource.entity.Response response = new com.brodos.commons.domain.resource.entity.Response();
        response.setMessage(message);
        response.setCode(Response.Status.BAD_REQUEST.getStatusCode());
        return Response
                .status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }
}
