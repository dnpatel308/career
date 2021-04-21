/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.domain.entity;

/**
 *
 * @author padhaval
 */
public enum ALGSequence implements AbstractSequenceType {
   
    TOF, MEDION1;

    @Override
    public String getLabelType() {
        return this.name();
    }
}
