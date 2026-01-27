package com.example.demo.controller;

import com.example.demo.entity.Complaint;
import com.example.demo.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:5173")
public class ComplaintController {
    
    @Autowired
    private ComplaintService complaintService;
    
    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@RequestBody Map<String, Object> request) {
        try {
            String userEmail = (String) request.get("userEmail");
            Long propertyId = Long.valueOf(request.get("propertyId").toString());
            String issue = (String) request.get("issue");
            
            Complaint complaint = complaintService.createComplaint(userEmail, propertyId, issue);
            return ResponseEntity.ok(complaint);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/user/{userEmail}")
    public ResponseEntity<List<Complaint>> getUserComplaints(@PathVariable String userEmail) {
        try {
            List<Complaint> complaints = complaintService.getUserComplaints(userEmail);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/admin/all")
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        try {
            List<Complaint> complaints = complaintService.getAllComplaints();
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{complaintId}/status")
    public ResponseEntity<Complaint> updateComplaintStatus(
            @PathVariable Long complaintId,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            String adminResponse = request.get("adminResponse");
            
            Complaint.ComplaintStatus complaintStatus = Complaint.ComplaintStatus.valueOf(status);
            Complaint updatedComplaint = complaintService.updateComplaintStatus(complaintId, complaintStatus, adminResponse);
            
            return ResponseEntity.ok(updatedComplaint);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}