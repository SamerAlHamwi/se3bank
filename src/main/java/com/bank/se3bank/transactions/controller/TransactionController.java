package com.bank.se3bank.transactions.controller;

import com.bank.se3bank.shared.dto.ApproveTransactionRequest;
import com.bank.se3bank.shared.dto.CreateTransactionRequest;
import com.bank.se3bank.transactions.model.Transaction;
import com.bank.se3bank.transactions.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "إدارة المعاملات", description = "عمليات إنشاء وإدارة المعاملات البنكية (Chain of Responsibility)")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @PostMapping
    @Operation(summary = "إنشاء معاملة", 
               description = "إنشاء معاملة جديدة باستخدام Chain of Responsibility")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        // سيتم تنفيذ هذا في المرحلة التالية مع الـ Facade
        return ResponseEntity.badRequest().build();
    }
    
    @GetMapping("/{transactionId}")
    @Operation(summary = "الحصول على معاملة", description = "الحصول على معلومات معاملة بواسطة ID")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/reference/{transactionId}")
    @Operation(summary = "الحصول على معاملة بالرقم المرجعي", 
               description = "الحصول على معاملة بواسطة رقم المعاملة المرجعي")
    public ResponseEntity<Transaction> getTransactionByReference(@PathVariable String transactionId) {
        Transaction transaction = transactionService.getTransactionByTransactionId(transactionId);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/account/{accountId}")
    @Operation(summary = "معاملات الحساب", description = "الحصول على معاملات حساب معين")
    public ResponseEntity<List<Transaction>> getAccountTransactions(
            @PathVariable Long accountId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        List<Transaction> transactions = transactionService.getAccountTransactions(
                accountId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/account/{accountId}/recent")
    @Operation(summary = "المعاملات الأخيرة للحساب", 
               description = "الحصول على أحدث معاملات حساب")
    public ResponseEntity<List<Transaction>> getRecentAccountTransactions(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Transaction> transactions = transactionService.getRecentTransactions(accountId, limit);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "المعاملات الأخيرة للمستخدم", 
               description = "الحصول على أحدث معاملات المستخدم")
    public ResponseEntity<List<Transaction>> getRecentUserTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Transaction> transactions = transactionService.getRecentTransactionsByUser(userId, limit);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/pending-approval")
    @Operation(summary = "المعاملات المعلقة اعتماد", 
               description = "الحصول على جميع المعاملات التي تنتظر اعتماد المدير")
    public ResponseEntity<List<Transaction>> getPendingApprovalTransactions() {
        List<Transaction> transactions = transactionService.getPendingApprovalTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    @PostMapping("/{transactionId}/approve")
    @Operation(summary = "اعتماد معاملة", 
               description = "اعتماد معاملة معلقة بواسطة المدير")
    public ResponseEntity<Transaction> approveTransaction(
            @PathVariable Long transactionId,
            @Valid @RequestBody ApproveTransactionRequest request) {
        
        Transaction transaction = transactionService.approveTransaction(
                transactionId, request.getManagerId(), request.getComments());
        return ResponseEntity.ok(transaction);
    }
    
    @PostMapping("/{transactionId}/reject")
    @Operation(summary = "رفض معاملة", 
               description = "رفض معاملة معلقة بواسطة المدير")
    public ResponseEntity<Transaction> rejectTransaction(
            @PathVariable Long transactionId,
            @Valid @RequestBody ApproveTransactionRequest request) {
        
        Transaction transaction = transactionService.rejectTransaction(
                transactionId, request.getManagerId(), 
                request.getReason(), request.getComments());
        return ResponseEntity.ok(transaction);
    }
    
    @PostMapping("/{transactionId}/cancel")
    @Operation(summary = "إلغاء معاملة", 
               description = "إلغاء معاملة معلقة من قبل المستخدم")
    public ResponseEntity<Transaction> cancelTransaction(
            @PathVariable Long transactionId,
            @RequestParam Long userId,
            @RequestParam String reason) {
        
        Transaction transaction = transactionService.cancelTransaction(transactionId, userId, reason);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping("/account/{accountId}/statistics")
    @Operation(summary = "إحصائيات المعاملات", 
               description = "الحصول على إحصائيات المعاملات لحساب معين")
    public ResponseEntity<TransactionStats> getTransactionStatistics(@PathVariable Long accountId) {
        Double totalDeposits = transactionService.getTotalDeposits(accountId);
        Double totalWithdrawals = transactionService.getTotalWithdrawals(accountId);
        
        TransactionStats stats = TransactionStats.builder()
                .accountId(accountId)
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .netFlow(totalDeposits - totalWithdrawals)
                .month(LocalDateTime.now().getMonth().name())
                .build();
        
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/process-pending")
    @Operation(summary = "معالجة المعاملات المعلقة", 
               description = "معالجة جميع المعاملات المعلقة تلقائياً")
    public ResponseEntity<Void> processPendingTransactions() {
        transactionService.processPendingTransactions();
        return ResponseEntity.ok().build();
    }
    
    /**
     * DTO لإحصائيات المعاملات
     */
    @lombok.Data
    @lombok.Builder
    public static class TransactionStats {
        private Long accountId;
        private Double totalDeposits;
        private Double totalWithdrawals;
        private Double netFlow;
        private String month;
    }
}