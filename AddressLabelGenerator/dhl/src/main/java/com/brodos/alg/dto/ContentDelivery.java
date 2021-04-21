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
@XmlRootElement(name = "content-delivery")
public class ContentDelivery {

    private Integer serial;
    private Timestamp timestamp;
    private String tan;
    private Timestamp validThru;
    private Integer ppu;
    private String pin;
    private String topupInstruction;

    public Integer getSerial() {
        return serial;
    }

    @XmlElement(name = "serial")
    public void setSerial(Integer serial) {
        this.serial = serial;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @XmlElement(name = "timestamp")
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTan() {
        return tan;
    }

    @XmlElement(name = "tan")
    public void setTan(String tan) {
        this.tan = tan;
    }

    public Timestamp getValidThru() {
        return validThru;
    }

    @XmlElement(name = "valid-thru")
    public void setValidThru(Timestamp validThru) {
        this.validThru = validThru;
    }

    public Integer getPpu() {
        return ppu;
    }

    @XmlElement(name = "ppu")
    public void setPpu(Integer ppu) {
        this.ppu = ppu;
    }

    public String getPin() {
        return pin;
    }

    @XmlElement(name = "pin")
    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getTopupInstruction() {
        return topupInstruction;
    }

    @XmlElement(name = "topup-instruction")
    public void setTopupInstruction(String topupInstruction) {
        this.topupInstruction = topupInstruction;
    }

    @Override
    public String toString() {
        return "ContentDelivery{" + "serial=" + serial + ", timestamp=" + timestamp + ", tan=" + tan + ", validThru=" + validThru + ", ppu=" + ppu + ", pin=" + pin + ", topupInstruction=" + topupInstruction + '}';
    }
}
