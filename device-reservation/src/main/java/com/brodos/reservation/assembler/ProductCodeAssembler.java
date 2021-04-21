/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.assembler;

import com.brodos.reservation.dto.response.ArticleDTO;
import com.brodos.reservation.entity.ArticleId;
import com.brodos.reservation.entity.EAN;
import com.brodos.reservation.entity.ProductCode;
import com.brodos.reservation.entity.TenantId;
import com.brodos.reservation.infrastructure.ArticleRepository;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author padhaval
 */
public class ProductCodeAssembler {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleAssembler articleAssembler;

    @Transactional
    public ProductCode toProductCode(ArticleDTO articleDTO, long tenantId) {
        ProductCode productCode = new EAN(articleDTO.getEan());
        productCode.setArticle(articleRepository.findById(
            new ArticleId(articleDTO.getArticleNumber(), new TenantId(tenantId))).orElse(
            articleAssembler.toArticle(articleDTO, tenantId)));
        return productCode;
    }

    public List<ProductCode> toProductCodes(List<ArticleDTO> articleDTOs, long tenantId) {
        List<ProductCode> productCodes = new ArrayList<>();
        for (ArticleDTO articleDTO : articleDTOs) {
            if (!StringUtils.isBlank(articleDTO.getEan())) {
                productCodes.add(toProductCode(articleDTO, tenantId));
            }
        }

        return productCodes;
    }
}
