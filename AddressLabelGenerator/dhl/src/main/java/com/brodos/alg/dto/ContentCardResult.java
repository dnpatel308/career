/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dto;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author padhaval
 */
@XmlRootElement(name = "contentcard-result", namespace = "http://contentcard.com/schemas/ContentCard/2014/01")
public class ContentCardResult {

    private List<ContentReservation> contentReservations;
    private List<ContentDelivery> contentDeliveries;

    public List<ContentReservation> getContentReservations() {
        return contentReservations;
    }

    @XmlElement(name = "content-reservation")
    public void setContentReservations(List<ContentReservation> contentReservations) {
        this.contentReservations = contentReservations;
    }

    public List<ContentDelivery> getContentDeliveries() {
        return contentDeliveries;
    }

    @XmlElement(name = "content-delivery")
    public void setContentDeliveries(List<ContentDelivery> contentDeliveries) {
        this.contentDeliveries = contentDeliveries;
    }

    @Override
    public String toString() {
        return "ContentCardResult{" + "contentReservations=" + contentReservations + ", contentDeliveries=" + contentDeliveries + '}';
    }                    
}
