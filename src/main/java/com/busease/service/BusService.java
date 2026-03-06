package com.busease.service;

import com.busease.dto.BusRequest;
import com.busease.dto.BusResponse;
import com.busease.entity.Bus;
import com.busease.entity.Seat;
import com.busease.repository.BusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusService {

    private final BusRepository busRepository;

    @Transactional
    public BusResponse addBus(BusRequest request) {
        Bus bus = Bus.builder()
                .busName(request.getBusName())
                .totalSeats(request.getTotalSeats())
                .type(request.getType())
                .seats(new ArrayList<>())
                .build();

        // Auto-generate seats
        for (int i = 1; i <= request.getTotalSeats(); i++) {
            Seat seat = Seat.builder()
                    .bus(bus)
                    .seatNumber("S" + i)
                    .isBooked(false)
                    .build();
            bus.getSeats().add(seat);
        }

        Bus savedBus = busRepository.save(bus);
        return mapToResponse(savedBus);
    }

    public List<BusResponse> getAllBuses() {
        return busRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BusResponse getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found with id: " + id));
        return mapToResponse(bus);
    }

    @Transactional
    public BusResponse updateBus(Long id, BusRequest request) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found with id: " + id));

        bus.setBusName(request.getBusName());
        bus.setType(request.getType());
        // For simplicity in V1, we do not regenerate seats upon update if totalSeats changes.
        // It requires complex logic to handle booked seats. We'll update the value only.
        bus.setTotalSeats(request.getTotalSeats());

        Bus updatedBus = busRepository.save(bus);
        return mapToResponse(updatedBus);
    }

    @Transactional
    public void deleteBus(Long id) {
        if (!busRepository.existsById(id)) {
            throw new RuntimeException("Bus not found with id: " + id);
        }
        busRepository.deleteById(id);
    }

    private BusResponse mapToResponse(Bus bus) {
        return BusResponse.builder()
                .id(bus.getId())
                .busName(bus.getBusName())
                .totalSeats(bus.getTotalSeats())
                .type(bus.getType())
                .build();
    }
}
