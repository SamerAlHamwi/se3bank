package com.bank.se3bank.accounts.controller;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.service.AccountService;
import com.bank.se3bank.shared.dto.CreateAccountRequest;
import com.bank.se3bank.shared.enums.AccountStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "إدارة الحسابات", description = "عمليات إنشاء وإدارة الحسابات البنكية")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "إنشاء حساب جديد", description = "إنشاء حساب بنكي باستخدام Factory Pattern")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "الحصول على حساب", description = "الحصول على معلومات حساب بواسطة رقم الحساب")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "حسابات المستخدم", description = "الحصول على جميع حسابات المستخدم")
    public ResponseEntity<List<Account>> getUserAccounts(@PathVariable Long userId) {
        List<Account> accounts = accountService.getUserAccounts(userId);
        return ResponseEntity.ok(accounts);
    }

    @PatchMapping("/{accountId}/balance")
    @Operation(summary = "تحديث الرصيد", description = "تحديث رصيد حساب معين")
    public ResponseEntity<Account> updateBalance(
            @PathVariable Long accountId,
            @RequestParam Double balance) {
        Account account = accountService.updateBalance(accountId, balance);
        return ResponseEntity.ok(account);
    }

    @PatchMapping("/{accountId}/status")
    @Operation(summary = "تغيير حالة الحساب", description = "تغيير حالة الحساب (نشط/مجمد/مغلق)")
    public ResponseEntity<Account> updateStatus(
            @PathVariable Long accountId,
            @RequestParam AccountStatus status) {
        Account account = accountService.updateAccountStatus(accountId, status);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/{accountId}")
    @Operation(summary = "إغلاق حساب", description = "إغلاق حساب (تغيير الحالة إلى مغلق)")
    public ResponseEntity<Void> closeAccount(@PathVariable Long accountId) {
        accountService.closeAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/total-balance")
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
    @Operation(summary = "التحقق من وجود حساب", description = "التحقق من وجود حساب بواسطة رقم الحساب")
    public ResponseEntity<Map<String, Boolean>> checkAccountExists(
            @PathVariable String accountNumber) {
        boolean exists = accountService.accountExists(accountNumber);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        
        return ResponseEntity.ok(response);
    }
}