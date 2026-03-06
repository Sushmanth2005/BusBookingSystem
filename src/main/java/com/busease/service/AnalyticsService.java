package com.busease.service;

import com.busease.enums.BookingStatus;
import com.busease.repository.BookingRepository;
import com.busease.repository.BusRepository;
import com.busease.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final BookingRepository bookingRepository;
    private final BusRepository busRepository;
    private final UserRepository userRepository;

    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalBookings", bookingRepository.count());
        summary.put("confirmedBookings", bookingRepository.countByStatus(BookingStatus.CONFIRMED));
        summary.put("cancelledBookings", bookingRepository.countByStatus(BookingStatus.CANCELLED));
        summary.put("totalRevenue", bookingRepository.sumTotalAmountByStatus(BookingStatus.CONFIRMED));
        summary.put("totalBuses", busRepository.count());
        summary.put("totalUsers", userRepository.count());
        return summary;
    }

    public List<Map<String, Object>> getBookingsByRoute() {
        return bookingRepository.countBookingsByRoute().stream()
                .map(row -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("route", row[0] + " → " + row[1]);
                    item.put("bookings", row[2]);
                    return item;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getRevenueByDay() {
        return bookingRepository.revenueByDay().stream()
                .map(row -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("date", row[0].toString());
                    item.put("bookings", row[1]);
                    item.put("revenue", row[2]);
                    return item;
                })
                .collect(Collectors.toList());
    }
}
