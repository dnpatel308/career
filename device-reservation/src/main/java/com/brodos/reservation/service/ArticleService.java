package com.brodos.reservation.service;

import com.brodos.reservation.entity.Article;
import com.brodos.reservation.entity.ProductCode;
import java.util.List;

public interface ArticleService {

    public List<ProductCode> syncBrodosSerialArticlesByEAN(String ean, Long tenant, String languageKey);

    public Article syncBrodosArticlesByArticleNo(String articleNo, Long tenantId, String languageKey);
}
