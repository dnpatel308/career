/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 *
 * @author padhaval
 */
@JsonPropertyOrder({"amountInLowestDenomination", "currency"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FreightDTO {

    private Integer amountInLowestDenomination;
    private String currency;

    public FreightDTO() {
        
    }        

    public Integer getAmountInLowestDenomination() {
        return amountInLowestDenomination;
    }

    public void setAmountInLowestDenomination(Integer amountInLowestDenomination) {
        this.amountInLowestDenomination = amountInLowestDenomination;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "FreightDTO{" + "amountInLowestDenomination=" + amountInLowestDenomination + ", currency=" + currency + '}';
    }        
}
