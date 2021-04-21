/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.infrastructure;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/**
 *
 * @author padhaval
 */
@Repository
public class HealthCheckRepository {

    @PersistenceContext
    EntityManager entityManager;

    public boolean checkDbHealth() {
        return entityManager.isOpen();
    }
}
