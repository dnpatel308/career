package com.brodos.reservation.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "ticket_reference")
@DiscriminatorColumn(name = "DTYPE")
public abstract class TicketReference implements Serializable {

    private static final long serialVersionUID = -6394162757061362503L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "ticket_number")
    private String ticketNumber;

    @Embedded
    private TenantId tenantId;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

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

    @Column(name = "email")
    private String email;

    @Column(name = "sales_email")
    private String salesPersonnelEmail;

    @Column(name = "reservation_comment")
    private String reservationComment;

    @Column(name = "cancellation_comment")
    private String cancellationComment;

    @Column(name = "ticket_failed_reason")
    private String ticketFailedReason;

    @OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "serial_number_res_id", referencedColumnName = "id")
    private SerialNumberReservation serialNumberReservation;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    protected Owner owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({ @JoinColumn(name = "article_number", referencedColumnName = "article_number"),
            @JoinColumn(name = "article_tenant_id", referencedColumnName = "tenant_id") })
    protected Article article;

    @Column(name = "warehouse_no")
    private Integer warehouseNo;

    @PostLoad
    public void postLoad() {
        if (serialNumberReservation != null) {
            serialNumberReservation.setTicketReference(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
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
        this.createdBy = createdBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReservationComment() {
        return reservationComment;
    }

    public void setReservationComment(String reservationComment) {
        this.reservationComment = reservationComment;
    }

    public String getCancellationComment() {
        return cancellationComment;
    }

    public void setCancellationComment(String cancellationComment) {
        this.cancellationComment = cancellationComment;
    }

    public String getSalesPersonnelEmail() {
        return salesPersonnelEmail;
    }

    public void setSalesPersonnelEmail(String salesPersonnelEmail) {
        this.salesPersonnelEmail = salesPersonnelEmail;
    }

    public String getTicketFailedReason() {
        return ticketFailedReason;
    }

    public void setTicketFailedReason(String ticketFailedReason) {
        this.ticketFailedReason = ticketFailedReason;
    }

    public SerialNumberReservation getSerialNumberReservation() {
        return serialNumberReservation;
    }

    public void setSerialNumberReservation(SerialNumberReservation serialNumberReservation) {
        if (serialNumberReservation != null) {
            serialNumberReservation.setTicketReference(this);
        }

        this.serialNumberReservation = serialNumberReservation;
    }

    public Integer getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(Integer warehouseNo) {
        this.warehouseNo = warehouseNo;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
