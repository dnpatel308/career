/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.dto;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author padhaval
 */
@Deprecated
@XmlRootElement(name = "object-descriptor")
public class ObjectDescriptor {
    
    private Integer id;    
    private String filename;    
    private String mimeType;    
    private String random;
    
    public Integer getId() {
        return id;
    }

    @XmlAttribute
    public void setId(Integer id) {
        this.id = id;
    }

    @XmlAttribute(name = "filename")
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @XmlAttribute(name = "mime-type")
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @XmlAttribute(name = "random")
    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    @Override
    public String toString() {
        return "ObjectDescriptor{" + "id=" + id + ", filename=" + filename + ", mimeType=" + mimeType + ", random=" + random + '}';
    }        
}
