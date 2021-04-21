/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.events;

import com.brodos.reservation.entity.DeviceReservationStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author padhaval
 */
public class Imported extends DomainEventAbstract {

    public Imported() {
        super(DeviceReservationStatus.IMPORTED);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
