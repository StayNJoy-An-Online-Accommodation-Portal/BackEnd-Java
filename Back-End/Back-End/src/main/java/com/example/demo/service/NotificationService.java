package com.example.demo.service;

import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    public void createBookingConfirmationNotification(User user, String propertyTitle) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType("booking_confirmed");
        notification.setTitle("Booking Confirmed");
        notification.setMessage("Your booking for " + propertyTitle + " has been confirmed");
        notificationRepository.save(notification);
    }
    
    public void createPaymentSuccessNotification(User user, String amount) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType("payment_success");
        notification.setTitle("Payment Successful");
        notification.setMessage("Payment of ₹" + amount + " has been processed successfully");
        notificationRepository.save(notification);
    }
    
    public void createBookingCancellationNotification(User user, String propertyTitle) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType("booking_cancelled");
        notification.setTitle("Booking Cancelled");
        notification.setMessage("Your booking for " + propertyTitle + " has been cancelled and refund is being processed");
        notificationRepository.save(notification);
    }
}