/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.devicecontext.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.brodos.devicecontext.model.entity.Device;

/**
 *
 * @author padhaval
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>, JpaSpecificationExecutor<Device> {

    @Query(
        value = "SELECT d.* FROM device d JOIN device_field df ON df.device_id = d.id WHERE d.articlenumber = ? AND (df.name = 'imei1' OR df.name = 'imei2') AND df.value = ?",
        nativeQuery = true)
    List<Device>
        findDeviceByArticleNoAndImei(String articleNo, String imei);

    @Query(
        value = "SELECT d.* FROM device d JOIN device_field df ON df.device_id = d.id WHERE d.articlenumber = ? AND df.name = 'serial' AND df.value = ?",
        nativeQuery = true)
    List<Device>
        findDeviceByArticleNoAndSerial(String articleNo, String serial);
}
