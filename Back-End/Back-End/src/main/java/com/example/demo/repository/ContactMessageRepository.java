package com.example.demo.repository;

import com.example.demo.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    
    List<ContactMessage> findByStatusOrderByCreatedAtDesc(ContactMessage.Status status);
    
    List<ContactMessage> findByEmailOrderByCreatedAtDesc(String email);
}