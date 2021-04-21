/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author padhaval
 */
@XmlRootElement(name = "content-reservation")
public class ContentReservation {

    private String tan;
    private Timestamp timestamp;

    public String getTan() {
        return tan;
    }

    @XmlElement(name = "tan")
    public void setTan(String tan) {
        this.tan = tan;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @XmlElement(name = "timestamp")
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }  

    @Override
    public String toString() {
        return "ContentReservation{" + "tan=" + tan + ", timestamp=" + timestamp + '}';
    }        
}
