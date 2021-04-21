/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.infrastructure;

import com.brodos.reservation.Constants;
import com.brodos.reservation.entity.SerialNumber;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author padhaval
 */
@Repository
public interface SerialNumberRepository extends JpaRepository<SerialNumber, Long> {

    @Query("SELECT sn FROM SerialNumber sn WHERE sn.article.articleId.articleNumber =:#{#articleNumber} AND sn.reservable =:#{#reservable} AND (sn.warehouseNo IS NULL OR sn.warehouseNo =:#{#warehouseNo}) AND sn.archived = FALSE AND sn.article.articleId.tenantId.tenant = "
        + Constants.TENANT_ID)
    List<SerialNumber>
        findByArticleAndWarehouseNoAndReservable(@Param("articleNumber") String articleNumber,
            @Param("warehouseNo") int warehouseNo, @Param("reservable") boolean reservable);

    @Query("SELECT sn FROM SerialNumber sn WHERE sn.article.articleId.articleNumber =:#{#articleNumber} AND sn.number = :#{#serialNumber} AND sn.article.articleId.tenantId.tenant = "
        + Constants.TENANT_ID)
    SerialNumber
        findByNumberAndArticle(@Param("serialNumber") String serialNumber, @Param("articleNumber") String articleNumber);
}
