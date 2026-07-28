package com.example.demo.dto;

import com.example.demo.entity.Booking;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingCreateDto {
    
    @NotNull(message = "Property ID is required")
    private Long propertyId;
    
    @NotNull(message = "Check-in date is required")
    private LocalDate checkInDate;
    
    @NotNull(message = "Check-out date is required")
    private LocalDate checkOutDate;
    
    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "At least 1 guest is required")
    private Integer guests = 2; // Default to 2 guests
    
    @NotNull(message = "Payment method is required")
    private Booking.PaymentMethod paymentMethod;
    
    @NotBlank(message = "User email is required")
    private String userEmail;
    
    // Additional fields for frontend compatibility
    private String checkIn; // Formatted date string
    private String checkOut; // Formatted date string
    private Integer nights;
}