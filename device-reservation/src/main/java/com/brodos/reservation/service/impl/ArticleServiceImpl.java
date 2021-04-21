package com.brodos.reservation.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.adapter.ArticleAPIAdapter;
import com.brodos.reservation.assembler.ArticleAssembler;
import com.brodos.reservation.assembler.ProductCodeAssembler;
import com.brodos.reservation.dto.response.ArticleDTO;
import com.brodos.reservation.entity.Article;
import com.brodos.reservation.entity.EAN;
import com.brodos.reservation.entity.ProductCode;
import com.brodos.reservation.exception.DeviceReservationException;
import com.brodos.reservation.infrastructure.ArticleRepository;
import com.brodos.reservation.infrastructure.ProductCodeRepository;
import com.brodos.reservation.service.ArticleService;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleAPIAdapter articleAPIAdapter;

    @Autowired
    ArticleAssembler articleAssembler;

    @Autowired
    ProductCodeAssembler productCodeAssembler;

    @Autowired
    ProductCodeRepository productCodeRepository;

    @Override
    public List<ProductCode> syncBrodosSerialArticlesByEAN(String ean, Long tenant, String languageKey) {
        List<ArticleDTO> articleDTOs = articleAPIAdapter.callArticleAPI(ean, null, tenant, languageKey);

        if (articleDTOs == null || articleDTOs.isEmpty()) {
            throw new DeviceReservationException(ErrorCodes.ARTICLE_NOT_FOUND);
        }

        List<ArticleDTO> serailArticleDTOs =
            articleDTOs.stream().filter(ArticleDTO::isIsSerial).collect(Collectors.toList());
        if (serailArticleDTOs.isEmpty()) {
            throw new DeviceReservationException(ErrorCodes.ARTICLE_NOT_SUPPORT_ISSERIAL);
        }

        List<ProductCode> productCodes = productCodeAssembler.toProductCodes(articleDTOs, tenant);
        for (ProductCode productCode : productCodes) {
            ProductCode existingProductCode = productCodeRepository.findByArticleNumberAndCode(
                productCode.getArticle().getArticleId().getArticleNumber(), productCode.getCode());
            if (existingProductCode == null) {
                productCode = productCodeRepository.save(productCode);
            } else {
                productCode = existingProductCode;
            }
        }

        return productCodes;
    }

    @Override
    public Article syncBrodosArticlesByArticleNo(String articleNo, Long tenantId, String languageKey) {
        List<ArticleDTO> articleDTOs = articleAPIAdapter.callArticleAPI(null, articleNo, tenantId, languageKey);
        if (articleDTOs == null || articleDTOs.isEmpty()) {
            throw new DeviceReservationException(ErrorCodes.ARTICLE_NOT_FOUND);
        }
        ArticleDTO articleDTO = articleDTOs.get(0);
        if (!articleDTO.isIsSerial()) {
            throw new DeviceReservationException(ErrorCodes.ARTICLE_NOT_SUPPORT_ISSERIAL);
        }
        Article article = articleAssembler.toArticle(articleDTO, tenantId);
        Set<EAN> articleEAN = article.getArticleEAN();
        article = articleRepository.save(article);
        article.setArticleEAN(articleEAN);
        return article;
    }
}
