/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.persistence.impl;


import com.brodos.alg.domain.entity.DhlTimerangeCodes;
import com.brodos.article.domain.persistence.DHLTimerangeCodesRepository;
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
@OsgiServiceProvider(classes = {DHLTimerangeCodesRepository.class})
@Transactional
@Singleton
public class DHLTimerangeCodesRepositoryImpl implements DHLTimerangeCodesRepository {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(CountrykeysRepositoryImpl.class);

    @PersistenceContext(unitName = "addressLabelGenerator")
    private EntityManager entityManager;

    @Override
    public DhlTimerangeCodes findByPk(String key) {
        return entityManager.find(DhlTimerangeCodes.class, key);
    }
}
