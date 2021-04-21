/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.alg.persistence.impl;

import com.brodos.alg.domain.entity.AddressLabel;
import com.brodos.article.domain.persistence.AddressLabelRepository;
import java.util.List;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
@OsgiServiceProvider(classes = { AddressLabelRepository.class })
@Transactional
@Singleton
public class AddressLabelRepositoryImpl implements AddressLabelRepository {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(AddressLabelRepositoryImpl.class);

    @PersistenceContext(unitName = "addressLabelGenerator")
    private EntityManager entityManager;

    private final String FIND_BY_TRACKING_CODE_AND_FF =
        "select al from AddressLabel al where al.trackingCode =:trackingCode and al.freightForwarder.key =:freightForwarder";

    private final String FIND_BY_TRACKING_CODE =
        "select al from AddressLabel al where al.trackingCode =:trackingCode order by al.createdDateAndTime desc";

    @Override
    public AddressLabel store(AddressLabel addressLabel) {
        entityManager.persist(addressLabel);
        return addressLabel;
    }

    @Override
    public AddressLabel merge(AddressLabel addressLabel) {
        addressLabel = entityManager.merge(addressLabel);
        return addressLabel;
    }

    @Override
    public AddressLabel findByPk(Long id) {
        try {
            return entityManager.find(AddressLabel.class, id);
        } catch (Exception exception) {
            LOG.trace(exception.getMessage(), exception);
            return null;
        }
    }

    @Override
    public AddressLabel findByMaxId() {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            javax.persistence.criteria.CriteriaQuery criteriaQuery = entityManager.getCriteriaBuilder().createQuery();
            Root<AddressLabel> root = criteriaQuery.from(AddressLabel.class);
            criteriaQuery.select(criteriaBuilder.max(root.get("id")));
            Long maxId = (Long) entityManager.createQuery(criteriaQuery).getSingleResult();
            return findByPk(maxId);
        } catch (Exception exception) {
            LOG.trace(exception.getMessage(), exception);
            return null;
        }
    }

    @Override
    public AddressLabel findByTrackingCode(String freightForwarder, String trackingCode) {
        try {
            return entityManager.createQuery(FIND_BY_TRACKING_CODE_AND_FF, AddressLabel.class)
                .setParameter("trackingCode", trackingCode).setParameter("freightForwarder", freightForwarder)
                .getSingleResult();
        } catch (Exception exception) {
            LOG.trace(exception.getMessage(), exception);
            return null;
        }
    }

    @Override
    public List<AddressLabel> findByTrackingCode(String trackingCode) {
        try {
            return entityManager.createQuery(FIND_BY_TRACKING_CODE, AddressLabel.class)
                .setParameter("trackingCode", trackingCode).getResultList();
        } catch (Exception exception) {
            LOG.trace(exception.getMessage(), exception);
            return null;
        }
    }

    @Override
    public AddressLabel checkAddressLabelExistForTrackingCodeOrNot(String freightForwarder, String trackingCode,
        String formatType) {
        AddressLabel addressLabel = findByTrackingCode(freightForwarder, trackingCode);
        if (addressLabel != null) {
            return addressLabel;
        } else {
            return null;
        }
    }
}
