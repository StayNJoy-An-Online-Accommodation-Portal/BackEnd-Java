package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "APIs for user registration, authentication and management")
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        UserResponseDto user = userService.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Authenticate user login")
    public ResponseEntity<JwtResponseDto> loginUser(@Valid @RequestBody LoginDto loginDto) {
        JwtResponseDto response = userService.authenticateUser(loginDto);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Get all users (Admin only)")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PatchMapping("/{email}/password")
    @Operation(summary = "Change user password")
    public ResponseEntity<UserResponseDto> changePassword(
            @PathVariable String email,
            @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        UserResponseDto user = userService.changePassword(email, passwordChangeDto);
        return ResponseEntity.ok(user);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete user (Admin only)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle user status (Admin only)")
    public ResponseEntity<UserResponseDto> toggleUserStatus(@PathVariable Long id) {
        UserResponseDto user = userService.toggleUserStatus(id);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponseDto> getCurrentUserProfile(Authentication authentication) {
        String email = authentication.getName();
        UserResponseDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}