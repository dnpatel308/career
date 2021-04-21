/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.infrastructure;

import com.brodos.reservation.Constants;
import com.brodos.reservation.entity.EAN;
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
public interface EANRepository extends JpaRepository<EAN, Long> {

    @Query("Select e from EAN e where e.article.articleId.articleNumber = :#{#articleNumber} and e.article.articleId.tenantId.tenant = "
        + Constants.TENANT_ID)
    List<EAN>
        findByArticleNumber(@Param("articleNumber") String articleNumber);

    @Query("Select e from EAN e where e.code =:#{#code} AND e.article.articleId.articleNumber = :#{#articleNumber} and e.article.articleId.tenantId.tenant = "
        + Constants.TENANT_ID)
    EAN
        findByCodeAndArticleNumber(@Param("code") String code, @Param("articleNumber") String articleNumber);
}
