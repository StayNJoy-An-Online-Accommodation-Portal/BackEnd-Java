package com.example.demo.repository;

import com.example.demo.entity.Property;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("SELECT DISTINCT p FROM Property p LEFT JOIN FETCH p.owner")
    List<Property> findAllWithDetails();

    @Query("SELECT DISTINCT p FROM Property p LEFT JOIN FETCH p.owner WHERE p.status = :status")
    List<Property> findByStatusWithDetails(@Param("status") Property.PropertyStatus status);

    List<Property> findByStatus(Property.PropertyStatus status);

    List<Property> findByOwner(User owner);

    List<Property> findByLocationContainingIgnoreCase(String location);

    @Query("SELECT DISTINCT p FROM Property p " +
           "LEFT JOIN FETCH p.owner " +
           "WHERE p.status = 'APPROVED' AND " +
           "(:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minPrice IS NULL OR p.pricePerNight >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.pricePerNight <= :maxPrice) AND " +
           "(:maxGuests IS NULL OR p.maxGuests >= :maxGuests)")
    List<Property> findAvailableProperties(@Param("location") String location,
                                           @Param("minPrice") BigDecimal minPrice,
                                           @Param("maxPrice") BigDecimal maxPrice,
                                           @Param("maxGuests") Integer maxGuests);
}