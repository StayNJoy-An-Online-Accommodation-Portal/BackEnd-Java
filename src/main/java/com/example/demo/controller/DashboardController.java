package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Property;
import com.example.demo.entity.User;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Dashboard", description = "Dashboard statistics and summary data")
public class DashboardController {
    
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    
    @GetMapping("/admin/stats")
    @Operation(summary = "Get admin dashboard statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // User statistics
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findByStatus(User.UserStatus.ACTIVE).size();
        long propertyOwners = userRepository.findByRole(User.Role.PROPERTY_OWNER).size();
        
        // Property statistics
        long totalProperties = propertyRepository.count();
        long approvedProperties = propertyRepository.findByStatus(Property.PropertyStatus.APPROVED).size();
        long pendingProperties = propertyRepository.findByStatus(Property.PropertyStatus.PENDING).size();
        
        // Booking statistics
        long totalBookings = bookingRepository.count();
        long confirmedBookings = bookingRepository.findByStatus(Booking.BookingStatus.CONFIRMED).size();
        
        // Calculate total revenue
        BigDecimal totalRevenue = bookingRepository.findAll().stream()
            .filter(booking -> booking.getPaymentStatus() == Booking.PaymentStatus.COMPLETED)
            .map(Booking::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("users", Map.of(
            "total", totalUsers,
            "active", activeUsers,
            "propertyOwners", propertyOwners
        ));
        
        stats.put("properties", Map.of(
            "total", totalProperties,
            "approved", approvedProperties,
            "pending", pendingProperties
        ));
        
        stats.put("bookings", Map.of(
            "total", totalBookings,
            "confirmed", confirmedBookings
        ));
        
        stats.put("revenue", Map.of(
            "total", totalRevenue
        ));
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @GetMapping("/owner/stats")
    @Operation(summary = "Get property owner dashboard statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOwnerStats(@RequestParam String ownerEmail) {
        Map<String, Object> stats = new HashMap<>();
        
        User owner = userRepository.findByEmail(ownerEmail).orElse(null);
        if (owner == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Owner not found"));
        }
        
        // Property statistics
        long totalProperties = propertyRepository.findByOwner(owner).size();
        long approvedProperties = propertyRepository.findByOwner(owner).stream()
            .mapToLong(p -> p.getStatus() == Property.PropertyStatus.APPROVED ? 1 : 0)
            .sum();
        
        // Booking statistics for owner's properties
        long totalBookings = bookingRepository.findByPropertyOwner(owner).size();
        long confirmedBookings = bookingRepository.findByPropertyOwner(owner).stream()
            .mapToLong(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED ? 1 : 0)
            .sum();
        
        // Calculate earnings
        BigDecimal totalEarnings = bookingRepository.findByPropertyOwner(owner).stream()
            .filter(booking -> booking.getPaymentStatus() == Booking.PaymentStatus.COMPLETED)
            .map(Booking::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("properties", Map.of(
            "total", totalProperties,
            "approved", approvedProperties
        ));
        
        stats.put("bookings", Map.of(
            "total", totalBookings,
            "confirmed", confirmedBookings
        ));
        
        stats.put("earnings", Map.of(
            "total", totalEarnings
        ));
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}