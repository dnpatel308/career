/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.events;

import com.brodos.reservation.dto.ReservationReferenceDTO;
import com.brodos.reservation.entity.DeviceReservationStatus;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.utility.EventDateSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Date;
import java.util.Set;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public abstract class DomainEventAbstract {

    @JsonProperty("reservation_id")
    Long reservationId;
    DeviceReservationStatus status;
    Integer group;
    @JsonProperty("article_no")
    String articleNo;
    @JsonProperty("cancel_if_unavailable")
    Boolean cancelIfUnavailable;
    @JsonProperty("created_by")
    String createdBy;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonSerialize(using = EventDateSerializer.class)
    @JsonProperty("created_at")
    Date createdAt;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonSerialize(using = EventDateSerializer.class)
    @JsonProperty("pended_at")
    Date pendedAt;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonSerialize(using = EventDateSerializer.class)
    @JsonProperty("reserved_at")
    Date reservedAt;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonSerialize(using = EventDateSerializer.class)
    @JsonProperty("cancelled_at")
    Date cancelledAt;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonSerialize(using = EventDateSerializer.class)
    @JsonProperty("requested_for_sendout_at")
    Date requestedForSendoutAt;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonSerialize(using = EventDateSerializer.class)
    @JsonProperty("sentout_at")
    Date sentoutAt;
    String comment;
    String email;
    String customerno;
    Boolean consignment;
    @JsonProperty("delivery_details")
    JsonNode deliveryDetails;
    Set<ReservationReferenceDTO> references;
    JsonNode device;

    @JsonIgnore
    private TicketReference ticketReference;

    public DomainEventAbstract(DeviceReservationStatus status) {
        this.status = status;
        this.createdAt = new Date();
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public DeviceReservationStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceReservationStatus status) {
        this.status = status;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public String getArticleNo() {
        return articleNo;
    }

    public void setArticleNo(String articleNo) {
        this.articleNo = articleNo;
    }

    public Boolean getCancelIfUnavailable() {
        return cancelIfUnavailable;
    }

    public void setCancelIfUnavailable(Boolean cancelIfUnavailable) {
        this.cancelIfUnavailable = cancelIfUnavailable;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getPendedAt() {
        return pendedAt;
    }

    public void setPendedAt(Date pendedAt) {
        this.pendedAt = pendedAt;
    }

    public Date getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(Date reservedAt) {
        this.reservedAt = reservedAt;
    }

    public Date getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Date cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public Date getRequestedForSendoutAt() {
        return requestedForSendoutAt;
    }

    public void setRequestedForSendoutAt(Date requestedForSendoutAt) {
        this.requestedForSendoutAt = requestedForSendoutAt;
    }

    public Date getSentoutAt() {
        return sentoutAt;
    }

    public void setSentoutAt(Date sentoutAt) {
        this.sentoutAt = sentoutAt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerno() {
        return customerno;
    }

    public void setCustomerno(String customerno) {
        this.customerno = customerno;
    }

    public Set<ReservationReferenceDTO> getReferences() {
        return references;
    }

    public void setReferences(Set<ReservationReferenceDTO> references) {
        this.references = references;
    }

    public JsonNode getDevice() {
        return device;
    }

    public void setDevice(JsonNode device) {
        this.device = device;
    }

    public TicketReference getTicketReference() {
        return ticketReference;
    }

    public void setTicketReference(TicketReference ticketReference) {
        this.ticketReference = ticketReference;
    }

    public Boolean getConsignment() {
        return consignment;
    }

    public void setConsignment(Boolean consignment) {
        this.consignment = consignment;
    }

    public JsonNode getDeliveryDetails() {
        return deliveryDetails;
    }

    public void setDeliveryDetails(JsonNode deliveryDetails) {
        this.deliveryDetails = deliveryDetails;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
