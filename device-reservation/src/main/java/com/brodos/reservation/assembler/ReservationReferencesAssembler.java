/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brodos.reservation.assembler;

import com.brodos.reservation.dto.ReservationReferenceDTO;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.ReservationReference;
import com.brodos.reservation.entity.ReservationReferenceID;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author padhaval
 */
public class ReservationReferencesAssembler {

    public Set<ReservationReference> toReservationReferences(SerialNumberReservation serialNumberReservation, Set<ReservationReferenceDTO> reservationReferenceDTOs) {
        Set<ReservationReference> serialNumberReservationReferences = new HashSet<>();
        if (reservationReferenceDTOs != null) {
            for (ReservationReferenceDTO reservationReferenceDTO : reservationReferenceDTOs) {
                ReservationReference serialNumberReservationProperty = new ReservationReference();
                serialNumberReservationProperty.setReservationReferenceID(new ReservationReferenceID(reservationReferenceDTO.getLabel(), serialNumberReservation));
                serialNumberReservationProperty.setValue(reservationReferenceDTO.getValue());
                serialNumberReservationReferences.add(serialNumberReservationProperty);
            }
        }

        return serialNumberReservationReferences;
    }

    public Set<ReservationReferenceDTO> toReservationReferences(Set<ReservationReference> reservationReferences) {
        Set<ReservationReferenceDTO> deviceReservationReferences = new HashSet<>();
        if (reservationReferences != null) {
            for (ReservationReference reservationReference : reservationReferences) {
                ReservationReferenceDTO deviceReservationPropertyDTO = new ReservationReferenceDTO();
                deviceReservationPropertyDTO.setLabel(reservationReference.getReservationReferenceID().getLabel());
                deviceReservationPropertyDTO.setValue(reservationReference.getValue());
                deviceReservationReferences.add(deviceReservationPropertyDTO);
            }
        }

        return deviceReservationReferences;
    }
}
