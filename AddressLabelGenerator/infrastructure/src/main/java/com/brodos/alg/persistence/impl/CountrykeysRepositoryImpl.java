/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.persistence.impl;

import com.brodos.alg.domain.entity.Countrykeys;
import com.brodos.article.domain.service.CountrykeysRepository;
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
@OsgiServiceProvider(classes = {CountrykeysRepository.class})
@Transactional
@Singleton
public class CountrykeysRepositoryImpl implements CountrykeysRepository {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(CountrykeysRepositoryImpl.class);
    
    @PersistenceContext(unitName = "addressLabelGenerator")
    private EntityManager entityManager;

    @Override
    public Countrykeys findCountrykeyByIsocodeAlpha2(String isocodeAlpha2) {
        try {
            return entityManager.createNamedQuery("Countrykeys.findByIsocodeAlpha2", Countrykeys.class)
                    .setParameter("isocodeAlpha2", isocodeAlpha2)
                    .getSingleResult();
        } catch (Exception e) {
            LOG.trace(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Countrykeys findCountrykeyByIsocodeAlpha3(String isocodeAlpha3) {
         try {
            return entityManager.createNamedQuery("Countrykeys.findByIsocodeAlpha3", Countrykeys.class)
                    .setParameter("isocodeAlpha3", isocodeAlpha3)
                    .getSingleResult();
        } catch (Exception e) {
            LOG.trace(e.getMessage(), e);
            return null;
        }
    }
}
