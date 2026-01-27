package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.repository.UserRepository;
import com.example.demo.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setName(registrationDto.getName()); // Uses computed property
        user.setRole(registrationDto.getRole());
        user.setGender(registrationDto.getGender());
        user.setAge(registrationDto.getAge());
        user.setMobile(registrationDto.getMobile());
        user.setStatus(User.UserStatus.ACTIVE);
        user.setDeleted(false);
        
        User savedUser = userRepository.save(user);
        System.out.println("User saved to database: " + savedUser.getEmail());
        return modelMapper.map(savedUser, UserResponseDto.class);
    }
    
    public JwtResponseDto authenticateUser(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));
        
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid email or password");
        }
        
        if (user.getStatus() == User.UserStatus.BLOCKED) {
            throw new RuntimeException("Account is blocked");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        UserResponseDto userResponse = modelMapper.map(user, UserResponseDto.class);
        
        return new JwtResponseDto(token, userResponse);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(user -> modelMapper.map(user, UserResponseDto.class))
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return modelMapper.map(user, UserResponseDto.class);
    }
    
    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return modelMapper.map(user, UserResponseDto.class);
    }
    
    public UserResponseDto updateProfile(String email, UserRegistrationDto updateDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Update only non-password fields
        user.setName(updateDto.getName());
        user.setGender(updateDto.getGender());
        user.setAge(updateDto.getAge());
        
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDto.class);
    }
    
    public UserResponseDto changePassword(String email, PasswordChangeDto passwordChangeDto) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDto.class);
    }
    
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setDeleted(true);
        user.setStatus(User.UserStatus.BLOCKED);
        userRepository.save(user);
    }
    
    public UserResponseDto toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setStatus(user.getStatus() == User.UserStatus.ACTIVE ? 
            User.UserStatus.BLOCKED : User.UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDto.class);
    }
}