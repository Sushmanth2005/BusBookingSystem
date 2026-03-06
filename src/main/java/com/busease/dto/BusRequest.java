package com.busease.dto;

import com.busease.enums.BusType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusRequest {

    @NotBlank(message = "Bus name is required")
    private String busName;

    @Min(value = 10, message = "Bus must have at least 10 seats")
    private int totalSeats;

    @NotNull(message = "Bus type is required")
    private BusType type;
}
