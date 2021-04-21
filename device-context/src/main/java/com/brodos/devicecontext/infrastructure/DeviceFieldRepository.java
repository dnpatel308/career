/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.infrastructure;

import com.brodos.devicecontext.model.entity.DeviceField;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author padhaval
 */
@Repository
public interface DeviceFieldRepository extends JpaRepository<DeviceField, String> {

    List<DeviceField> findByValueIn(List<String> values);

    List<DeviceField> findByValue(String value);
}
