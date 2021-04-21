package com.brodos.devicecontext.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brodos.devicecontext.model.entity.DeviceFieldConfig;

@Repository
public interface DeviceFieldConfigRepository extends JpaRepository<DeviceFieldConfig, Integer> {

}
