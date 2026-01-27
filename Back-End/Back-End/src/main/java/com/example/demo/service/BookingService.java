package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Property;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.BookingConflictException;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;
    
    private final DateTimeFormatter FRONTEND_DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM");
    
    public BookingResponseDto createBooking(BookingCreateDto createDto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check if user is blocked
        if (user.getStatus() == User.UserStatus.BLOCKED) {
            throw new RuntimeException("Your account is blocked. You cannot make bookings.");
        }
        
        Property property = propertyRepository.findById(createDto.getPropertyId())
            .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        if (property.getStatus() != Property.PropertyStatus.APPROVED) {
            throw new RuntimeException("Property is not available for booking");
        }
        
        // Validate dates
        if (createDto.getCheckOutDate().isBefore(createDto.getCheckInDate()) || 
            createDto.getCheckOutDate().isEqual(createDto.getCheckInDate())) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }
        
        // Check guest capacity
        if (createDto.getGuests() > property.getMaxGuests()) {
            throw new RuntimeException("Number of guests exceeds property capacity");
        }
        
        // Check for booking conflicts
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
            property, createDto.getCheckInDate(), createDto.getCheckOutDate());
        
        if (!conflicts.isEmpty()) {
            throw new BookingConflictException("Property is not available for selected dates");
        }
        
        // Calculate pricing (matching frontend logic)
        int nights = (int) ChronoUnit.DAYS.between(createDto.getCheckInDate(), createDto.getCheckOutDate());
        BigDecimal subtotal = property.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        BigDecimal cleaningFee = subtotal.multiply(BigDecimal.valueOf(0.1));
        BigDecimal serviceFee = subtotal.multiply(BigDecimal.valueOf(0.08));
        BigDecimal totalAmount = subtotal.add(cleaningFee).add(serviceFee);
        
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setProperty(property);
        booking.setCheckInDate(createDto.getCheckInDate());
        booking.setCheckOutDate(createDto.getCheckOutDate());
        booking.setGuests(createDto.getGuests());
        booking.setNights(nights);
        booking.setSubtotal(subtotal);
        booking.setCleaningFee(cleaningFee);
        booking.setServiceFee(serviceFee);
        booking.setTotalAmount(totalAmount);
        booking.setPaymentMethod(createDto.getPaymentMethod());
        booking.setPaymentTransactionId(UUID.randomUUID().toString());
        booking.setPaymentStatus(Booking.PaymentStatus.COMPLETED);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Update property earnings and booking count
        property.setEarnAmount(property.getEarnAmount().add(totalAmount));
        property.setBookingCount(property.getBookingCount() + 1);
        propertyRepository.save(property);
        
        // Create notifications
        notificationService.createBookingConfirmationNotification(user, property.getTitle());
        notificationService.createPaymentSuccessNotification(user, totalAmount.toString());
        
        return mapToResponseDto(savedBooking);
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getUserBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return bookingRepository.findByUser(user).stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getOwnerBookings(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        
        return bookingRepository.findByPropertyOwner(owner).stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookings() {
        return bookingRepository.findAll().stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        return mapToResponseDto(booking);
    }
    
    public BookingResponseDto updateBookingGuests(Long id, Integer guests, String userEmail) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized to update this booking");
        }
        
        if (guests > booking.getProperty().getMaxGuests()) {
            throw new RuntimeException("Number of guests exceeds property capacity");
        }
        
        booking.setGuests(guests);
        Booking savedBooking = bookingRepository.save(booking);
        return mapToResponseDto(savedBooking);
    }
    
    public BookingResponseDto cancelBooking(Long id, String userEmail) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        if (!booking.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }
        
        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }
        
        // Check if cancellation is allowed (48 hours before check-in)
        LocalDate now = LocalDate.now();
        LocalDate cancellationDeadline = booking.getCheckInDate().minusDays(2);
        if (now.isAfter(cancellationDeadline)) {
            throw new RuntimeException("Cancellation not allowed less than 48 hours before check-in");
        }
        
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setPaymentStatus(Booking.PaymentStatus.REFUNDED);
        
        // Subtract the cancelled booking amount from property earnings
        Property property = booking.getProperty();
        property.setEarnAmount(property.getEarnAmount().subtract(booking.getTotalAmount()));
        property.setBookingCount(property.getBookingCount() - 1);
        propertyRepository.save(property);
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Create cancellation notification
        notificationService.createBookingCancellationNotification(booking.getUser(), booking.getProperty().getTitle());
        
        return mapToResponseDto(savedBooking);
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponseDto> checkAvailability(Long propertyId, LocalDate checkIn, LocalDate checkOut) {
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        return bookingRepository.findConflictingBookings(property, checkIn, checkOut)
            .stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    private BookingResponseDto mapToResponseDto(Booking booking) {
        BookingResponseDto dto = modelMapper.map(booking, BookingResponseDto.class);
        dto.setUserId(booking.getUser().getId());
        dto.setUserName(booking.getUser().getName());
        dto.setUserEmail(booking.getUser().getEmail());
        dto.setPropertyId(booking.getProperty().getId());
        dto.setPropertyTitle(booking.getProperty().getTitle());
        dto.setPropertyLocation(booking.getProperty().getLocation());
        dto.setPricePerNight(booking.getProperty().getPricePerNight());
        
        // Set property image
        if (booking.getProperty().getImages() != null && !booking.getProperty().getImages().isEmpty()) {
            dto.setPropertyImage(booking.getProperty().getImages().get(0));
        }
        
        // Format dates for frontend compatibility
        dto.setCheckIn(booking.getCheckInDate().format(FRONTEND_DATE_FORMAT));
        dto.setCheckOut(booking.getCheckOutDate().format(FRONTEND_DATE_FORMAT));
        
        return dto;
    }
}