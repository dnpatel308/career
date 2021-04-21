package com.brodos.reservation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brodos.reservation.entity.Owner;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    @Modifying
    @Query(
        value = "UPDATE `owner` o, (SELECT COUNT(*) AS reservations_count FROM serial_number sn, serial_number_reservation snr  WHERE sn.id = snr.serial_number_id AND snr.owner_id IS NOT NULL AND snr.owner_id = :#{#ownerId}  AND (snr.`status` = 'RESERVED' OR snr.`status` = 'REQUESTFORSENTOUT')  AND (snr.is_archived IS NULL OR snr.is_archived != 1)  AND sn.warehouse_no IS NOT NULL AND sn.warehouse_no = 2) t1, (SELECT COUNT(*) AS opencases_count FROM ticket_reference  WHERE owner_id IS NOT NULL AND owner_id = :#{#ownerId}  AND `DTYPE` = 'SerialNumberImportTicketRef' AND `status` = 'OPEN' AND warehouse_no IS NOT NULL AND warehouse_no = 2) t2 SET o.`serial_no_reservation_count` = t1.reservations_count + t2.opencases_count WHERE o.id = :#{#ownerId}",
        nativeQuery = true)
    int
        updateReservationCount(Long ownerId);
}
