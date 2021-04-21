/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.entity;

import com.brodos.reservation.ErrorCodes;
import com.brodos.reservation.exception.DeviceReservationException;
import com.brodos.reservation.utility.Utils;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.LoggerFactory;

/**
 *
 * @author padhaval
 */
@Entity
@Table(name = "device_reservation_domainevents")
public class DeviceReservationDomainevents implements Serializable {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(DeviceReservationDomainevents.class);

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "type_name")
    private String typeName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "occurred_on")
    @Temporal(TemporalType.TIMESTAMP)
    private final Date occurredOn;
    @Column(name = "`version`")
    private final Integer version;
    @Column(name = "reservation_id")
    private Long reservationId;
    @Column(name = "`group`")
    private Integer group;
    @Column(name = "`status`")
    private String status;
    @Column(name = "event_body")
    private String eventBody;

    public DeviceReservationDomainevents() {
        this.version = 1;
        this.occurredOn = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Date getOccurredOn() {
        return occurredOn;
    }

    public Integer getVersion() {
        return version;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEventBody() {
        return eventBody;
    }

    public void setEventBody(String eventBody) {
        this.eventBody = eventBody;
    }

    public Object extractEventBodyObject() {
        try {
            return Utils.readValue(getEventBody(), Class.forName(getTypeName()));
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            throw new DeviceReservationException(ErrorCodes.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
