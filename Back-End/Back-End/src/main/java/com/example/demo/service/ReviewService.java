package com.example.demo.service;

import com.example.demo.dto.ReviewCreateDto;
import com.example.demo.dto.ReviewResponseDto;
import com.example.demo.entity.Review;
import com.example.demo.entity.Property;
import com.example.demo.entity.User;
import com.example.demo.entity.Booking;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper modelMapper;
    
    public ReviewResponseDto createReview(ReviewCreateDto createDto) {
        User user = userRepository.findByEmail(createDto.getUserEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Property property = propertyRepository.findById(createDto.getPropertyId())
            .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        Booking booking = bookingRepository.findById(createDto.getBookingId())
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        // Check if user has already reviewed this booking
        if (reviewRepository.findByBookingId(createDto.getBookingId()).isPresent()) {
            throw new RuntimeException("You have already reviewed this booking");
        }
        
        // Check if booking belongs to the user
        if (!booking.getUser().equals(user)) {
            throw new RuntimeException("You can only review your own bookings");
        }
        
        // Check if booking is completed
        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new RuntimeException("You can only review completed bookings");
        }
        
        Review review = new Review();
        review.setUser(user);
        review.setProperty(property);
        review.setBooking(booking);
        review.setRating(createDto.getRating());
        review.setComment(createDto.getComment());
        
        Review savedReview = reviewRepository.save(review);
        
        // Update property rating
        updatePropertyRating(property);
        
        return mapToResponseDto(savedReview);
    }
    
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getPropertyReviews(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        return reviewRepository.findByPropertyOrderByCreatedAtDesc(property)
            .stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getUserReviews(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return reviewRepository.findByUserOrderByCreatedAtDesc(user)
            .stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    private void updatePropertyRating(Property property) {
        Double avgRating = reviewRepository.getAverageRatingByProperty(property);
        Long reviewCount = reviewRepository.getReviewCountByProperty(property);
        
        property.setAverageRating(avgRating != null ? avgRating.floatValue() : 0.0f);
        property.setReviewCount(reviewCount != null ? reviewCount.intValue() : 0);
        
        propertyRepository.save(property);
    }
    
    private ReviewResponseDto mapToResponseDto(Review review) {
        ReviewResponseDto dto = modelMapper.map(review, ReviewResponseDto.class);
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getName());
        dto.setUserEmail(review.getUser().getEmail());
        dto.setPropertyId(review.getProperty().getId());
        dto.setPropertyTitle(review.getProperty().getTitle());
        dto.setBookingId(review.getBooking().getId());
        return dto;
    }
}