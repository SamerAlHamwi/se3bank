package com.bank.se3bank.transactions.repository;

import com.bank.se3bank.transactions.model.Transaction;
import com.bank.se3bank.shared.enums.TransactionStatus;
import com.bank.se3bank.shared.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionId(String transactionId);
    
    List<Transaction> findByFromAccountIdOrToAccountId(Long fromAccountId, Long toAccountId);
    
    List<Transaction> findByStatus(TransactionStatus status);
    
    List<Transaction> findByTransactionType(TransactionType type);
    
    @Query("SELECT t FROM Transaction t WHERE " +
           "(:accountId IS NULL OR t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) " +
           "AND (:startDate IS NULL OR t.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR t.createdAt <= :endDate) " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findTransactionsByAccountAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE " +
           "t.toAccount.id = :accountId AND t.status = 'COMPLETED' " +
           "AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    Double getTotalDeposits(@Param("accountId") Long accountId,
                           @Param("startDate") LocalDateTime startDate,
                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE " +
           "t.fromAccount.id = :accountId AND t.status = 'COMPLETED' " +
           "AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    Double getTotalWithdrawals(@Param("accountId") Long accountId,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);
}