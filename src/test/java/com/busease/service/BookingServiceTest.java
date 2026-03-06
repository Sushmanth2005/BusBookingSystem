package com.busease.service;

import com.busease.dto.BookingRequest;
import com.busease.dto.BookingResponse;
import com.busease.entity.Bus;
import com.busease.entity.Route;
import com.busease.entity.Schedule;
import com.busease.entity.Seat;
import com.busease.entity.User;
import com.busease.enums.BusType;
import com.busease.enums.Role;
import com.busease.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceTest {

    @Autowired private BookingService bookingService;
    @Autowired private UserRepository userRepository;
    @Autowired private BusRepository busRepository;
    @Autowired private RouteRepository routeRepository;
    @Autowired private ScheduleRepository scheduleRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private User testUser;
    private Schedule testSchedule;

    @BeforeEach
    void setUp() {
        // Create test user (DataInitializer may have already created users and data,
        // but we create our own isolated set for predictable test data)
        testUser = userRepository.save(User.builder()
                .name("Booking Test User")
                .email("booktest@test.com")
                .password(passwordEncoder.encode("test123"))
                .role(Role.USER)
                .build());

        // Create bus with seats
        Bus bus = Bus.builder()
                .busName("Test Bus")
                .totalSeats(10)
                .type(BusType.AC)
                .seats(new ArrayList<>())
                .build();
        bus = busRepository.save(bus);

        // Create seats
        for (int i = 1; i <= 10; i++) {
            Seat seat = Seat.builder()
                    .bus(bus)
                    .seatNumber("S" + i)
                    .isBooked(false)
                    .build();
            seatRepository.save(seat);
        }

        // Create route
        Route route = routeRepository.save(Route.builder()
                .source("TestCity A")
                .destination("TestCity B")
                .distance(100.0)
                .build());

        // Create schedule
        testSchedule = scheduleRepository.save(Schedule.builder()
                .bus(bus)
                .route(route)
                .departureTime(LocalDateTime.now().plusDays(1))
                .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(3))
                .price(250.0)
                .build());
    }

    @Test
    void createBooking_success() {
        BookingRequest request = new BookingRequest();
        request.setScheduleId(testSchedule.getId());
        request.setSeatNumbers(List.of("S1", "S2"));

        BookingResponse response = bookingService.createBooking("booktest@test.com", request);

        assertNotNull(response);
        assertEquals(2, response.getSeatNumbers().size());
        assertEquals(500.0, response.getTotalAmount()); // 250 × 2
        assertEquals("CONFIRMED", response.getStatus().name());
    }

    @Test
    void createBooking_doubleBooking_throwsException() {
        // Book seats S1, S2
        BookingRequest request1 = new BookingRequest();
        request1.setScheduleId(testSchedule.getId());
        request1.setSeatNumbers(List.of("S1", "S2"));
        bookingService.createBooking("booktest@test.com", request1);

        // Try booking same seats again
        BookingRequest request2 = new BookingRequest();
        request2.setScheduleId(testSchedule.getId());
        request2.setSeatNumbers(List.of("S1"));

        assertThrows(RuntimeException.class,
                () -> bookingService.createBooking("booktest@test.com", request2));
    }

    @Test
    void cancelBooking_success() {
        BookingRequest request = new BookingRequest();
        request.setScheduleId(testSchedule.getId());
        request.setSeatNumbers(List.of("S3"));

        BookingResponse response = bookingService.createBooking("booktest@test.com", request);

        // Cancel the booking
        assertDoesNotThrow(() ->
                bookingService.cancelBooking("booktest@test.com", response.getId()));
    }

    @Test
    void cancelBooking_unauthorizedUser_throwsException() {
        // Create a second user
        userRepository.save(User.builder()
                .name("Other User")
                .email("other@test.com")
                .password(passwordEncoder.encode("test123"))
                .role(Role.USER)
                .build());

        BookingRequest request = new BookingRequest();
        request.setScheduleId(testSchedule.getId());
        request.setSeatNumbers(List.of("S4"));

        BookingResponse response = bookingService.createBooking("booktest@test.com", request);

        // Try to cancel with a different user
        assertThrows(RuntimeException.class,
                () -> bookingService.cancelBooking("other@test.com", response.getId()));
    }

    @Test
    void getUserBookings_returnsCorrectBookings() {
        BookingRequest request = new BookingRequest();
        request.setScheduleId(testSchedule.getId());
        request.setSeatNumbers(List.of("S5"));
        bookingService.createBooking("booktest@test.com", request);

        List<BookingResponse> bookings = bookingService.getUserBookings("booktest@test.com");

        assertFalse(bookings.isEmpty());
    }
}
