/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.assembler;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brodos.reservation.dto.response.ArticleDTO;
import com.brodos.reservation.entity.Article;
import com.brodos.reservation.entity.ArticleId;
import com.brodos.reservation.entity.EAN;
import com.brodos.reservation.entity.TenantId;

/**
 *
 * @author padhaval
 */
public class ArticleAssembler {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleAssembler.class);

    public Article toArticle(ArticleDTO articleDTO, long tenantId) {
        Article article =
            new Article(new ArticleId(articleDTO.getArticleNumber(), new TenantId(tenantId)), articleDTO.isIsSerial());
        article.setProducer(articleDTO.getProducerName());
        article.setArticleName(articleDTO.getArticleName());
        article.setSerial(articleDTO.isIsSerial());
        article.setBlisterType(articleDTO.getBlisterType());
        article.setOriginalEan(articleDTO.getOriginalEAN());
        article.setArticleDesc(articleDTO.getArticleDesc());
        article.setNeedToSync(false);
        Set<EAN> articleEAN = new HashSet<>();
        articleEAN.add(new EAN(articleDTO.getEan()));
        article.setArticleEAN(articleEAN);
        LOG.info("Article to sync from BNet to stock={}", article);
        return article;
    }
}
