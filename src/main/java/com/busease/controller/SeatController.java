package com.busease.controller;

import com.busease.dto.SeatResponse;
import com.busease.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<SeatResponse>> getSeatsByBusId(@PathVariable Long busId) {
        return ResponseEntity.ok(seatService.getSeatsByBusId(busId));
    }
}
