package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/simple")
@CrossOrigin(origins = "*")
public class SimpleController {
    
    private static List<Map<String, Object>> properties = new ArrayList<>();
    private static List<Map<String, Object>> bookings = new ArrayList<>();
    private static Long propertyIdCounter = 1L;
    private static Long bookingIdCounter = 1L;
    
    static {
        // Initialize with sample data
        initializeSampleData();
    }
    
    private static void initializeSampleData() {
        createSampleProperty("Room in Lonavala", "Lonavala, Maharashtra", 4500, 
            "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=400&h=300&fit=crop&crop=center");
        
        createSampleProperty("Beach Villa in Goa", "Candolim, Goa", 8500,
            "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=400&h=300&fit=crop&crop=center");
        
        createSampleProperty("Mountain Cottage", "Manali, Himachal Pradesh", 3200,
            "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=400&h=300&fit=crop&crop=center");
        
        createSampleProperty("City Apartment", "Mumbai, Maharashtra", 6800,
            "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=400&h=300&fit=crop&crop=center");
        
        createSampleProperty("Lake House", "Udaipur, Rajasthan", 7200,
            "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=400&h=300&fit=crop&crop=center");
        
        createSampleProperty("Desert Camp", "Jaisalmer, Rajasthan", 5500,
            "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&h=300&fit=crop&crop=center");
    }
    
    private static void createSampleProperty(String title, String location, int price, String image) {
        Map<String, Object> property = new HashMap<>();
        property.put("id", propertyIdCounter++);
        property.put("title", title);
        property.put("location", location);
        property.put("price", price);
        property.put("pricePerNight", price);
        property.put("image", image);
        property.put("maxGuests", 4);
        property.put("rating", 4.5);
        property.put("reviews", 20);
        property.put("nights", 2);
        property.put("checkIn", "20 Jan");
        property.put("checkOut", "22 Jan");
        property.put("ownerEmail", "owner@test.com");
        property.put("amenities", new String[]{"WiFi", "AC", "Kitchen"});
        property.put("description", "Beautiful property with great amenities.");
        property.put("status", "APPROVED");
        properties.add(property);
    }
    
    @GetMapping("/properties")
    public ResponseEntity<List<Map<String, Object>>> getAllProperties() {
        return ResponseEntity.ok(properties);
    }
    
    @PostMapping("/properties")
    public ResponseEntity<Map<String, Object>> createProperty(@RequestBody Map<String, Object> property) {
        property.put("id", propertyIdCounter++);
        property.put("status", "APPROVED");
        properties.add(property);
        return ResponseEntity.ok(property);
    }
    
    @GetMapping("/bookings")
    public ResponseEntity<List<Map<String, Object>>> getAllBookings() {
        return ResponseEntity.ok(bookings);
    }
    
    @PostMapping("/bookings")
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody Map<String, Object> booking) {
        booking.put("id", bookingIdCounter++);
        booking.put("status", "CONFIRMED");
        bookings.add(booking);
        return ResponseEntity.ok(booking);
    }
    
    @DeleteMapping("/properties/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        properties.removeIf(p -> p.get("id").equals(id));
        System.out.println("Property deleted with ID: " + id);
        System.out.println("Remaining properties: " + properties.size());
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/properties/{id}")
    public ResponseEntity<Map<String, Object>> updateProperty(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        for (Map<String, Object> property : properties) {
            if (property.get("id").equals(id)) {
                property.putAll(updates);
                return ResponseEntity.ok(property);
            }
        }
        return ResponseEntity.notFound().build();
    }
}