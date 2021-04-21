package com.brodos.reservation.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Embeddable
public class ArticleId implements Serializable {
    private static final long serialVersionUID = -6692752152027291615L;

    @Column(name = "article_number")
    private String articleNumber;

    @Embedded
    private TenantId tenantId;

    public ArticleId() {
    }

    public ArticleId(String articleNumber, TenantId tenantId) {
        super();
        this.articleNumber = articleNumber;
        this.tenantId = tenantId;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
