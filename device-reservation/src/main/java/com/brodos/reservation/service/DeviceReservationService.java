package com.brodos.reservation.service;

import java.util.List;

import com.brodos.reservation.dto.request.DeviceReservationActionDTO;
import com.brodos.reservation.dto.request.DeviceReservationRequestDTO;
import com.brodos.reservation.entity.DeviceReservationStatus;
import com.brodos.reservation.entity.SerialNumber;
import com.brodos.reservation.entity.SerialNumberReservation;
import com.brodos.reservation.entity.TicketReference;
import com.brodos.reservation.events.SerialNumberSentoutEvent;
import org.springframework.data.domain.Page;

public interface DeviceReservationService {

    public SerialNumberReservation getDeviceReservationById(Long id);

    public List<SerialNumberReservation> getDeviceReservationByBulkReservationId(Long bulkid);

    public SerialNumberReservation createDeviceReservationRequest(
        DeviceReservationRequestDTO deviceReservationRequestDTO, Long bulkReservationId, String requestId);

    public List<SerialNumberReservation>
        createDeviceReservationRequests(List<DeviceReservationRequestDTO> deviceReservationRequestDTOs);

    public Page<TicketReference> getDeviceReservationBySearchCriteria(DeviceReservationStatus status, String articleNo,
        String customerNo, String reference, String imei1, int offset, int size);

    public SerialNumberReservation cancelDeviceReservation(Long id, String reason, boolean isArchive);

    public SerialNumberReservation requestForSentoutDeviceReservation(Long id,
        DeviceReservationActionDTO deviceReservationActionDTO);

    public SerialNumberReservation doDeviceReservationAction(Long id,
        DeviceReservationActionDTO deviceReservationActionDTO);

    public SerialNumberReservation sentoutDeviceReservation(SerialNumberSentoutEvent serialNumberSentoutEvent);

    public boolean reserveOpencaseIfExist(SerialNumber serialNumber);
}
