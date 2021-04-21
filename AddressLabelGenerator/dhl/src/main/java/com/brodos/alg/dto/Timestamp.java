/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author padhaval
 */
//@XmlRootElement(name = "timestamp")
public class Timestamp {

    private String timestamp;
    private Long timestampLong;

    public String getTimestamp() {
        return timestamp;
    }

    @XmlValue
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimestampLong() {
        return timestampLong;
    }

    @XmlAttribute(name = "long")
    public void setTimestampLong(Long timestampLong) {
        this.timestampLong = timestampLong;
    }

    @Override
    public String toString() {
        return "Timestamp{" + "timestamp=" + timestamp + ", timestampLong=" + timestampLong + '}';
    }        
}
