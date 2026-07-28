package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponseDto {
    
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long propertyId;
    private String propertyTitle;
    private Long bookingId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}