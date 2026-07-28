package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GuestUpdateDto {
    
    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "At least 1 guest is required")
    private Integer guests;
}