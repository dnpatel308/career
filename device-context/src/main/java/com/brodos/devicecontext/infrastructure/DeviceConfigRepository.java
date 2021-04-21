package com.brodos.devicecontext.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.brodos.devicecontext.model.entity.DeviceConfig;

@Repository
public interface DeviceConfigRepository extends JpaRepository<DeviceConfig, Long>,
    JpaSpecificationExecutor<DeviceConfig> {

    DeviceConfig findByArticlenumber(String articlenumber);
}
