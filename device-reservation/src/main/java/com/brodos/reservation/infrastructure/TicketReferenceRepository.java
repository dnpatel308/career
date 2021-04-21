/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.infrastructure;

import com.brodos.reservation.Constants;
import com.brodos.reservation.entity.SerialNumberImportTicketReference;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.TicketReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author padhaval
 */
@Repository
public interface TicketReferenceRepository extends JpaRepository<TicketReference, Long>,
    JpaSpecificationExecutor<TicketReference> {

    TicketReference findFirstBySerialNumberReservationOrderByIdDesc(SerialNumberReservation serialNumberReservation);

    @Query(
        value = "SELECT * FROM `ticket_reference` tr LEFT JOIN `serial_number_reservation` snr ON tr.`serial_number_res_id` = snr.`id` LEFT JOIN `article` a ON tr.`article_number` = a.`article_number` AND tr.`article_tenant_id` = a.`tenant_id` WHERE a.`article_number` =:#{#articleNumber} AND a.`tenant_id` = "
            + Constants.TENANT_ID
            + " AND (snr.`status` IS NULL  OR snr.`status` = 'PENDING') AND (tr.`status` = 'OPEN'  OR snr.`status` = 'PENDING') AND (tr.`warehouse_no` IS NULL OR tr.`warehouse_no` =:#{#warehouseNo}) AND tr.`DTYPE` = 'SerialNumberImportTicketRef' LIMIT 1",
        nativeQuery = true)
    SerialNumberImportTicketReference
        findFirstPendingDeviceReservation(@Param("articleNumber") String articleNumber,
            @Param("warehouseNo") Integer warehouseNo);
}
