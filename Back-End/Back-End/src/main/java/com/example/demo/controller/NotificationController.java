package com.example.demo.controller;

import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUserNotifications(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Notification notification : notifications) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", notification.getId());
            map.put("type", notification.getType());
            map.put("title", notification.getTitle());
            map.put("message", notification.getMessage());
            map.put("read", notification.getIsRead());
            map.put("timestamp", notification.getCreatedAt().toString());
            result.add(map);
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PatchMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
        return ResponseEntity.ok("Notification marked as read");
    }
}