/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.infrastructure;

import com.brodos.reservation.entity.DeviceReservationDomainevents;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author padhaval
 */
@Repository
public interface DeviceReservationDomaineventsRepository extends JpaRepository<DeviceReservationDomainevents, Integer> {

    DeviceReservationDomainevents findByReservationId(Long reservationId);

    List<DeviceReservationDomainevents> findByIdGreaterThanAndOccurredOnLessThan(Integer id, Date occurredOn);

    DeviceReservationDomainevents findByReservationIdAndStatus(Long reservationId, String status);
}
