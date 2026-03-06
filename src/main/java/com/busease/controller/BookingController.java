package com.busease.controller;

import com.busease.dto.BookingRequest;
import com.busease.dto.BookingResponse;
import com.busease.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            Authentication authentication,
            @RequestBody @Valid BookingRequest request) {
        String email = authentication.getName();
        return new ResponseEntity<>(bookingService.createBooking(email, request), HttpStatus.CREATED);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(bookingService.getUserBookings(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            Authentication authentication,
            @PathVariable Long id) {
        String email = authentication.getName();
        bookingService.cancelBooking(email, id);
        return ResponseEntity.noContent().build();
    }
}
