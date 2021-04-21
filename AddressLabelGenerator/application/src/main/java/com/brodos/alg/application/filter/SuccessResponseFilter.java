package com.brodos.alg.application.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brodos.commons.domain.resource.entity.Response;
import com.brodos.commons.domain.resource.entity.StatusType;

public class SuccessResponseFilter implements ContainerResponseFilter {
    private static final Logger LOG = LoggerFactory.getLogger(SuccessResponseFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (responseContext.hasEntity() && !(responseContext.getEntity() instanceof Response)) {
            Response successResponse = new Response();
            successResponse.setStatus(StatusType.SUCCESS);
            successResponse.setMessage("Success");
            successResponse.setCode(Status.OK.getStatusCode());
            successResponse.setData(responseContext.getEntity());
            responseContext.setEntity(successResponse);
            responseContext.setStatus(Status.OK.getStatusCode());
            LOG.debug("Success response = {}", responseContext);
        } else {
            LOG.debug("Response doesn't have any entity");
        }
    }

}
