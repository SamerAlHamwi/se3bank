// 📁 src/main/java/com/bank/se3bank/notifications/repository/NotificationRepository.java
package com.bank.se3bank.notifications.repository;

import com.bank.se3bank.notifications.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    List<Notification> findByUserIdAndChannelOrderByCreatedAtDesc(Long userId, String channel);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt >= :since")
    List<Notification> findByUserIdAndCreatedAtAfter(
            @Param("userId") Long userId, 
            @Param("since") LocalDateTime since);
    
    long countByUserId(Long userId);
    
    long countByUserIdAndIsReadFalse(Long userId);
    
    long countByUserIdAndCreatedAtAfter(Long userId, LocalDateTime since);
    
    void deleteByUserId(Long userId);
    
    @Query("SELECT n FROM Notification n WHERE " +
           "n.user.id = :userId AND " +
           "LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(n.message) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY n.createdAt DESC")
    List<Notification> searchByKeyword(
            @Param("userId") Long userId, 
            @Param("keyword") String keyword);
}