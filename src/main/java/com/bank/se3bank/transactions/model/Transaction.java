package com.bank.se3bank.transactions.model;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.shared.enums.TransactionStatus;
import com.bank.se3bank.shared.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;
    
    @Column(name = "amount", nullable = false)
    private Double amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "reference_number")
    private String referenceNumber;
    
    @Column(name = "initiated_by")
    private Long initiatedBy; // User ID
    
    @Column(name = "approved_by")
    private Long approvedBy; // User ID
    
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "failure_reason")
    private String failureReason;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string for additional data
    
    @Column(name = "approval_chain_log", columnDefinition = "TEXT")
    private String approvalChainLog; // سجل سلسلة الاعتماد
    
    @PrePersist
    public void generateTransactionId() {
        if (this.transactionId == null) {
            this.transactionId = "TXN" + System.currentTimeMillis() + 
                (int)(Math.random() * 1000);
        }
    }
    
    public void markAsCompleted() {
        this.status = TransactionStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String reason) {
        this.status = TransactionStatus.FAILED;
        this.failureReason = reason;
        this.processedAt = LocalDateTime.now();
    }
    
    public void markAsPendingApproval() {
        this.status = TransactionStatus.PENDING_APPROVAL;
    }
    
    public void markAsCancelled() {
        this.status = TransactionStatus.CANCELLED;
        this.processedAt = LocalDateTime.now();
    }
    
    public boolean requiresApproval() {
        return this.status == TransactionStatus.PENDING_APPROVAL;
    }
    
    public void addToApprovalChainLog(String handlerName, String message) {
        String logEntry = String.format("[%s] %s: %s\n", 
                LocalDateTime.now(), handlerName, message);
        
        if (this.approvalChainLog == null) {
            this.approvalChainLog = logEntry;
        } else {
            this.approvalChainLog += logEntry;
        }
    }
}