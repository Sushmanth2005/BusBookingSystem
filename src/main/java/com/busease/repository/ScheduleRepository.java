package com.busease.repository;

import com.busease.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s JOIN FETCH s.bus JOIN FETCH s.route " +
           "WHERE s.route.source = :source " +
           "AND s.route.destination = :destination " +
           "AND s.departureTime >= :startOfDay " +
           "AND s.departureTime < :endOfDay " +
           "AND s.departureTime > :now")
    List<Schedule> searchSchedules(
            @Param("source") String source,
            @Param("destination") String destination,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("now") LocalDateTime now
    );
}
