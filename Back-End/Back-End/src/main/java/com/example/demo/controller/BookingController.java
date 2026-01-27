package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Booking Management", description = "APIs for booking operations and management")
public class BookingController {
    
    private final BookingService bookingService;
    
    @PostMapping
    @Operation(summary = "Create a new booking")
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingCreateDto createDto) {
        String userEmail = createDto.getUserEmail(); // Get email from request body
        BookingResponseDto booking = bookingService.createBooking(createDto, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }
    
    @GetMapping
    @Operation(summary = "Get all bookings (Admin only)")
    public ResponseEntity<List<BookingResponseDto>> getAllBookings() {
        List<BookingResponseDto> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long id) {
        BookingResponseDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }
    
    @GetMapping("/user/{userEmail}")
    @Operation(summary = "Get bookings by user")
    public ResponseEntity<List<BookingResponseDto>> getUserBookings(@PathVariable String userEmail) {
        List<BookingResponseDto> bookings = bookingService.getUserBookings(userEmail);
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/owner/{ownerEmail}")
    @Operation(summary = "Get bookings for property owner")
    public ResponseEntity<List<BookingResponseDto>> getOwnerBookings(@PathVariable String ownerEmail) {
        List<BookingResponseDto> bookings = bookingService.getOwnerBookings(ownerEmail);
        return ResponseEntity.ok(bookings);
    }
    
    @PutMapping("/{id}/guests")
    @Operation(summary = "Update booking guest count")
    public ResponseEntity<BookingResponseDto> updateBookingGuests(
            @PathVariable Long id,
            @Valid @RequestBody GuestUpdateDto guestUpdate,
            @RequestParam String userEmail) {
        BookingResponseDto booking = bookingService.updateBookingGuests(id, guestUpdate.getGuests(), userEmail);
        return ResponseEntity.ok(booking);
    }
    
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking")
    public ResponseEntity<BookingResponseDto> cancelBooking(
            @PathVariable Long id,
            @RequestParam String userEmail) {
        BookingResponseDto booking = bookingService.cancelBooking(id, userEmail);
        return ResponseEntity.ok(booking);
    }
    
    @GetMapping("/availability/{propertyId}")
    @Operation(summary = "Check property availability for given dates")
    public ResponseEntity<List<BookingResponseDto>> checkAvailability(
            @PathVariable Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        List<BookingResponseDto> conflictingBookings = bookingService.checkAvailability(propertyId, checkIn, checkOut);
        return ResponseEntity.ok(conflictingBookings);
    }
}