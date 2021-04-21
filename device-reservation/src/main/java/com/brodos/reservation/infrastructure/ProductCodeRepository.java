/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.infrastructure;

import com.brodos.reservation.entity.ProductCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author padhaval
 */
@Repository
public interface ProductCodeRepository extends JpaRepository<ProductCode, Long> {

    @Query("Select pc from ProductCode pc where pc.article.articleId.articleNumber = :#{#articleNumber} and pc.code = :#{#code}")
    ProductCode
        findByArticleNumberAndCode(@Param("articleNumber") String articleNumber, @Param("code") String code);
}
