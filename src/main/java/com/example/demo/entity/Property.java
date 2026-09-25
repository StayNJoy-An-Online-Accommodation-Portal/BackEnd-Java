package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.BatchSize;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal earnAmount = BigDecimal.ZERO;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Integer maxGuests;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    @CollectionTable(name = "property_amenities", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "amenity")
    private List<String> amenities;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    @CollectionTable(name = "property_images", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "image_url", columnDefinition = "LONGTEXT")
    private List<String> images;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private User owner;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status = PropertyStatus.PENDING;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;
    
    @Column
    private Float averageRating = 0.0f;
    
    @Column(nullable = false)
    private Integer reviewCount = 0;
    
    @Column(nullable = false)
    private Integer bookingCount = 0;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum PropertyStatus {
        PENDING, APPROVED, REJECTED
    }
}