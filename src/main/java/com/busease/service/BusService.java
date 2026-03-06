package com.busease.service;

import com.busease.dto.BusRequest;
import com.busease.dto.BusResponse;
import com.busease.entity.Booking;
import com.busease.entity.Bus;
import com.busease.entity.Schedule;
import com.busease.entity.Seat;
import com.busease.enums.BookingStatus;
import com.busease.repository.BookingRepository;
import com.busease.repository.BusRepository;
import com.busease.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusService {

    private final BusRepository busRepository;
    private final ScheduleRepository scheduleRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;

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
        bus.setTotalSeats(request.getTotalSeats());

        Bus updatedBus = busRepository.save(bus);
        return mapToResponse(updatedBus);
    }

    @Transactional
    public void deleteBus(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found with id: " + id));

        // Find all schedules for this bus
        List<Schedule> schedules = scheduleRepository.findByBusId(id);

        // For each schedule, cancel active bookings and notify users
        for (Schedule schedule : schedules) {
            List<Booking> activeBookings = bookingRepository.findByScheduleIdAndStatus(
                    schedule.getId(), BookingStatus.CONFIRMED);

            for (Booking booking : activeBookings) {
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);

                // Notify the user
                String message = String.format(
                    "Your booking #%06d (%s → %s on %s, Bus: %s) has been cancelled because the bus has been removed from service. A full refund will be processed.",
                    booking.getId(),
                    schedule.getRoute().getSource(),
                    schedule.getRoute().getDestination(),
                    schedule.getDepartureTime().toLocalDate(),
                    bus.getBusName()
                );
                notificationService.createNotification(booking.getUser(), message, "BUS_CANCELLED");

                log.info("Cancelled booking #{} and notified user {} due to bus removal",
                        booking.getId(), booking.getUser().getEmail());
            }
        }

        // Delete schedules first (due to FK constraints), then the bus
        scheduleRepository.deleteAll(schedules);
        busRepository.delete(bus);

        log.info("Deleted bus '{}' (ID: {}), cancelled {} bookings across {} schedules",
                bus.getBusName(), id,
                schedules.stream().mapToLong(s -> bookingRepository.findByScheduleIdAndStatus(s.getId(), BookingStatus.CANCELLED).size()).sum(),
                schedules.size());
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
