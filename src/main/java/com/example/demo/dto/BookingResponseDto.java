package com.example.demo.dto;

import com.example.demo.entity.Booking;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingResponseDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long propertyId;
    private String propertyTitle;
    private String propertyLocation;
    private String propertyImage;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String checkIn; // Formatted date for frontend
    private String checkOut; // Formatted date for frontend
    private Integer guests;
    private Integer nights;
    private BigDecimal pricePerNight;
    private BigDecimal subtotal;
    private BigDecimal cleaningFee;
    private BigDecimal serviceFee;
    private BigDecimal totalAmount;
    private Booking.BookingStatus status;
    private Booking.PaymentMethod paymentMethod;
    private Booking.PaymentStatus paymentStatus;
    private String paymentTransactionId;
    private LocalDateTime createdAt;
}