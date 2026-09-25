package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PropertyCreateDto {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal pricePerNight;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Max guests is required")
    @Min(value = 1, message = "Max guests must be at least 1")
    private Integer maxGuests;
    
    private List<String> amenities;
    private List<String> images;
}