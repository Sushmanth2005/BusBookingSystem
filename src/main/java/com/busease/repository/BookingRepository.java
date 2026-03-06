package com.busease.repository;

import com.busease.entity.Booking;
import com.busease.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByScheduleIdAndStatus(Long scheduleId, BookingStatus status);

    long countByStatus(BookingStatus status);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.status = :status")
    Double sumTotalAmountByStatus(@Param("status") BookingStatus status);

    @Query("SELECT r.source, r.destination, COUNT(b) FROM Booking b " +
           "JOIN b.schedule s JOIN s.route r WHERE b.status = 'CONFIRMED' " +
           "GROUP BY r.source, r.destination ORDER BY COUNT(b) DESC")
    List<Object[]> countBookingsByRoute();

    @Query("SELECT CAST(b.bookingDate AS date), COUNT(b), COALESCE(SUM(b.totalAmount), 0) " +
           "FROM Booking b WHERE b.status = 'CONFIRMED' " +
           "GROUP BY CAST(b.bookingDate AS date) ORDER BY CAST(b.bookingDate AS date)")
    List<Object[]> revenueByDay();

    List<Booking> findAllByOrderByBookingDateDesc();
}
