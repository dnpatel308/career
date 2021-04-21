/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brodos.reservation.entity.SerialNumberReservation;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author padhaval
 */
@Repository
public interface SerialNumberReservationRepository extends JpaRepository<SerialNumberReservation, Long> {

    @Query("Select snr from SerialNumberReservation snr where snr.serialNumber.number = :#{#serialNumber} and (snr.archived = null or snr.archived = false) and (snr.status = 'RESERVED' or snr.status = 'REQUESTFORSENTOUT') and snr.serialNumber.article.articleId.articleNumber = :#{#articleNumber} and snr.serialNumber.article.articleId.tenantId.tenant = :#{#tenantId}")
    SerialNumberReservation
        findBySerialNumberAndReservedOrRequestForSentout(@Param("serialNumber") String serialNumber,
            @Param("articleNumber") String articleNumber, @Param("tenantId") Long tenantId);

    public List<SerialNumberReservation> findByBulkReservationId(Long bulkReservationId);

    public SerialNumberReservation findByRequestId(String requestId);
}
