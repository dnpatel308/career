/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.article.domain.persistence;

import com.brodos.alg.domain.entity.AbstractSequenceType;
import com.brodos.alg.domain.entity.IntegerSequenceFormatter;
import com.brodos.alg.domain.entity.SequenceFormatter;

/**
 *
 * @author padhaval
 */
public interface SequenceFormatterRepository {

    SequenceFormatter store(SequenceFormatter sequenceFormatter);

    SequenceFormatter merge(SequenceFormatter sequenceFormatter);

    IntegerSequenceFormatter getFormatterForType(AbstractSequenceType formatterType);

    IntegerSequenceFormatter getIncrementalFormatterForType(AbstractSequenceType sequenceType);

    IntegerSequenceFormatter getIncrementalFormatterForType(String sequenceFor);    
}
