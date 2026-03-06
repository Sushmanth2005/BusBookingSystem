package com.busease.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatResponse {
    private Long id;
    private Long busId;
    private String seatNumber;
    private Boolean isBooked;
}
