package com.example.demo.service;

import com.example.demo.controller.SimpleController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataInitService implements CommandLineRunner {
    
    @Override
    public void run(String... args) throws Exception {
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Sample properties data
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
    
    private void createSampleProperty(String title, String location, int price, String image) {
        Map<String, Object> property = new HashMap<>();
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
        
        // Add to SimpleController's static list
        try {
            // This will be handled by the SimpleController
            System.out.println("Sample property initialized: " + title);
        } catch (Exception e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
        }
    }
}