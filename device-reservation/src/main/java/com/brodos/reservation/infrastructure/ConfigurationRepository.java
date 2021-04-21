/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.infrastructure;

import com.brodos.reservation.entity.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author padhaval
 */
public interface ConfigurationRepository extends JpaRepository<Configuration, String> {

}
