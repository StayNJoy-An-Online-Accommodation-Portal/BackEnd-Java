package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Property;
import com.example.demo.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Property Management", description = "APIs for property CRUD operations and search")
public class PropertyController {
    
    private final PropertyService propertyService;
    
    @PostMapping
    @Operation(summary = "Create a new property")
    public ResponseEntity<PropertyResponseDto> createProperty(
            @Valid @RequestBody PropertyCreateDto createDto,
            @RequestParam String ownerEmail) {
        PropertyResponseDto property = propertyService.createProperty(createDto, ownerEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(property);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a property")
    public ResponseEntity<PropertyResponseDto> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyCreateDto updateDto,
            @RequestParam String ownerEmail) {
        PropertyResponseDto property = propertyService.updateProperty(id, updateDto, ownerEmail);
        return ResponseEntity.ok(property);
    }
    
    @GetMapping
    @Operation(summary = "Get all properties")
    public ResponseEntity<List<PropertyResponseDto>> getAllProperties() {
        List<PropertyResponseDto> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }
    
    @GetMapping("/approved")
    @Operation(summary = "Get all approved properties with amenities")
    public ResponseEntity<List<PropertyResponseDto>> getApprovedProperties() {
        List<PropertyResponseDto> properties = propertyService.getApprovedProperties();
        return ResponseEntity.ok(properties);
    }
    
    @GetMapping("/rooms")
    @Operation(summary = "Get all rooms (alias for approved properties)")
    public ResponseEntity<List<PropertyResponseDto>> getAllRooms() {
        List<PropertyResponseDto> properties = propertyService.getApprovedProperties();
        return ResponseEntity.ok(properties);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search properties with filters")
    public ResponseEntity<List<PropertyResponseDto>> searchProperties(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer maxGuests) {
        List<PropertyResponseDto> properties = propertyService.searchProperties(location, minPrice, maxPrice, maxGuests);
        return ResponseEntity.ok(properties);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get property by ID")
    public ResponseEntity<PropertyResponseDto> getPropertyById(@PathVariable Long id) {
        PropertyResponseDto property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }
    
    @GetMapping("/owner/{ownerEmail}")
    @Operation(summary = "Get properties by owner")
    public ResponseEntity<List<PropertyResponseDto>> getPropertiesByOwner(@PathVariable String ownerEmail) {
        List<PropertyResponseDto> properties = propertyService.getPropertiesByOwner(ownerEmail);
        return ResponseEntity.ok(properties);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update property status (Admin only)")
    public ResponseEntity<PropertyResponseDto> updatePropertyStatus(
            @PathVariable Long id,
            @RequestParam Property.PropertyStatus status) {
        PropertyResponseDto property = propertyService.updatePropertyStatus(id, status);
        return ResponseEntity.ok(property);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete property")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}/owner")
    @Operation(summary = "Delete property by owner")
    public ResponseEntity<Void> deletePropertyByOwner(
            @PathVariable Long id,
            @RequestParam String ownerEmail) {
        propertyService.deleteProperty(id, ownerEmail);
        return ResponseEntity.noContent().build();
    }
}