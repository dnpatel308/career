package com.brodos.reservation.dto.response;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ReservationDTO {

    List<DeviceReservationResponseDTO> reservations;

    public List<DeviceReservationResponseDTO> getReservations() {
        return reservations;
    }

    public void setReservations(List<DeviceReservationResponseDTO> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
