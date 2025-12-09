package com.bank.se3bank.notifications.model;

import com.bank.se3bank.users.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "message", nullable = false, length = 1000)
    private String message;
    
    @Column(name = "notification_type", nullable = false)
    private String type; // TRANSACTION, SECURITY, ACCOUNT, MARKETING
    
    @Column(name = "channel", nullable = false)
    private String channel; // EMAIL, SMS, IN_APP, PUSH
    
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;
    
    @Column(name = "is_sent", nullable = false)
    @Builder.Default
    private Boolean isSent = false;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON for additional data
    
    // رابط بالمعاملة (اختياري)
    @Column(name = "transaction_id")
    private String transactionId;
    
    public void markAsSent() {
        this.isSent = true;
        this.sentAt = LocalDateTime.now();
    }
    
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}