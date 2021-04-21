package com.brodos.reservation.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Observable;
import javax.persistence.PostLoad;

@Entity
@Table(name = "serial_number")
public class SerialNumber extends Observable implements Serializable {

    private static final long serialVersionUID = -3229618260230582958L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number")
    private String number;

    @Column(name = "reservable")
    private Boolean reservable;

    @Column(name = "relocation_requested")
    private Boolean relocationRequested;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumns({ @JoinColumn(name = "article_number", referencedColumnName = "article_number"),
            @JoinColumn(name = "article_tenant_id", referencedColumnName = "tenant_id") })
    private Article article;

    @Column(name = "ean")
    private String ean;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "serialNumber")
    @OrderBy("id")
    private Set<SerialNumberReservation> serialNumberReservation;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", insertable = false, updatable = false,
        columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Date createdDate;

    @Column(name = "created_by", nullable = true)
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date", nullable = true)
    private Date modifiedDate;

    @Column(name = "modified_by", nullable = true)
    private String modifiedBy;

    @Column(nullable = false)
    private Boolean relocated = false;

    @Column(name = "is_archived")
    private Boolean archived;

    @Column(name = "ticket_number")
    private String ticketNumber;

    @Column(name = "warehouse_no")
    private Integer warehouseNo;

    @Transient
    private JsonNode _embedded;

    @Transient
    private Integer originalWarehouseNo;

    @PostLoad
    public void postLoad() {
        originalWarehouseNo = warehouseNo;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setId(Long id) {
        setChanged();
        this.id = id;
    }

    public Boolean getReservable() {
        return reservable;
    }

    public void setReservable(Boolean reservable) {
        setChanged();
        this.reservable = reservable;
    }

    public Boolean getRelocationRequested() {
        return relocationRequested;
    }

    public void setRelocationRequested(Boolean relocationRequested) {
        setChanged();
        this.relocationRequested = relocationRequested;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        setChanged();
        this.article = article;
    }

    public Set<SerialNumberReservation> getSerialNumberReservation() {
        return serialNumberReservation;
    }

    public void setSerialNumberReservation(Set<SerialNumberReservation> serialNumberReservation) {
        this.serialNumberReservation = serialNumberReservation;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        setChanged();
        this.createdBy = createdBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        setChanged();
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        setChanged();
        this.modifiedBy = modifiedBy;
    }

    public Boolean getRelocated() {
        return relocated;
    }

    public void setRelocated(Boolean relocated) {
        setChanged();
        this.relocated = relocated;
    }

    public Boolean getArchived() {
        if (archived == null) {
            setArchived(false);
        }
        return archived;
    }

    public void setArchived(Boolean archived) {
        setChanged();
        this.archived = archived;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Integer getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(Integer warehouseNo) {
        setChanged();
        this.warehouseNo = warehouseNo;
    }

    public JsonNode getEmbedded() {
        return _embedded;
    }

    public void setEmbedded(JsonNode _embedded) {
        this._embedded = _embedded;
    }

    public Integer getOriginalWarehouseNo() {
        return originalWarehouseNo;
    }

    public void setOriginalWarehouseNo(Integer originalWarehouseNo) {
        this.originalWarehouseNo = originalWarehouseNo;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
