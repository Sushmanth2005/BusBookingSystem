package com.busease.service;

import com.busease.dto.RouteRequest;
import com.busease.dto.RouteResponse;
import com.busease.entity.Route;
import com.busease.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteResponse addRoute(RouteRequest request) {
        Route route = Route.builder()
                .source(request.getSource())
                .destination(request.getDestination())
                .distance(request.getDistance())
                .build();
        Route savedRoute = routeRepository.save(route);
        return mapToResponse(savedRoute);
    }

    public List<RouteResponse> getAllRoutes() {
        return routeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RouteResponse getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));
        return mapToResponse(route);
    }

    public RouteResponse updateRoute(Long id, RouteRequest request) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found with id: " + id));

        route.setSource(request.getSource());
        route.setDestination(request.getDestination());
        route.setDistance(request.getDistance());

        Route updatedRoute = routeRepository.save(route);
        return mapToResponse(updatedRoute);
    }

    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new RuntimeException("Route not found with id: " + id);
        }
        routeRepository.deleteById(id);
    }

    private RouteResponse mapToResponse(Route route) {
        return RouteResponse.builder()
                .id(route.getId())
                .source(route.getSource())
                .destination(route.getDestination())
                .distance(route.getDistance())
                .build();
    }
}
