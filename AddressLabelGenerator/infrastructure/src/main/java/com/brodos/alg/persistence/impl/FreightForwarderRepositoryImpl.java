/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.persistence.impl;

import com.brodos.alg.domain.entity.FreightForwarder;
import com.brodos.article.domain.persistence.FreightForwarderRepository;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 *
 * @author padhaval
 */
@OsgiServiceProvider(classes = {FreightForwarderRepository.class})
@Transactional
@Singleton
public class FreightForwarderRepositoryImpl implements FreightForwarderRepository {

    @PersistenceContext(unitName = "addressLabelGenerator")
    private EntityManager entityManager;

    @Override
    public FreightForwarder store(FreightForwarder freightForwarder) {
        entityManager.persist(freightForwarder);
        return freightForwarder;
    }

    @Override
    public FreightForwarder merge(FreightForwarder freightForwarder) {
        freightForwarder = entityManager.merge(freightForwarder);
        return freightForwarder;
    }

    @Override
    public FreightForwarder findByPk(String key) {
        return entityManager.find(FreightForwarder.class, key);
    }
}
