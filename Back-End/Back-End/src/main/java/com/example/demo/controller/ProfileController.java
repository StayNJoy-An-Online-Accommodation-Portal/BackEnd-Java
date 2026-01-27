package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "User Profile", description = "User profile management")
public class ProfileController {
    
    private final UserService userService;
    
    @GetMapping("/{email}")
    @Operation(summary = "Get user profile by email")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable String email) {
        UserResponseDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/{email}")
    @Operation(summary = "Update user profile")
    public ResponseEntity<UserResponseDto> updateProfile(
            @PathVariable String email,
            @Valid @RequestBody UserRegistrationDto updateDto) {
        UserResponseDto user = userService.updateProfile(email, updateDto);
        return ResponseEntity.ok(user);
    }
}