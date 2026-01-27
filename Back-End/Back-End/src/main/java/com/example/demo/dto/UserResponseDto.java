package com.example.demo.dto;

import com.example.demo.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private User.Role role;
    private User.Gender gender;
    private Integer age;
    private User.UserStatus status;
    private LocalDateTime createdAt;
}