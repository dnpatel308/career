/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.persistence.impl;

import com.brodos.alg.domain.entity.TofSevicecodes;
import com.brodos.article.domain.persistence.TofSevicecodesRepository;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

/**
 *
 * @author padhaval
 */
@OsgiServiceProvider(classes = {TofSevicecodesRepository.class})
@Transactional
@Singleton
public class TofSevicecodesRepositoryImpl implements TofSevicecodesRepository {

    @PersistenceContext(unitName = "addressLabelGenerator")
    private EntityManager entityManager;

    @Override
    public TofSevicecodes findSevicecodeBySevicecode(Integer serviceCode) {
        return entityManager.find(TofSevicecodes.class, serviceCode);
    }
}
