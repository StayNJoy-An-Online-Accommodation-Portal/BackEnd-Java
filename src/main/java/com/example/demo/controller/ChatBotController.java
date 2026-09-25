package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "ChatBot", description = "AI chatbot assistance")
public class ChatBotController {
    
    @PostMapping("/message")
    @Operation(summary = "Send message to chatbot")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendMessage(@RequestParam String message) {
        String response = getResponse(message.toLowerCase());
        
        Map<String, String> chatResponse = Map.of(
            "message", response,
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        
        return ResponseEntity.ok(ApiResponse.success(chatResponse));
    }
    
    private String getResponse(String input) {
        if (input.contains("book") || input.contains("reservation")) {
            return "To make a booking, browse our available rooms and click 'Reserve'. You'll need to provide check-in/out dates and guest details.";
        }
        if (input.contains("cancel")) {
            return "You can cancel bookings up to 48 hours before check-in for a full refund. Go to 'My Bookings' to manage your reservations.";
        }
        if (input.contains("payment") || input.contains("pay")) {
            return "We accept all major credit cards, PayPal, and UPI. Payments are processed securely and you'll receive confirmation within minutes.";
        }
        if (input.contains("refund")) {
            return "Refunds are processed within 5-7 business days. Cancellations made 48+ hours before check-in receive full refunds.";
        }
        if (input.contains("contact") || input.contains("support")) {
            return "Our support team is available 24/7 at support@stayease.com or through our help center. You can also use this chat for immediate assistance.";
        }
        if (input.contains("check") && (input.contains("in") || input.contains("out"))) {
            return "Standard check-in is 3:00 PM and check-out is 11:00 AM. Contact your host for early/late arrangements.";
        }
        if (input.contains("price") || input.contains("cost")) {
            return "Room prices vary by location, dates, and amenities. Use our search filters to find options within your budget.";
        }
        if (input.contains("hello") || input.contains("hi") || input.contains("hey")) {
            return "Hello! I'm here to help with your booking questions. What can I assist you with today?";
        }
        
        return "I'm here to help with bookings, payments, cancellations, and general questions. Could you please be more specific about what you need help with?";
    }
}