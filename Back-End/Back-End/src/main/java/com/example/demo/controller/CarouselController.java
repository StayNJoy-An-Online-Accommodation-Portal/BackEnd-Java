package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Property;
import com.example.demo.repository.PropertyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carousel")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Carousel", description = "Homepage carousel management")
public class CarouselController {
    
    private final PropertyRepository propertyRepository;
    
    @GetMapping("/images")
    @Operation(summary = "Get carousel images for homepage")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getCarouselImages() {
        // Get top-rated properties from database for carousel
        List<Property> topProperties = propertyRepository.findByStatus(Property.PropertyStatus.APPROVED)
            .stream()
            .sorted((p1, p2) -> p2.getRating().compareTo(p1.getRating()))
            .limit(3)
            .toList();
        
        List<Map<String, String>> images = topProperties.stream()
            .map(property -> Map.of(
                "id", property.getId().toString(),
                "url", property.getImages().isEmpty() ? 
                    "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800" : 
                    property.getImages().get(0),
                "title", property.getTitle(),
                "description", property.getDescription().length() > 50 ? 
                    property.getDescription().substring(0, 50) + "..." : 
                    property.getDescription()
            ))
            .collect(java.util.stream.Collectors.toList());
        
        // Fallback if no properties
        if (images.isEmpty()) {
            images = List.of(
                Map.of("id", "1", 
                       "url", "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800",
                       "title", "Luxury Accommodations",
                       "description", "Experience comfort like never before")
            );
        }
        
        return ResponseEntity.ok(ApiResponse.success(images));
    }
}