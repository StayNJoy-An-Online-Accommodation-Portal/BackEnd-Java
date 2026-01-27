package com.example.demo.service;

import com.example.demo.entity.Complaint;
import com.example.demo.entity.Property;
import com.example.demo.entity.User;
import com.example.demo.repository.ComplaintRepository;
import com.example.demo.repository.PropertyRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {
    
    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    public Complaint createComplaint(String userEmail, Long propertyId, String issue) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Property property = propertyRepository.findById(propertyId)
            .orElseThrow(() -> new RuntimeException("Property not found"));
        
        Complaint complaint = new Complaint(user, property, issue);
        return complaintRepository.save(complaint);
    }
    
    public List<Complaint> getUserComplaints(String userEmail) {
        return complaintRepository.findByUserEmailOrderByCreatedAtDesc(userEmail);
    }
    
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public Complaint updateComplaintStatus(Long complaintId, Complaint.ComplaintStatus status, String adminResponse) {
        Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        complaint.setStatus(status);
        if (adminResponse != null) {
            complaint.setAdminResponse(adminResponse);
        }
        
        if (status == Complaint.ComplaintStatus.RESOLVED || status == Complaint.ComplaintStatus.CLOSED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }
        
        return complaintRepository.save(complaint);
    }
}