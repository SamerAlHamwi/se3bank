package com.bank.se3bank.transactions.controller;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.service.AccountService;
import com.bank.se3bank.shared.dto.ApproveTransactionRequest;
import com.bank.se3bank.shared.dto.CreateTransactionRequest;
import com.bank.se3bank.shared.dto.TransactionResponse;
import com.bank.se3bank.transactions.model.Transaction;
import com.bank.se3bank.transactions.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "إدارة المعاملات", description = "عمليات إنشاء وإدارة المعاملات البنكية (Chain of Responsibility)")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER','CUSTOMER')")
    @Operation(summary = "إنشاء معاملة", description = "إنشاء معاملة جديدة باستخدام Chain of Responsibility")
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        Transaction transaction = switch (request.getTransactionType()) {
            case TRANSFER -> {
                Account from = accountService.getAccountByNumber(request.getFromAccountNumber());
                Account to = accountService.getAccountByNumber(request.getToAccountNumber());
                yield transactionService.createTransaction(from, to, request.getAmount(), request.getDescription());
            }
            case DEPOSIT -> {
                Account to = accountService.getAccountByNumber(request.getToAccountNumber());
                yield transactionService.createDepositTransaction(to, request.getAmount(), request.getDescription());
            }
            case WITHDRAWAL -> {
                Account from = accountService.getAccountByNumber(request.getFromAccountNumber());
                yield transactionService.createWithdrawalTransaction(from, request.getAmount(), request.getDescription());
            }
            case PAYMENT -> {
                Account from = accountService.getAccountByNumber(request.getFromAccountNumber());
                yield transactionService.createPaymentTransaction(from, request.getReferenceNumber(), request.getAmount(), request.getDescription());
            }
            default -> throw new IllegalArgumentException("نوع معاملة غير مدعوم: " + request.getTransactionType());
        };

        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    @GetMapping("/{transactionId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER','CUSTOMER')")
    @Operation(summary = "الحصول على معاملة", description = "الحصول على معلومات معاملة بواسطة ID")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    @GetMapping("/reference/{transactionId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER','CUSTOMER')")
    @Operation(summary = "الحصول على معاملة بالرقم المرجعي", description = "الحصول على معاملة بواسطة رقم المعاملة المرجعي")
    public ResponseEntity<TransactionResponse> getTransactionByReference(@PathVariable String transactionId) {
        Transaction transaction = transactionService.getTransactionByTransactionId(transactionId);
        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER','CUSTOMER')")
    @Operation(summary = "معاملات الحساب", description = "الحصول على معاملات حساب معين")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactions(
            @PathVariable Long accountId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {

        List<TransactionResponse> transactions = transactionService.getAccountTransactions(
                        accountId, startDate, endDate)
                .stream()
                .map(TransactionResponse::from)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/recent")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER','CUSTOMER')")
    @Operation(summary = "المعاملات الأخيرة للحساب", description = "الحصول على أحدث معاملات حساب")
    public ResponseEntity<List<TransactionResponse>> getRecentAccountTransactions(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "10") int limit) {

        List<TransactionResponse> transactions = transactionService.getRecentTransactions(accountId, limit)
                .stream()
                .map(TransactionResponse::from)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/user/{userId}/recent")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER','CUSTOMER')")
    @Operation(summary = "المعاملات الأخيرة للمستخدم", description = "الحصول على أحدث معاملات المستخدم")
    public ResponseEntity<List<TransactionResponse>> getRecentUserTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {

        List<TransactionResponse> transactions = transactionService.getRecentTransactionsByUser(userId, limit)
                .stream()
                .map(TransactionResponse::from)
                .toList();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/pending-approval")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "المعاملات المعلقة اعتماد", description = "الحصول على جميع المعاملات التي تنتظر اعتماد المدير")
    public ResponseEntity<List<TransactionResponse>> getPendingApprovalTransactions() {
        List<TransactionResponse> transactions = transactionService.getPendingApprovalTransactions()
                .stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/{transactionId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "اعتماد معاملة", description = "اعتماد معاملة معلقة بواسطة المدير")
    public ResponseEntity<TransactionResponse> approveTransaction(
            @PathVariable Long transactionId,
            @Valid @RequestBody ApproveTransactionRequest request) {

        Transaction transaction = transactionService.approveTransaction(
                transactionId, request.getManagerId(), request.getComments());
        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    @PostMapping("/{transactionId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "رفض معاملة", description = "رفض معاملة معلقة بواسطة المدير")
    public ResponseEntity<TransactionResponse> rejectTransaction(
            @PathVariable Long transactionId,
            @Valid @RequestBody ApproveTransactionRequest request) {

        Transaction transaction = transactionService.rejectTransaction(
                transactionId, request.getManagerId(),
                request.getReason(), request.getComments());
        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    @PostMapping("/{transactionId}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','CUSTOMER')")
    @Operation(summary = "إلغاء معاملة", description = "إلغاء معاملة معلقة من قبل المستخدم")
    public ResponseEntity<TransactionResponse> cancelTransaction(
            @PathVariable Long transactionId,
            @RequestParam Long userId,
            @RequestParam String reason) {

        Transaction transaction = transactionService.cancelTransaction(transactionId, userId, reason);
        return ResponseEntity.ok(TransactionResponse.from(transaction));
    }

    @GetMapping("/account/{accountId}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "إحصائيات المعاملات", description = "الحصول على إحصائيات المعاملات لحساب معين")
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
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "معالجة المعاملات المعلقة", description = "معالجة جميع المعاملات المعلقة تلقائياً")
    public ResponseEntity<Void> processPendingTransactions() {
        transactionService.processPendingTransactions();
        return ResponseEntity.ok().build();
    }

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