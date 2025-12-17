package com.bank.se3bank.transactions.repository;

import com.bank.se3bank.transactions.model.Transaction;
import com.bank.se3bank.shared.enums.TransactionStatus;
import com.bank.se3bank.shared.enums.TransactionType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByFromAccountIdOrToAccountId(Long fromAccountId, Long toAccountId);
    
    List<Transaction> findByStatus(TransactionStatus status);
    
    List<Transaction> findByTransactionType(TransactionType type);
    
    Optional<Transaction> findByTransactionId(String transactionId);
    
    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) " +
           "AND (cast(:startDate as timestamp) IS NULL OR t.createdAt >= :startDate) " +
           "AND (cast(:endDate as timestamp) IS NULL OR t.createdAt <= :endDate) " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findTransactionsByAccountAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE " +
           "t.toAccount.id = :accountId AND t.status = :status " +
           "AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    Double getTotalDeposits(@Param("accountId") Long accountId,
                           @Param("status") TransactionStatus status,
                           @Param("startDate") LocalDateTime startDate,
                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE " +
           "t.fromAccount.id = :accountId AND t.status = :status " +
           "AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    Double getTotalWithdrawals(@Param("accountId") Long accountId,
                              @Param("status") TransactionStatus status,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);
    
    // إضافة دوال جديدة مع معلمات Pageable
    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findRecentTransactionsByAccount(@Param("accountId") Long accountId, 
                                                     Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE " +
           "(t.fromAccount.user.id = :userId OR t.toAccount.user.id = :userId) " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findRecentTransactionsByUser(@Param("userId") Long userId, 
                                                  Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE " +
           "t.fromAccount.id = :accountId AND t.status = :status " +
           "AND t.createdAt >= :date")
    Long countCompletedTransactionsSince(@Param("accountId") Long accountId,
                                         @Param("status") TransactionStatus status,
                                         @Param("date") LocalDateTime date);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = :status " +
           "ORDER BY t.createdAt ASC")
    List<Transaction> findPendingApprovalTransactions(@Param("status") TransactionStatus status);
    
    @Query("SELECT t FROM Transaction t WHERE t.approvedBy = :userId " +
           "AND t.status = :status " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findApprovedTransactionsByUser(@Param("userId") Long userId,
                                                     @Param("status") TransactionStatus status);
    
    // دوال مساعدة جديدة بدون Pageable (لكل احتياجات Service)
    @Query(value = "SELECT * FROM transactions t WHERE " +
           "(t.from_account_id = :accountId OR t.to_account_id = :accountId) " +
           "ORDER BY t.created_at DESC LIMIT :limit", nativeQuery = true)
    List<Transaction> findRecentTransactionsByAccountNative(@Param("accountId") Long accountId, 
                                                           @Param("limit") int limit);
    
    @Query(value = "SELECT * FROM transactions t WHERE " +
           "(t.from_account_id IN (SELECT id FROM accounts WHERE user_id = :userId) " +
           "OR t.to_account_id IN (SELECT id FROM accounts WHERE user_id = :userId)) " +
           "ORDER BY t.created_at DESC LIMIT :limit", nativeQuery = true)
    List<Transaction> findRecentTransactionsByUserNative(@Param("userId") Long userId, 
                                                        @Param("limit") int limit);
    
    // دوال مبسطة للحصول على إجمالي الإيداعات والسحوبات
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
           "t.toAccount.id = :accountId AND t.status = 'COMPLETED' " +
           "AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    Double getTotalCompletedDeposits(@Param("accountId") Long accountId,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE " +
           "t.fromAccount.id = :accountId AND t.status = 'COMPLETED' " +
           "AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    Double getTotalCompletedWithdrawals(@Param("accountId") Long accountId,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
}