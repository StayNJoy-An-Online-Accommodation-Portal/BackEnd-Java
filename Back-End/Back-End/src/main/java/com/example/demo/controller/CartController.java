package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Property;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PropertyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Booking Cart", description = "Shopping cart functionality for bookings")
public class CartController {
    
    private final PropertyRepository propertyRepository;
    
    @PostMapping("/calculate")
    @Operation(summary = "Calculate booking totals")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateBookingTotals(
            @RequestBody List<Map<String, Object>> bookingItems) {
        
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalNights = 0;
        int totalGuests = 0;
        
        for (Map<String, Object> item : bookingItems) {
            Long propertyId = Long.valueOf(item.get("propertyId").toString());
            Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
            
            int nights = Integer.parseInt(item.get("nights").toString());
            int guests = Integer.parseInt(item.get("guests").toString());
            
            BigDecimal itemTotal = property.getPricePerNight().multiply(BigDecimal.valueOf(nights));
            subtotal = subtotal.add(itemTotal);
            totalNights += nights;
            totalGuests += guests;
        }
        
        BigDecimal cleaningFee = subtotal.multiply(BigDecimal.valueOf(0.1));
        BigDecimal serviceFee = subtotal.multiply(BigDecimal.valueOf(0.08));
        BigDecimal taxes = subtotal.multiply(BigDecimal.valueOf(0.18));
        BigDecimal total = subtotal.add(cleaningFee).add(serviceFee).add(taxes);
        
        Map<String, Object> calculation = Map.of(
            "subtotal", subtotal,
            "cleaningFee", cleaningFee,
            "serviceFee", serviceFee,
            "taxes", taxes,
            "total", total,
            "totalNights", totalNights,
            "totalGuests", totalGuests
        );
        
        return ResponseEntity.ok(ApiResponse.success(calculation));
    }
}