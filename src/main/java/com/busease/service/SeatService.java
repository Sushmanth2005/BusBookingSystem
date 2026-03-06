package com.busease.service;

import com.busease.dto.SeatResponse;
import com.busease.entity.Seat;
import com.busease.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public List<SeatResponse> getSeatsByBusId(Long busId) {
        return seatRepository.findByBusId(busId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SeatResponse mapToResponse(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .busId(seat.getBus().getId())
                .seatNumber(seat.getSeatNumber())
                .isBooked(seat.getIsBooked())
                .build();
    }
}
