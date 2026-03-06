package com.busease.service;

import com.busease.dto.ScheduleRequest;
import com.busease.dto.ScheduleResponse;
import com.busease.entity.Bus;
import com.busease.entity.Route;
import com.busease.entity.Schedule;
import com.busease.repository.BusRepository;
import com.busease.repository.RouteRepository;
import com.busease.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;

    public ScheduleResponse addSchedule(ScheduleRequest request) {
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found with id: " + request.getBusId()));

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + request.getRouteId()));

        Schedule schedule = Schedule.builder()
                .bus(bus)
                .route(route)
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .price(request.getPrice())
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return mapToResponse(savedSchedule);
    }

    public List<ScheduleResponse> getAllSchedules() {
        return scheduleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ScheduleResponse getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));
        return mapToResponse(schedule);
    }

    public ScheduleResponse updateSchedule(Long id, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id: " + id));

        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found with id: " + request.getBusId()));

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + request.getRouteId()));

        schedule.setBus(bus);
        schedule.setRoute(route);
        schedule.setDepartureTime(request.getDepartureTime());
        schedule.setArrivalTime(request.getArrivalTime());
        schedule.setPrice(request.getPrice());

        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return mapToResponse(updatedSchedule);
    }

    public void deleteSchedule(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new RuntimeException("Schedule not found with id: " + id);
        }
        scheduleRepository.deleteById(id);
    }

    public List<ScheduleResponse> searchSchedules(String source, String destination, java.time.LocalDate date) {
        java.time.LocalDateTime startOfDay = date.atStartOfDay();
        java.time.LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        return scheduleRepository.searchSchedules(source, destination, startOfDay, endOfDay, now).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ScheduleResponse mapToResponse(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .busId(schedule.getBus().getId())
                .busName(schedule.getBus().getBusName())
                .routeId(schedule.getRoute().getId())
                .source(schedule.getRoute().getSource())
                .destination(schedule.getRoute().getDestination())
                .departureTime(schedule.getDepartureTime())
                .arrivalTime(schedule.getArrivalTime())
                .price(schedule.getPrice())
                .build();
    }
}
