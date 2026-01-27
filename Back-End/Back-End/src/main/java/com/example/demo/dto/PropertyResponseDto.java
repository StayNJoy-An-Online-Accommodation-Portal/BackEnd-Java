package com.example.demo.dto;

import com.example.demo.entity.Property;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PropertyResponseDto {
    private Long id;
    private String title;
    private String location;
    private BigDecimal pricePerNight;
    private String description;
    private Integer maxGuests;
    private List<String> amenities;
    private List<String> images;
    private String ownerEmail;
    private String ownerName;
    private Property.PropertyStatus status;
    private BigDecimal rating;
    private Integer reviewCount;
    private Integer bookingCount;
    private BigDecimal earnAmount;
    private LocalDateTime createdAt;
}