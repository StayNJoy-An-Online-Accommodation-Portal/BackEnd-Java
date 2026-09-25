package com.example.demo.service;

import com.example.demo.entity.Property;
import com.example.demo.entity.User;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataInitializationService implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeUsers();
        // Initialize properties only on first run (when database is empty)
        if (userRepository.count() <= 3) {
            initializeProperties();
        }
    }
    
    private void initializeUsers() {
        // Create test users matching frontend mock data
        if (!userRepository.existsByEmail("user@test.com")) {
            User user = new User();
            user.setEmail("user@test.com");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setName("John Doe");
            user.setRole(User.Role.USER);
            user.setGender(User.Gender.MALE);
            user.setAge(25);
            userRepository.save(user);
        }
        
        if (!userRepository.existsByEmail("admin@test.com")) {
            User admin = new User();
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setName("Admin User");
            admin.setRole(User.Role.ADMIN);
            admin.setGender(User.Gender.OTHER);
            admin.setAge(30);
            userRepository.save(admin);
        }
        
        if (!userRepository.existsByEmail("owner@test.com")) {
            User owner = new User();
            owner.setEmail("owner@test.com");
            owner.setPassword(passwordEncoder.encode("owner123"));
            owner.setName("Property Owner");
            owner.setRole(User.Role.PROPERTY_OWNER);
            owner.setGender(User.Gender.FEMALE);
            owner.setAge(35);
            userRepository.save(owner);
        }
    }
    
    private void initializeProperties() {
        User owner = userRepository.findByEmail("owner@test.com").orElse(null);
        User admin = userRepository.findByEmail("admin@test.com").orElse(null);
        User user = userRepository.findByEmail("user@test.com").orElse(null);
        
        if (owner != null) {
            // Property 1
            Property property1 = new Property();
            property1.setTitle("Room in Lonavala");
            property1.setLocation("Lonavala, Maharashtra");
            property1.setPricePerNight(new BigDecimal("4500"));
            property1.setDescription("Beautiful room with mountain views in Lonavala.");
            property1.setMaxGuests(4);
            property1.setAmenities(Arrays.asList("WiFi", "AC", "Kitchen"));
            property1.setImages(Arrays.asList("https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=400"));
            property1.setOwner(owner);
            property1.setStatus(Property.PropertyStatus.APPROVED);
            property1.setRating(new BigDecimal("4.5"));
            property1.setReviewCount(23);
            property1.setEarnAmount(new BigDecimal("18000"));
            property1.setBookingCount(4);
            propertyRepository.save(property1);
            
            // Property 2
            Property property2 = new Property();
            property2.setTitle("Beach Villa in Goa");
            property2.setLocation("Candolim, Goa");
            property2.setPricePerNight(new BigDecimal("8500"));
            property2.setDescription("Luxury beach villa with private pool and ocean views.");
            property2.setMaxGuests(6);
            property2.setAmenities(Arrays.asList("Pool", "WiFi", "AC", "Balcony"));
            property2.setImages(Arrays.asList("https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=400"));
            property2.setOwner(owner);
            property2.setStatus(Property.PropertyStatus.APPROVED);
            property2.setRating(new BigDecimal("4.8"));
            property2.setReviewCount(45);
            property2.setEarnAmount(new BigDecimal("42500"));
            property2.setBookingCount(5);
            propertyRepository.save(property2);
        }
        
        if (admin != null) {
            // Property 3
            Property property3 = new Property();
            property3.setTitle("Mountain Cottage");
            property3.setLocation("Manali, Himachal Pradesh");
            property3.setPricePerNight(new BigDecimal("3200"));
            property3.setDescription("Cozy mountain cottage perfect for couples.");
            property3.setMaxGuests(2);
            property3.setAmenities(Arrays.asList("WiFi", "Kitchen", "Parking"));
            property3.setImages(Arrays.asList("https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=400"));
            property3.setOwner(admin);
            property3.setStatus(Property.PropertyStatus.APPROVED);
            property3.setRating(new BigDecimal("4.6"));
            property3.setReviewCount(18);
            propertyRepository.save(property3);
            
            // Property 5
            Property property5 = new Property();
            property5.setTitle("Lake House");
            property5.setLocation("Udaipur, Rajasthan");
            property5.setPricePerNight(new BigDecimal("7200"));
            property5.setDescription("Beautiful lake house with stunning water views.");
            property5.setMaxGuests(8);
            property5.setAmenities(Arrays.asList("WiFi", "AC", "Balcony", "Kitchen"));
            property5.setImages(Arrays.asList("https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=400"));
            property5.setOwner(admin);
            property5.setStatus(Property.PropertyStatus.APPROVED);
            property5.setRating(new BigDecimal("4.7"));
            property5.setReviewCount(31);
            propertyRepository.save(property5);
        }
        
        if (user != null) {
            // Property 4
            Property property4 = new Property();
            property4.setTitle("City Apartment");
            property4.setLocation("Mumbai, Maharashtra");
            property4.setPricePerNight(new BigDecimal("6800"));
            property4.setDescription("Modern city apartment in the heart of Mumbai.");
            property4.setMaxGuests(3);
            property4.setAmenities(Arrays.asList("WiFi", "AC", "TV"));
            property4.setImages(Arrays.asList("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=400"));
            property4.setOwner(user);
            property4.setStatus(Property.PropertyStatus.APPROVED);
            property4.setRating(new BigDecimal("4.3"));
            property4.setReviewCount(12);
            propertyRepository.save(property4);
            
            // Property 6
            Property property6 = new Property();
            property6.setTitle("Desert Camp");
            property6.setLocation("Jaisalmer, Rajasthan");
            property6.setPricePerNight(new BigDecimal("5500"));
            property6.setDescription("Unique desert camping experience under the stars.");
            property6.setMaxGuests(4);
            property6.setAmenities(Arrays.asList("WiFi", "AC"));
            property6.setImages(Arrays.asList("https://images.unsplash.com/photo-1571003123894-1f0594d2b5d9?w=400"));
            property6.setOwner(user);
            property6.setStatus(Property.PropertyStatus.APPROVED);
            property6.setRating(new BigDecimal("4.4"));
            property6.setReviewCount(27);
            property6.setEarnAmount(new BigDecimal("16500"));
            property6.setBookingCount(3);
            propertyRepository.save(property6);
        }
    }
}