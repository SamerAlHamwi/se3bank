package com.bank.se3bank.facade;

import com.bank.se3bank.shared.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banking")
@RequiredArgsConstructor
@Tag(name = "الخدمات البنكية المبسطة", 
     description = "واجهة مبسطة للعمليات البنكية المعقدة (Facade Pattern)")
public class BankFacadeController {
    
    private final BankFacade bankFacade;
    
    @PostMapping("/accounts/open")
    @Operation(summary = "فتح حساب جديد", 
               description = "عملية كاملة لفتح حساب مع الإشعارات والتسجيل")
    public ResponseEntity<AccountOpenResponse> openAccount(@Valid @RequestBody OpenAccountRequest request) {
        AccountOpenResponse response = bankFacade.openNewAccount(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/transfer")
    @Operation(summary = "تحويل أموال", 
               description = "تحويل أموال كامل مع التحقق والإشعارات والتسجيل")
    public ResponseEntity<TransferResponse> transferMoney(@Valid @RequestBody TransferRequest request) {
        TransferResponse response = bankFacade.transferMoney(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/withdraw")
    @Operation(summary = "سحب أموال", 
               description = "سحب أموال كامل مع التحقق والإشعارات والتسجيل")
    public ResponseEntity<WithdrawalResponse> withdrawMoney(@Valid @RequestBody WithdrawalRequest request) {
        WithdrawalResponse response = bankFacade.withdrawMoney(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/deposit")
    @Operation(summary = "إيداع أموال", 
               description = "إيداع أموال كامل مع الإشعارات والتسجيل")
    public ResponseEntity<DepositResponse> depositMoney(@Valid @RequestBody DepositRequest request) {
        DepositResponse response = bankFacade.depositMoney(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/accounts/{accountNumber}/summary")
    @Operation(summary = "ملخص الحساب", 
               description = "الحصول على جميع معلومات الحساب في استجابة واحدة")
    public ResponseEntity<AccountSummary> getAccountSummary(@PathVariable String accountNumber) {
        AccountSummary summary = bankFacade.getAccountSummary(accountNumber);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/users/{userId}/summary")
    @Operation(summary = "ملخص المستخدم", 
               description = "الحصول على جميع معلومات المستخدم وحساباته في استجابة واحدة")
    public ResponseEntity<UserSummary> getUserSummary(@PathVariable Long userId) {
        UserSummary summary = bankFacade.getUserSummary(userId);
        return ResponseEntity.ok(summary);
    }
    
    @PostMapping("/groups/create")
    @Operation(summary = "إنشاء مجموعة حسابات", 
               description = "إنشاء مجموعة حسابات مع إضافة الحسابات والإشعارات")
    public ResponseEntity<GroupCreationResponse> createAccountGroup(
            @Valid @RequestBody GroupCreationRequest request) {
        GroupCreationResponse response = bankFacade.createAccountGroup(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/accounts/{accountNumber}/balance")
    @Operation(summary = "رصيد الحساب", description = "الحصول على رصيد حساب بسيط")
    public ResponseEntity<Double> getAccountBalance(@PathVariable String accountNumber) {
        Double balance = bankFacade.getAccountBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }
    
    @GetMapping("/accounts/{accountNumber}/transactions")
    @Operation(summary = "معاملات الحساب", description = "الحصول على معاملات الحساب الأخيرة")
    public ResponseEntity<?> getAccountTransactions(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "10") int limit) {
        var transactions = bankFacade.getAccountTransactions(accountNumber, limit);
        return ResponseEntity.ok(transactions);
    }
}