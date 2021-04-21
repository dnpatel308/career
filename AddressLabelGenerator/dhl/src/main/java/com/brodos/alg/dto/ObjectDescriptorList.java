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
@Deprecated
@XmlRootElement(name = "object-descriptor-list")
public class ObjectDescriptorList {
    
    private List<ObjectDescriptor> objectDescriptors;
    
    public List<ObjectDescriptor> getObjectDescriptors() {
        return objectDescriptors;
    }

    @XmlElement(name = "object-descriptor")
    public void setObjectDescriptors(List<ObjectDescriptor> objectDescriptors) {
        this.objectDescriptors = objectDescriptors;
    }

    @Override
    public String toString() {
        return "ObjectDescriptorList{" + "objectDescriptors=" + objectDescriptors + '}';
    }
}
