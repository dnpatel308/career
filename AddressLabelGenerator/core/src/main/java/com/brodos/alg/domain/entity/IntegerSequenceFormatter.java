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

import javax.persistence.Column;
import javax.persistence.Entity;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author Alexander Sahler <alexander.sahler at brodos.de>
 */
@Entity
public class IntegerSequenceFormatter extends SequenceFormatter<Long> {

    private static final long serialVersionUID = 1L;

    @Column(name = "incremental_value")
    Long incrementalValue;

    public IntegerSequenceFormatter() {
        // needed for JPA
    }

    public IntegerSequenceFormatter(AbstractSequenceType sequencelType, String formatTemplate) {
        super(sequencelType, formatTemplate);
    }

    @Override
    public String getFormatedIncrementalValue() {
        return String.format(formatTemplate, getIncrementalValue());
    }

    @Override
    public String getFormatedIncrementalValue(Long value) {
        return String.format(formatTemplate, value);
    }

    public Long getIncrementalValue() {
        return incrementalValue;
    }

    public void setIncrementalValue(Long incrementalValue) {
        this.incrementalValue = incrementalValue;
    }

    @Override
    public String toString() {
         return ToStringBuilder.reflectionToString(this);
    }
}
