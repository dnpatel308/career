/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.events.filter;

import com.brodos.reservation.entity.DeviceReservationDomainevents;
import com.brodos.reservation.events.Imported;
import net.engio.mbassy.listener.IMessageFilter;
import net.engio.mbassy.subscription.SubscriptionContext;

/**
 *
 * @author padhaval
 */
public class ImportedEventFilter implements IMessageFilter {

    @Override
    public boolean accepts(Object m, SubscriptionContext sc) {
        DeviceReservationDomainevents deviceReservationDomainevents = (DeviceReservationDomainevents) m;
        return deviceReservationDomainevents.getTypeName().equalsIgnoreCase(Imported.class.getName());
    }
}
