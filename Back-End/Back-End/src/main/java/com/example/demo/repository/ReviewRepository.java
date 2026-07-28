package com.example.demo.repository;

import com.example.demo.entity.Review;
import com.example.demo.entity.Property;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByPropertyOrderByCreatedAtDesc(Property property);
    
    List<Review> findByUserOrderByCreatedAtDesc(User user);
    
    Optional<Review> findByBookingId(Long bookingId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property = :property")
    Double getAverageRatingByProperty(@Param("property") Property property);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.property = :property")
    Long getReviewCountByProperty(@Param("property") Property property);
}