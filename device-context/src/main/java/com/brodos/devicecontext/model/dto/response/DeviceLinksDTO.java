/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
@JsonInclude(Include.NON_NULL)
public class DeviceLinksDTO {

    private LinkDTO self;
    private LinkDTO next;

    public LinkDTO getSelf() {
        return self;
    }

    public void setSelf(LinkDTO self) {
        this.self = self;
    }

    public LinkDTO getNext() {
        return next;
    }

    public void setNext(LinkDTO next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
