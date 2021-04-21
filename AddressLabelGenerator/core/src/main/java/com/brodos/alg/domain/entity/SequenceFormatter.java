/*
 * Copyright 2016 The Apache Software Foundation.
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
package com.brodos.alg.domain.entity;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author Alexander Sahler <alexander.sahler at brodos.de>
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "sequence_formatter")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "DTYPE")
public abstract class SequenceFormatter<T> implements Serializable {

    private static final long serialVersionUID = 5931689562496119273L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    
    @Column(name = "format_template", nullable = false)
    String formatTemplate;

    @Transient
    AbstractSequenceType sequenceType;

    @Column(name = "sequence_type")
    String sequenceTypeCol;
    
    @Column(name = "max_value")
    Long maxValue;        
    
    public SequenceFormatter() {
    }

    public SequenceFormatter(AbstractSequenceType sequenceType, String formatTemplate) {
        this.formatTemplate = formatTemplate;
        this.sequenceType = sequenceType;        
        this.sequenceTypeCol = sequenceType.toString();
    }

    public abstract String getFormatedIncrementalValue();
    
    public abstract String getFormatedIncrementalValue(Long value);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFormatTemplate() {
        return formatTemplate;
    }

    public void setFormatTemplate(String formatTemplate) {
        this.formatTemplate = formatTemplate;
    }

    public AbstractSequenceType getSequenceType() {
        return sequenceType;
    }

    public void setSequenceType(AbstractSequenceType sequenceType) {
        this.sequenceType = sequenceType;
    }

    public String getSequenceTypeCol() {
        return sequenceTypeCol;
    }

    public void setSequenceTypeCol(String sequenceTypeCol) {
        this.sequenceTypeCol = sequenceTypeCol;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }              

    @Override
    public String toString() {
         return ToStringBuilder.reflectionToString(this);
    }        
}
