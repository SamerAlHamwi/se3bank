package com.bank.se3bank.accounts.controller;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.service.AccountService;
import com.bank.se3bank.shared.dto.AccountResponse;
import com.bank.se3bank.shared.dto.BalanceResponse;
import com.bank.se3bank.shared.dto.CreateAccountRequest;
import com.bank.se3bank.shared.dto.TransferRequest;
import com.bank.se3bank.shared.dto.TransferResponse;
import com.bank.se3bank.shared.dto.UpdateAccountRequest;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.transactions.model.Transaction;
import com.bank.se3bank.transactions.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "إدارة الحسابات", description = "عمليات إنشاء وإدارة الحسابات البنكية")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER')")
    @Operation(summary = "إنشاء حساب جديد", description = "إنشاء حساب بنكي باستخدام Factory Pattern")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountResponse.from(account));
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER','CUSTOMER')")
    @Operation(summary = "الحصول على حساب", description = "الحصول على معلومات حساب بواسطة معرف الحساب")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long accountId) {
        Account account = accountService.getAccountById(accountId);
        return ResponseEntity.ok(AccountResponse.from(account));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER')")
    @Operation(summary = "جميع الحسابات", description = "الحصول على قائمة بجميع الحسابات")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts()
                .stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER','CUSTOMER')")
    @Operation(summary = "حسابات المستخدم", description = "الحصول على جميع حسابات المستخدم")
    public ResponseEntity<List<AccountResponse>> getUserAccounts(@PathVariable Long userId) {
        List<AccountResponse> accounts = accountService.getUserAccounts(userId)
                .stream()
                .map(AccountResponse::from)
                .toList();
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "تحديث الحساب", description = "تحديث بيانات الحساب")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable Long accountId,
            @Valid @RequestBody UpdateAccountRequest request) {
        Account existing = accountService.getAccountById(accountId);
        existing.setStatus(request.getStatus() != null ? request.getStatus() : existing.getStatus());
        existing.setInterestRate(request.getInterestRate() != null ? request.getInterestRate() : existing.getInterestRate());
        existing.setOverdraftLimit(request.getOverdraftLimit() != null ? request.getOverdraftLimit() : existing.getOverdraftLimit());
        existing.setMinimumBalance(request.getMinimumBalance() != null ? request.getMinimumBalance() : existing.getMinimumBalance());
        existing.setBalance(request.getBalance() != null ? request.getBalance() : existing.getBalance());

        Account updated = accountService.updateAccount(existing);
        return ResponseEntity.ok(AccountResponse.from(updated));
    }

    @PatchMapping("/{accountId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "تغيير حالة الحساب", description = "تغيير حالة الحساب (نشط/مجمد/مغلق)")
    public ResponseEntity<AccountResponse> updateStatus(
            @PathVariable Long accountId,
            @RequestParam AccountStatus status) {
        Account account = accountService.updateAccountStatus(accountId, status);
        return ResponseEntity.ok(AccountResponse.from(account));
    }

    @DeleteMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "إغلاق حساب", description = "إغلاق حساب (تغيير الحالة إلى مغلق)")
    public ResponseEntity<Void> closeAccount(@PathVariable Long accountId) {
        accountService.closeAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{accountId}/balance")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER','CUSTOMER')")
    @Operation(summary = "رصيد الحساب", description = "الحصول على رصيد الحساب الحالي")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long accountId) {
        Account account = accountService.getAccountById(accountId);
        BalanceResponse response = BalanceResponse.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .availableBalance(account.getAvailableBalance())
                .currency("USD")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER','CUSTOMER')")
    @Operation(summary = "تحويل الأموال", description = "تحويل الأموال بين حسابين")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        Account fromAccount = accountService.getAccountByNumber(request.getFromAccountNumber());
        Account toAccount = accountService.getAccountByNumber(request.getToAccountNumber());

        Transaction transaction = transactionService.createTransaction(
                fromAccount,
                toAccount,
                request.getAmount(),
                request.getDescription());

        TransferResponse response = TransferResponse.builder()
                .success(true)
                .transactionId(transaction.getTransactionId())
                .fromAccount(fromAccount.getAccountNumber())
                .toAccount(toAccount.getAccountNumber())
                .amount(request.getAmount())
                .newFromBalance(fromAccount.getBalance())
                .newToBalance(toAccount.getBalance())
                .status(transaction.getStatus())
                .message("Transfer request submitted")
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/user/{userId}/total-balance")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER','CUSTOMER')")
    @Operation(summary = "إجمالي الرصيد", description = "الحصول على إجمالي رصيد جميع حسابات المستخدم")
    public ResponseEntity<Map<String, Object>> getTotalBalance(@PathVariable Long userId) {
        Double totalBalance = accountService.getTotalBalanceByUser(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("totalBalance", totalBalance);
        response.put("currency", "USD");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists/{accountNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','TELLER')")
    @Operation(summary = "التحقق من وجود حساب", description = "التحقق من وجود حساب بواسطة رقم الحساب")
    public ResponseEntity<Map<String, Boolean>> checkAccountExists(
            @PathVariable String accountNumber) {
        boolean exists = accountService.accountExists(accountNumber);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }
}