package com.brodos.reservation.entity;

import java.io.Serializable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@DiscriminatorValue("ReservationTicketRef")
public class SerialNumberReservationTicketReference extends TicketReference implements Serializable {

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
