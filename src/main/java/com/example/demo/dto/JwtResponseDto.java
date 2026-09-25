package com.example.demo.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private UserResponseDto user;
    
    public JwtResponseDto(String token, UserResponseDto user) {
        this.token = token;
        this.user = user;
    }
}