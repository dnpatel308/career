/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.persistence.impl;

import java.util.List;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brodos.alg.domain.entity.TofRouteFigureKey;
import com.brodos.article.domain.persistence.TofRouteFigureKeyRepository;

/**
 *
 * @author padhaval
 */
@OsgiServiceProvider(classes = { TofRouteFigureKeyRepository.class })
@Transactional
@Singleton
public class TofRouteFigureKeyRepositoryImpl implements TofRouteFigureKeyRepository {

    private static final Logger LOG = LoggerFactory.getLogger(TofRouteFigureKeyRepositoryImpl.class);

    private final String FIND_BY_COUNTRYCALLINGCODE_AND_POSTAL_RANGE =
        "SELECT * FROM `tof_route_figure_key` WHERE `country_calling_code` =:countryCallingCode AND :postalCode BETWEEN `postal_range_from` AND `postal_range_to`";

    private static final String DELETE_ALL_TOF_ROUTEKEY = "DELETE FROM tof_route_figure_key";
    private static final String RESET_AUTO_INCREMENT = "ALTER TABLE tof_route_figure_key AUTO_INCREMENT = 1";

    @PersistenceContext(unitName = "addressLabelGenerator")
    private EntityManager entityManager;

    @Override
    public TofRouteFigureKey findByCountryCallingCodeAndPostalRange(String countryCallingCode, String postalCode) {
        try {
            return (TofRouteFigureKey) entityManager
                .createNativeQuery(FIND_BY_COUNTRYCALLINGCODE_AND_POSTAL_RANGE, TofRouteFigureKey.class)
                .setParameter("countryCallingCode", countryCallingCode)
                .setParameter("postalCode", Integer.parseInt(postalCode)).getSingleResult();
        } catch (Exception e) {
            LOG.trace(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public int deleteAll() {
        Query query = entityManager.createNativeQuery(DELETE_ALL_TOF_ROUTEKEY);
        return query.executeUpdate();
    }

    @Override
    public List<TofRouteFigureKey> saveAll(List<TofRouteFigureKey> tofRouteFigureKeys) {
        entityManager.createNativeQuery(RESET_AUTO_INCREMENT).executeUpdate();
        tofRouteFigureKeys.forEach(tofRouteFigureKey -> entityManager.persist(tofRouteFigureKey));
        return tofRouteFigureKeys;
    }
}
