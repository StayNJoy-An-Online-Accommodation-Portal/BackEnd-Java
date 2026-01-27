package com.example.demo.repository;

import com.example.demo.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<Complaint> findAllByOrderByCreatedAtDesc();
}