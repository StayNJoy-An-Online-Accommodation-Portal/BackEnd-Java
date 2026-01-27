package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.deleted = false")
    Optional<User> findByEmail(String email);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = ?1 AND u.deleted = false")
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.role = ?1 AND u.deleted = false")
    List<User> findByRole(User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.status = ?1 AND u.deleted = false")
    List<User> findByStatus(User.UserStatus status);
    
    @Query("SELECT u FROM User u WHERE u.deleted = false")
    List<User> findAllActive();
}