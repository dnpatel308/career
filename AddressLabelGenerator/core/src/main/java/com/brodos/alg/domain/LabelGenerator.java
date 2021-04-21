/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.domain;

import com.brodos.alg.domain.entity.AddressLabel;
import com.brodos.alg.domain.exception.ALGException;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author padhaval
 */
public abstract class LabelGenerator {
    
    private final Properties documentLayoutProperties;
    private final List<String> stringKeys;
    private final List<String> integerKeys;
    private final List<String> excludeFieldsFromValidation;

    public LabelGenerator(List<String> stringKeys, List<String> integerKeys, List<String> excludeFieldsFromValidation) {
        this.documentLayoutProperties = new Properties();
        this.stringKeys = stringKeys;
        this.integerKeys = integerKeys;
        this.excludeFieldsFromValidation = excludeFieldsFromValidation;
    }                                   
    
    public abstract void validateRequest(AddressLabel addressLabel);
    
    public abstract AddressLabel generateLabel(AddressLabel addressLabel) throws ALGException;

    public Properties getDocumentLayoutProperties() {
        return documentLayoutProperties;
    }     

    public List<String> getStringKeys() {
        return stringKeys;
    }

    public List<String> getIntegerKeys() {
        return integerKeys;
    }

    public List<String> getExcludeFieldsFromValidation() {
        return excludeFieldsFromValidation;
    }        
}
