package com.brodos.reservation.entity;

import com.brodos.reservation.events.DomainEventAbstract;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "serial_number_reservation")
public class SerialNumberReservation implements Serializable {

    private static final long serialVersionUID = -6304775452958206894L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private Owner owner;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE, optional = true)
    @JoinColumn(name = "serial_number_id", referencedColumnName = "id")
    private SerialNumber serialNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_time", nullable = true)
    private Date expirationTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reservation_time", insertable = false, updatable = false,
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date reservationTime;

    @Column(name = "reserved_by", nullable = true)
    private String reservedBy;

    @Column(name = "mail_sent_to_customer")
    private Boolean mailSentToCustomer;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sentout_requested_time", insertable = true, updatable = true, nullable = true)
    private Date sentoutRequestedTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "cancellation_requested_time", insertable = true, updatable = true, nullable = true)
    private Date cancellationRequestedTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "cancellation_time", insertable = true, updatable = true, nullable = true)
    private Date cancellationTime;

    @Column(name = "cancelled_by", nullable = true)
    private String cancelledBy;

    @Column(name = "status", nullable = true)
    @Enumerated(EnumType.STRING)
    private DeviceReservationStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sentout_time", insertable = true, updatable = true, nullable = true)
    private Date sentOutTime;

    @Column(name = "sentout_requested_by", nullable = true)
    private String sentoutRequestedBy;

    @Column(name = "sent_by", nullable = true)
    private String sentBy;

    @Column(name = "is_archived")
    private Boolean archived;

    @Column(name = "ticket_no")
    private String ticketNo;

    @Column(name = "customer_notified")
    private Boolean customerNotified;

    @Column(name = "sales_personnel_notified")
    private Boolean salesPersonnelNotified;

    @Column(name = "brodos_voucher_no")
    private String brodosVoucherNo;

    @Column(name = "brodos_voucher_mail_status")
    private Boolean brodosVoucherMailSuccess;

    @Column(name = "bulk_reservation_id")
    private Long bulkReservationId;

    @Column(name = "client")
    private String client;

    @Column(name = "modified_by", nullable = true)
    private String modifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_date", insertable = true, updatable = true, nullable = true)
    private Date modifiedDate;

    @Column(name = "request_id")
    private String requestId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reservationReferenceID.serialNumberReservation",
        fetch = FetchType.EAGER)
    private Set<ReservationReference> reservationReferences;

    private Boolean consignment;

    @Transient
    TicketReference ticketReference;

    @Transient
    DomainEventAbstract domainEventAbstract;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public SerialNumber getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(SerialNumber serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Date getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(Date reservationTime) {
        this.reservationTime = reservationTime;
    }

    public Date getCancellationRequestedTime() {
        return cancellationRequestedTime;
    }

    public void setCancellationRequestedTime(Date cancellationRequestedTime) {
        this.cancellationRequestedTime = cancellationRequestedTime;
    }

    public Date getCancellationTime() {
        return cancellationTime;
    }

    public void setCancellationTime(Date cancellationTime) {
        this.cancellationTime = cancellationTime;
    }

    public Boolean isMailSentToCustomer() {
        return mailSentToCustomer;
    }

    public void setMailSentToCustomer(Boolean mailSentToCustomer) {
        this.mailSentToCustomer = mailSentToCustomer;
    }

    public DeviceReservationStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceReservationStatus status) {
        this.status = status;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(String reservedBy) {
        this.reservedBy = reservedBy;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public Date getSentOutTime() {
        return sentOutTime;
    }

    public void setSentOutTime(Date sentOutTime) {
        this.sentOutTime = sentOutTime;
    }

    public String getSentBy() {
        return sentBy;
    }

    public String getSentoutRequestedBy() {
        return sentoutRequestedBy;
    }

    public void setSentoutRequestedBy(String sentoutRequestedBy) {
        this.sentoutRequestedBy = sentoutRequestedBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public Boolean isArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public Boolean isCustomerNotified() {
        return customerNotified;
    }

    public void setCustomerNotified(Boolean customerNotified) {
        this.customerNotified = customerNotified;
    }

    public Boolean isSalesPersonnelNotified() {
        return salesPersonnelNotified;
    }

    public void setSalesPersonnelNotified(Boolean salesPersonnelNotified) {
        this.salesPersonnelNotified = salesPersonnelNotified;
    }

    public Date getSentoutRequestedTime() {
        return sentoutRequestedTime;
    }

    public void setSentoutRequestedTime(Date sentoutRequestedTime) {
        this.sentoutRequestedTime = sentoutRequestedTime;
    }

    public String getBrodosVoucherNo() {
        return brodosVoucherNo;
    }

    public void setBrodosVoucherNo(String brodosVoucherNo) {
        this.brodosVoucherNo = brodosVoucherNo;
    }

    public Boolean getBrodosVoucherMailSuccess() {
        return brodosVoucherMailSuccess;
    }

    public void setBrodosVoucherMailSuccess(Boolean brodosVoucherMailSuccess) {
        this.brodosVoucherMailSuccess = brodosVoucherMailSuccess;
    }

    public Long getBulkReservationId() {
        return bulkReservationId;
    }

    public void setBulkReservationId(Long bulkReservationId) {
        this.bulkReservationId = bulkReservationId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public TicketReference getTicketReference() {
        return ticketReference;
    }

    public void setTicketReference(TicketReference ticketReference) {
        this.ticketReference = ticketReference;
    }

    public DomainEventAbstract getDomainEvent() {
        return domainEventAbstract;
    }

    public void setDomainEvent(DomainEventAbstract domainEventAbstract) {
        this.domainEventAbstract = domainEventAbstract;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Set<ReservationReference> getReservationReferences() {
        return reservationReferences;
    }

    public void setReservationReferences(Set<ReservationReference> reservationReferences) {
        this.reservationReferences = reservationReferences;
    }

    public Boolean getConsignment() {
        return consignment;
    }

    public void setConsignment(Boolean consignment) {
        this.consignment = consignment;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SerialNumberReservation other = (SerialNumberReservation) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
