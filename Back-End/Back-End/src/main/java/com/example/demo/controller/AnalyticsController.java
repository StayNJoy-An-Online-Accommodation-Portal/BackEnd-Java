package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Property;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Analytics", description = "Property and booking analytics")
public class AnalyticsController {
    
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    
    @GetMapping("/property/{id}/performance")
    @Operation(summary = "Get property performance metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPropertyPerformance(@PathVariable Long id) {
        Property property = propertyRepository.findById(id).orElse(null);
        if (property == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> performance = Map.of(
            "totalEarnings", property.getEarnAmount(),
            "totalBookings", property.getBookingCount(),
            "averageRating", property.getRating(),
            "totalReviews", property.getReviewCount(),
            "occupancyRate", calculateOccupancyRate(property)
        );
        
        return ResponseEntity.ok(ApiResponse.success(performance));
    }
    
    @GetMapping("/owner/{email}/summary")
    @Operation(summary = "Get owner portfolio summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOwnerSummary(@PathVariable String email) {
        var owner = userRepository.findByEmail(email).orElse(null);
        if (owner == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<Property> properties = propertyRepository.findByOwner(owner);
        
        BigDecimal totalEarnings = properties.stream()
            .map(Property::getEarnAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int totalBookings = properties.stream()
            .mapToInt(Property::getBookingCount)
            .sum();
        
        Map<String, Object> summary = Map.of(
            "totalProperties", properties.size(),
            "totalEarnings", totalEarnings,
            "totalBookings", totalBookings,
            "averageRating", calculateAverageRating(properties),
            "topPerformingProperty", getTopPerformingProperty(properties)
        );
        
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
    
    private double calculateOccupancyRate(Property property) {
        // Mock calculation - in real implementation, calculate based on actual bookings vs available days
        return Math.min(95.0, property.getBookingCount() * 5.0);
    }
    
    private double calculateAverageRating(List<Property> properties) {
        return properties.stream()
            .filter(p -> p.getReviewCount() > 0)
            .mapToDouble(p -> p.getRating().doubleValue())
            .average()
            .orElse(0.0);
    }
    
    private Map<String, Object> getTopPerformingProperty(List<Property> properties) {
        Property top = properties.stream()
            .max((p1, p2) -> p1.getEarnAmount().compareTo(p2.getEarnAmount()))
            .orElse(null);
        
        if (top == null) {
            return Map.of();
        }
        
        return Map.of(
            "id", top.getId(),
            "title", top.getTitle(),
            "earnings", top.getEarnAmount()
        );
    }
}