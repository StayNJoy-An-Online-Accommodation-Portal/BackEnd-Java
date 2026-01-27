package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Booking;
import com.example.demo.entity.Property;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyService {
    
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;
    
    @Transactional
    public PropertyResponseDto updateProperty(Long id, PropertyCreateDto updateDto, String ownerEmail) {
        System.out.println("Updating property with ID: " + id);
        System.out.println("Update data: " + updateDto);
        System.out.println("Owner email: " + ownerEmail);
        
        Property property = propertyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        if (!property.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Unauthorized to update this property");
        }
        
        // Validate required fields
        if (updateDto.getTitle() == null || updateDto.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Title is required");
        }
        if (updateDto.getDescription() == null || updateDto.getDescription().trim().isEmpty()) {
            throw new RuntimeException("Description is required");
        }
        if (updateDto.getMaxGuests() == null || updateDto.getMaxGuests() < 1) {
            throw new RuntimeException("Max guests must be at least 1");
        }
        
        // Validate and convert data types
        BigDecimal pricePerNight;
        try {
            if (updateDto.getPricePerNight() instanceof Number) {
                pricePerNight = BigDecimal.valueOf(((Number) updateDto.getPricePerNight()).doubleValue());
            } else {
                pricePerNight = new BigDecimal(updateDto.getPricePerNight().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid price format");
        }
        
        if (pricePerNight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Price per night must be greater than 0");
        }
        
        // Update fields
        property.setTitle(updateDto.getTitle().trim());
        property.setDescription(updateDto.getDescription().trim());
        property.setPricePerNight(pricePerNight);
        property.setMaxGuests(updateDto.getMaxGuests());
        property.setAmenities(updateDto.getAmenities());
        
        if (updateDto.getImages() != null && !updateDto.getImages().isEmpty()) {
            // Store actual base64 images instead of placeholders
            property.setImages(new ArrayList<>(updateDto.getImages()));
        }
        
        // Reset status to pending for admin approval
        property.setStatus(Property.PropertyStatus.PENDING);
        
        Property savedProperty = propertyRepository.saveAndFlush(property);
        System.out.println("Property updated successfully: " + savedProperty.getId());
        return mapToResponseDto(savedProperty);
    }
    
    @Transactional
    public PropertyResponseDto createProperty(PropertyCreateDto createDto, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        
        if (owner.getRole() != User.Role.PROPERTY_OWNER && owner.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Only property owners can create properties");
        }
        
        Property property = new Property();
        property.setTitle(createDto.getTitle());
        property.setLocation(createDto.getLocation());
        property.setPricePerNight(createDto.getPricePerNight());
        property.setDescription(createDto.getDescription());
        property.setMaxGuests(createDto.getMaxGuests());
        property.setOwner(owner);
        
        // Set amenities
        if (createDto.getAmenities() != null) {
            property.setAmenities(createDto.getAmenities());
        }
        
        // Handle images - store actual base64 images
        if (createDto.getImages() != null && !createDto.getImages().isEmpty()) {
            property.setImages(new ArrayList<>(createDto.getImages()));
        } else {
            property.setImages(Arrays.asList("https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=400"));
        }
        
        Property savedProperty = propertyRepository.saveAndFlush(property);
        return mapToResponseDto(savedProperty);
    }
    
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> getAllProperties() {
        return propertyRepository.findAll().stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> getApprovedProperties() {
        return propertyRepository.findByStatus(Property.PropertyStatus.APPROVED).stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> searchProperties(String location, BigDecimal minPrice, 
                                                    BigDecimal maxPrice, Integer maxGuests) {
        return propertyRepository.findAvailableProperties(location, minPrice, maxPrice, maxGuests)
            .stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PropertyResponseDto getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        return mapToResponseDto(property);
    }
    
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> getPropertiesByOwner(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        
        return propertyRepository.findByOwner(owner).stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }
    
    public PropertyResponseDto updatePropertyStatus(Long id, Property.PropertyStatus status) {
        Property property = propertyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        property.setStatus(status);
        Property savedProperty = propertyRepository.save(property);
        return mapToResponseDto(savedProperty);
    }
    
    public void deleteProperty(Long id, String ownerEmail) {
        Property property = propertyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        
        // Check ownership or admin role
        User user = userRepository.findByEmail(ownerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!property.getOwner().getEmail().equals(ownerEmail) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Unauthorized to delete this property");
        }
        
        // Check for active bookings
        List<Booking> activeBookings = bookingRepository.findByProperty(property)
            .stream()
            .filter(booking -> booking.getStatus() == Booking.BookingStatus.CONFIRMED || 
                             booking.getStatus() == Booking.BookingStatus.PENDING)
            .collect(Collectors.toList());
        
        if (!activeBookings.isEmpty()) {
            throw new RuntimeException("Cannot delete property with active bookings. Cancel all bookings first.");
        }
        
        // Delete all completed/cancelled bookings for this property
        List<Booking> allBookings = bookingRepository.findByProperty(property);
        bookingRepository.deleteAll(allBookings);
        
        propertyRepository.deleteById(id);
    }
    
    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Property not found");
        }
        propertyRepository.deleteById(id);
    }
    
    private PropertyResponseDto mapToResponseDto(Property property) {
        PropertyResponseDto dto = modelMapper.map(property, PropertyResponseDto.class);
        dto.setOwnerEmail(property.getOwner().getEmail());
        dto.setOwnerName(property.getOwner().getName());
        
        // Add default image if no images exist
        if (dto.getImages() == null || dto.getImages().isEmpty()) {
            dto.setImages(List.of("https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=400"));
        }
        
        // Add compatibility fields for frontend
        dto.setRating(property.getRating() != null ? property.getRating() : BigDecimal.valueOf(4.5));
        
        return dto;
    }
}