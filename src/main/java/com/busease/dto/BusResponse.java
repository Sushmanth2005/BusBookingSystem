package com.busease.dto;

import com.busease.enums.BusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusResponse {
    private Long id;
    private String busName;
    private int totalSeats;
    private BusType type;
}
