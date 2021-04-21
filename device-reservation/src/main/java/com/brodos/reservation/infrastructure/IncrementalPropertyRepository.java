/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import com.brodos.reservation.entity.IncrementalProperty;
import org.springframework.stereotype.Repository;

/**
 *
 * @author padhaval
 */
@Repository
public interface IncrementalPropertyRepository extends JpaRepository<IncrementalProperty, Long> {

    IncrementalProperty findByNameAndTenantId(String name, Long tenantId);
}
