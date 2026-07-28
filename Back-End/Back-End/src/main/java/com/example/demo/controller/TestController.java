package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    
    @GetMapping("/connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Frontend-Backend connection working!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/data")
    public ResponseEntity<Map<String, Object>> testDataSubmission(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Data received successfully");
        response.put("receivedData", data);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}