package com.example.demo.controller;

import com.example.demo.dto.ReviewCreateDto;
import com.example.demo.dto.ReviewResponseDto;
import com.example.demo.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Review Management", description = "APIs for property reviews and ratings")
public class ReviewController {
    
    private final ReviewService reviewService;
    
    @PostMapping
    @Operation(summary = "Create a new review")
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewCreateDto createDto) {
        ReviewResponseDto review = reviewService.createReview(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }
    
    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Get reviews for a property")
    public ResponseEntity<List<ReviewResponseDto>> getPropertyReviews(@PathVariable Long propertyId) {
        List<ReviewResponseDto> reviews = reviewService.getPropertyReviews(propertyId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/user/{userEmail}")
    @Operation(summary = "Get reviews by user")
    public ResponseEntity<List<ReviewResponseDto>> getUserReviews(@PathVariable String userEmail) {
        List<ReviewResponseDto> reviews = reviewService.getUserReviews(userEmail);
        return ResponseEntity.ok(reviews);
    }
}