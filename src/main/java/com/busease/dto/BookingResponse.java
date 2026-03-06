package com.busease.dto;

import com.busease.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long scheduleId;
    private String source;
    private String destination;
    private LocalDateTime departureTime;
    private List<String> seatNumbers;
    private BookingStatus status;
    private Double totalAmount;
    private LocalDateTime bookingDate;
}
