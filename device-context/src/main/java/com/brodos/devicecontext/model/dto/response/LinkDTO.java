/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkDTO {

    private String href;

    public LinkDTO(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
