package com.example.demo.repository;

import com.example.demo.entity.Booking;
import com.example.demo.entity.Property;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUser(User user);
    
    List<Booking> findByProperty(Property property);
    
    List<Booking> findByStatus(Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.property = :property AND " +
           "b.status IN ('CONFIRMED', 'PENDING') AND " +
           "((b.checkInDate <= :checkOut AND b.checkOutDate >= :checkIn))")
    List<Booking> findConflictingBookings(@Param("property") Property property,
                                        @Param("checkIn") LocalDate checkIn,
                                        @Param("checkOut") LocalDate checkOut);
    
    @Query("SELECT b FROM Booking b WHERE b.property.owner = :owner")
    List<Booking> findByPropertyOwner(@Param("owner") User owner);
}