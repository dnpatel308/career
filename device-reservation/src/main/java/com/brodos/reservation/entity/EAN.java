package com.brodos.reservation.entity;

import javax.persistence.Entity;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
public class EAN extends ProductCode {

    private static final long serialVersionUID = 7150568223634582318L;

    public EAN() {
        super();
    }

    public EAN(String code) {
        this();
        this.code = code;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
