package com.example.demo.controller;

import com.example.demo.dto.PropertyResponseDto;
import com.example.demo.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Search", description = "Search functionality for properties")
public class SearchController {
    
    private final PropertyService propertyService;
    
    @GetMapping("/location")
    @Operation(summary = "Search properties by location")
    public ResponseEntity<List<PropertyResponseDto>> searchByLocation(@RequestParam String query) {
        List<PropertyResponseDto> properties = propertyService.searchProperties(query, null, null, null);
        return ResponseEntity.ok(properties);
    }
    
    @GetMapping("/clear")
    @Operation(summary = "Get all approved properties (clear search)")
    public ResponseEntity<List<PropertyResponseDto>> clearSearch() {
        List<PropertyResponseDto> properties = propertyService.getApprovedProperties();
        return ResponseEntity.ok(properties);
    }
}