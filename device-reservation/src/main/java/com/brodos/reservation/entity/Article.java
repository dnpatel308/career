package com.brodos.reservation.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "article")
public class Article implements Serializable {

    private static final long serialVersionUID = -2321974928682762214L;

    @EmbeddedId
    ArticleId articleId;

    @Column(name = "is_serial")
    private boolean isSerial;

    @Column(name = "article_name")
    private String articleName;

    @Column(name = "blister_type")
    private String blisterType;

    @Column(name = "original_ean")
    private String originalEan;

    @Column(name = "need_to_sync")
    private boolean needToSync;

    @Column(name = "description")
    private String articleDesc;

    @Transient
    private SerialNumber serialNumber;

    @OneToMany(mappedBy = "article")
    private Set<EAN> articleEAN;

    private String producer;

    public Article() {
        // constructor required by JPA
    }

    public Article(ArticleId articleId, boolean isSerial) {
        super();
        this.articleId = articleId;
        this.isSerial = isSerial;
    }

    public SerialNumber getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(SerialNumber serialNumber) {
        this.serialNumber = serialNumber;
    }

    public boolean isSerial() {
        return isSerial;
    }

    public void setSerial(boolean isSerial) {
        this.isSerial = isSerial;
    }

    public ArticleId getArticleId() {
        return articleId;
    }

    public void setArticleId(ArticleId articleId) {
        this.articleId = articleId;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Set<EAN> getArticleEAN() {
        if (articleEAN == null) {
            articleEAN = new HashSet<>();
        }
        return articleEAN;
    }

    public void setArticleEAN(Set<EAN> articleEAN) {
        this.articleEAN = articleEAN;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public boolean isIsSerial() {
        return isSerial;
    }

    public void setIsSerial(boolean isSerial) {
        this.isSerial = isSerial;
    }

    public String getBlisterType() {
        return blisterType;
    }

    public void setBlisterType(String blisterType) {
        this.blisterType = blisterType;
    }

    public String getOriginalEan() {
        return originalEan;
    }

    public void setOriginalEan(String originalEan) {
        this.originalEan = originalEan;
    }

    public boolean isNeedToSync() {
        return needToSync;
    }

    public void setNeedToSync(boolean needToSync) {
        this.needToSync = needToSync;
    }

    public String getArticleDesc() {
        return articleDesc;
    }

    public void setArticleDesc(String articleDesc) {
        this.articleDesc = articleDesc;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
