/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.application.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class DHLDTO extends FreightForwarderDTO {

    private ProductType product;

    public DHLDTO() {
        freightForwarderType = FreightForwarderType.DHL;
    }        

    public ProductType getProduct() {
        return product;
    }

    public void setProduct(ProductType product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
           
}
