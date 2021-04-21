package com.brodos.devicecontext.model.dto.response;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DeviceConfigLinksDTO {
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
