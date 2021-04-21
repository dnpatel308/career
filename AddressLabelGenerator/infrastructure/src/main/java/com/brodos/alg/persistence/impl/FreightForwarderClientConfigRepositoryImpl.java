/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.persistence.impl;

import com.brodos.alg.domain.entity.FreightForwarderClientConfig;
import com.brodos.article.domain.persistence.FreightForwarderClientConfigRepository;
import java.util.List;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
@OsgiServiceProvider(classes = {FreightForwarderClientConfigRepository.class})
@Transactional
@Singleton
public class FreightForwarderClientConfigRepositoryImpl implements FreightForwarderClientConfigRepository {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(FreightForwarderClientConfigRepositoryImpl.class);

    @PersistenceContext(unitName = "addressLabelGenerator")
    private EntityManager entityManager;

    @Override
    public List<FreightForwarderClientConfig> findByFreightForwarderAndClient(String freightForwarder, String client) {
        return entityManager.createQuery("SELECT f FROM FreightForwarderClientConfig f WHERE f.freightForwarder =:freightForwarder AND f.client =:client", FreightForwarderClientConfig.class)
                .setParameter("freightForwarder", freightForwarder)
                .setParameter("client", client)
                .getResultList();
    }
}
