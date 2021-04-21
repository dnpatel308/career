/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.persistence.impl;

import com.brodos.alg.domain.entity.AbstractSequenceType;
import com.brodos.alg.domain.entity.IntegerSequenceFormatter;
import com.brodos.alg.domain.entity.SequenceFormatter;
import com.brodos.article.domain.persistence.SequenceFormatterRepository;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
@OsgiServiceProvider(classes = {SequenceFormatterRepository.class})
@Transactional
@Singleton
public class SequenceFormatterRepositoryImpl implements SequenceFormatterRepository {

    private static final Logger LOG = LoggerFactory.getLogger(SequenceFormatterRepositoryImpl.class);

    private static final String GET_FORMATTER_FOR_TYPE
            = "SELECT i FROM IntegerSequenceFormatter i WHERE i.sequenceTypeCol =:sequencetype";
    
    private static final String GET_FORMATTER_BY_TYPE_AND_CUSTOMER_NUMBER
            = "SELECT i FROM IntegerSequenceFormatter i WHERE i.sequenceTypeCol =:sequencetype AND i.customerNumber =:customerNumber";

    @PersistenceContext(unitName = "addressLabelGenerator")
    private EntityManager entityManager;

    @Override
    public SequenceFormatter store(SequenceFormatter sequenceFormatter) {
        entityManager.persist(sequenceFormatter);
        return sequenceFormatter;
    }

    @Override
    public SequenceFormatter merge(SequenceFormatter sequenceFormatter) {
        sequenceFormatter = entityManager.merge(sequenceFormatter);
        return sequenceFormatter;
    }

    @Override
    public IntegerSequenceFormatter getFormatterForType(AbstractSequenceType sequenceType) {
        String formatterString = sequenceType.toString();
        try {
            return entityManager.createQuery(GET_FORMATTER_FOR_TYPE, IntegerSequenceFormatter.class)
                    .setParameter("sequencetype", formatterString).getSingleResult();
        } catch (Exception e) {
            LOG.trace(e.getMessage(), e);
            return null;
        }
    }

    @Override
    synchronized public IntegerSequenceFormatter getIncrementalFormatterForType(AbstractSequenceType sequenceType) {
        String formatterString = sequenceType.toString();
        try {
            IntegerSequenceFormatter sequenceFormatter = entityManager.createQuery(GET_FORMATTER_FOR_TYPE, IntegerSequenceFormatter.class)
                    .setParameter("sequencetype", formatterString).getSingleResult();
            sequenceFormatter.setIncrementalValue(sequenceFormatter.getIncrementalValue() + 1);

            if (sequenceFormatter.getMaxValue() != null 
                    && sequenceFormatter.getIncrementalValue() > sequenceFormatter.getMaxValue()) {
                sequenceFormatter.setIncrementalValue(1L);
            }

            sequenceFormatter = (IntegerSequenceFormatter) merge(sequenceFormatter);
            return sequenceFormatter;
        } catch (Exception e) {
            LOG.trace(e.getMessage(), e);
            return null;
        }
    }

    @Override
    synchronized public IntegerSequenceFormatter getIncrementalFormatterForType(String sequenceFor) {
        try {
            IntegerSequenceFormatter sequenceFormatter = entityManager.createQuery(GET_FORMATTER_FOR_TYPE, IntegerSequenceFormatter.class)
                    .setParameter("sequencetype", sequenceFor).getSingleResult();
            sequenceFormatter.setIncrementalValue(sequenceFormatter.getIncrementalValue() + 1);

            if (sequenceFormatter.getMaxValue() != null 
                    && sequenceFormatter.getIncrementalValue() > sequenceFormatter.getMaxValue()) {
                sequenceFormatter.setIncrementalValue(1L);
            }

            sequenceFormatter = (IntegerSequenceFormatter) merge(sequenceFormatter);
            return sequenceFormatter;
        } catch (Exception e) {
            LOG.trace(e.getMessage(), e);
            return null;
        }
    }                 
}
