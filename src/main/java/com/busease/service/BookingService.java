package com.busease.service;

import com.busease.dto.BookingRequest;
import com.busease.dto.BookingResponse;
import com.busease.entity.Booking;
import com.busease.entity.Schedule;
import com.busease.entity.Seat;
import com.busease.entity.User;
import com.busease.enums.BookingStatus;
import com.busease.repository.BookingRepository;
import com.busease.repository.ScheduleRepository;
import com.busease.repository.SeatRepository;
import com.busease.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingResponse createBooking(String userEmail, BookingRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        // Pessimistic lock on seats
        List<Seat> seats = seatRepository.findByBusIdAndSeatNumbersForUpdate(schedule.getBus().getId(), request.getSeatNumbers());

        if (seats.size() != request.getSeatNumbers().size()) {
            throw new RuntimeException("One or more invalid seats selected");
        }

        for (Seat seat : seats) {
            if (seat.getIsBooked()) {
                throw new RuntimeException("Seat " + seat.getSeatNumber() + " is already booked");
            }
        }

        // Mark seats as booked
        for (Seat seat : seats) {
            seat.setIsBooked(true);
        }
        seatRepository.saveAll(seats);

        // Calculate amount
        Double totalAmount = schedule.getPrice() * request.getSeatNumbers().size();

        // Create booking record
        Booking booking = Booking.builder()
                .user(user)
                .schedule(schedule)
                .seatNumbers(String.join(",", request.getSeatNumbers()))
                .bookingDate(LocalDateTime.now())
                .status(BookingStatus.CONFIRMED)
                .totalAmount(totalAmount)
                .passengerName(request.getPassengerName())
                .passengerPhone(request.getPassengerPhone())
                .passengerEmail(request.getPassengerEmail())
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        return mapToResponse(savedBooking);
    }

    public List<BookingResponse> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllByOrderByBookingDateDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelBooking(String userEmail, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Not authorized to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        // Release seats
        List<String> seatNumbers = Arrays.asList(booking.getSeatNumbers().split(","));
        List<Seat> seats = seatRepository.findByBusIdAndSeatNumbersForUpdate(booking.getSchedule().getBus().getId(), seatNumbers);
        
        for (Seat seat : seats) {
            seat.setIsBooked(false);
        }
        seatRepository.saveAll(seats);

        // Update status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .scheduleId(booking.getSchedule().getId())
                .source(booking.getSchedule().getRoute().getSource())
                .destination(booking.getSchedule().getRoute().getDestination())
                .busName(booking.getSchedule().getBus().getBusName())
                .departureTime(booking.getSchedule().getDepartureTime())
                .seatNumbers(Arrays.asList(booking.getSeatNumbers().split(",")))
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .bookingDate(booking.getBookingDate())
                .passengerName(booking.getPassengerName())
                .passengerPhone(booking.getPassengerPhone())
                .build();
    }
}
