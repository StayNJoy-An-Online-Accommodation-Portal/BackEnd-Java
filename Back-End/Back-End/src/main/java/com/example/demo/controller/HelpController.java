package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.ContactMessage;
import com.example.demo.repository.ContactMessageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/help")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Help Center", description = "Help and support functionality")
public class HelpController {
    
    private final ContactMessageRepository contactMessageRepository;
    
    @GetMapping("/faq")
    @Operation(summary = "Get frequently asked questions")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getFAQ() {
        List<Map<String, String>> faqs = List.of(
            Map.of("question", "How do I make a booking?", 
                   "answer", "Browse available rooms, select your dates, and click 'Reserve'. You'll be guided through the booking process."),
            Map.of("question", "What is the cancellation policy?", 
                   "answer", "Free cancellation up to 48 hours before check-in. Cancellations within 48 hours are non-refundable."),
            Map.of("question", "How do I contact support?", 
                   "answer", "Use our chat bot, email support@stayease.com, or call our 24/7 helpline."),
            Map.of("question", "What payment methods are accepted?", 
                   "answer", "We accept all major credit cards, PayPal, and UPI payments."),
            Map.of("question", "How do I become a property owner?", 
                   "answer", "Sign up with a property owner account and start listing your properties for approval.")
        );
        
        return ResponseEntity.ok(ApiResponse.success(faqs));
    }
    
    @PostMapping("/contact")
    @Operation(summary = "Submit contact form")
    public ResponseEntity<ApiResponse<String>> submitContact(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String message) {
        
        ContactMessage contactMessage = new ContactMessage();
        contactMessage.setName(name);
        contactMessage.setEmail(email);
        contactMessage.setSubject(subject);
        contactMessage.setMessage(message);
        
        contactMessageRepository.save(contactMessage);
        
        return ResponseEntity.ok(ApiResponse.success("Your message has been sent successfully. We'll get back to you within 24 hours."));
    }
}