/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.infrastructure;

import com.brodos.reservation.entity.SerialNumberHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author padhaval
 */
@Repository
public interface SerialNumberHistoryRepository extends JpaRepository<SerialNumberHistory, Long> {

}
