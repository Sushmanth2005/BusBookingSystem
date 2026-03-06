package com.busease.repository;

import com.busease.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByBusId(Long busId);

    List<Seat> findByBusIdAndIsBooked(Long busId, Boolean isBooked);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.bus.id = :busId AND s.seatNumber IN :seatNumbers")
    List<Seat> findByBusIdAndSeatNumbersForUpdate(
            @Param("busId") Long busId,
            @Param("seatNumbers") List<String> seatNumbers
    );
}
