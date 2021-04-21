/*
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.brodos.reservation.dto.response;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author Alexander Sahler <alexander.sahler at brodos.de>
 */
public class ArticleDTO implements Serializable {

    private static final long serialVersionUID = 0L;

    private String articleNumber;
    private String ean;
    private boolean isSerial;
    private boolean isActive;
    private String producerName;
    private String articleName;
    private String blisterType;
    private String originalEAN;
    private String articleCodeType;
    private String articleDesc;

    public ArticleDTO() {
    }

    public ArticleDTO(String articleNumber, String ean, boolean isSerial, boolean isActive, String producerName) {
        this.articleNumber = articleNumber;
        this.ean = ean;
        this.isSerial = isSerial;
        this.isActive = isActive;
        this.producerName = producerName;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public boolean isIsSerial() {
        return isSerial;
    }

    public void setIsSerial(boolean isSerial) {
        this.isSerial = isSerial;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getBlisterType() {
        return blisterType;
    }

    public void setBlisterType(String blisterType) {
        this.blisterType = blisterType;
    }

    public String getOriginalEAN() {
        return originalEAN;
    }

    public void setOriginalEAN(String originalEAN) {
        this.originalEAN = originalEAN;
    }

    public String getArticleCodeType() {
        return articleCodeType;
    }

    public void setArticleCodeType(String articleCodeType) {
        this.articleCodeType = articleCodeType;
    }

    public String getArticleDesc() {
        return articleDesc;
    }

    public void setArticleDesc(String articleDesc) {
        this.articleDesc = articleDesc;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
