package com.bank.se3bank.accounts.controller;

import com.bank.se3bank.accounts.decorators.AccountDecorator;
import com.bank.se3bank.accounts.service.DecoratorService;
import com.bank.se3bank.shared.dto.AddDecoratorRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/decorators")
@RequiredArgsConstructor
@Tag(name = "إدارة ديكورات الحسابات", description = "إضافة وإزالة الميزات الديناميكية للحسابات (Decorator Pattern)")
public class DecoratorController {
    
    private final DecoratorService decoratorService;
    
    @PostMapping
    @Operation(summary = "إضافة ديكور لحساب", 
               description = "إضافة ميزة ديناميكية لحساب باستخدام Decorator Pattern")
    public ResponseEntity<AccountDecorator> addDecorator(@Valid @RequestBody AddDecoratorRequest request) {
        AccountDecorator decorator = decoratorService.addDecorator(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(decorator);
    }
    
    @GetMapping("/account/{accountId}")
    @Operation(summary = "ديكورات الحساب", description = "الحصول على جميع ديكورات حساب معين")
    public ResponseEntity<List<AccountDecorator>> getAccountDecorators(@PathVariable Long accountId) {
        List<AccountDecorator> decorators = decoratorService.getAccountDecorators(accountId);
        return ResponseEntity.ok(decorators);
    }
    
    @GetMapping("/account/{accountId}/active")
    @Operation(summary = "الديكورات النشطة", description = "الحصول على الديكورات النشطة فقط")
    public ResponseEntity<List<AccountDecorator>> getActiveDecorators(@PathVariable Long accountId) {
        List<AccountDecorator> decorators = decoratorService.getActiveDecorators(accountId);
        return ResponseEntity.ok(decorators);
    }
    
    @GetMapping("/account/{accountId}/features")
    @Operation(summary = "ميزات الحساب", description = "الحصول على جميع الميزات (الأصلية + المضافة)")
    public ResponseEntity<List<String>> getAccountFeatures(@PathVariable Long accountId) {
        List<String> features = decoratorService.getAccountFeatures(accountId);
        return ResponseEntity.ok(features);
    }
    
    @PatchMapping("/{decoratorId}/activate")
    @Operation(summary = "تفعيل ديكور", description = "تفعيل ديكور معطل")
    public ResponseEntity<AccountDecorator> activateDecorator(@PathVariable Long decoratorId) {
        AccountDecorator decorator = decoratorService.activateDecorator(decoratorId);
        return ResponseEntity.ok(decorator);
    }
    
    @DeleteMapping("/{decoratorId}")
    @Operation(summary = "إزالة ديكور", description = "تعطيل وإزالة ديكور من حساب")
    public ResponseEntity<Void> removeDecorator(@PathVariable Long decoratorId) {
        decoratorService.removeDecorator(decoratorId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/apply-fees")
    @Operation(summary = "تطبيق الرسوم الشهرية", 
               description = "تطبيق الرسوم الشهرية لجميع الديكورات النشطة")
    public ResponseEntity<Void> applyMonthlyFees() {
        decoratorService.applyAllMonthlyFees();
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/info")
    @Operation(summary = "معلومات الديكورات", 
               description = "الحصول على معلومات عن أنواع الديكورات المتاحة")
    public ResponseEntity<String> getDecoratorsInfo() {
        // في تطبيق حقيقي، نرجع DTO بدلاً من String
        String info = """
                أنواع الديكورات المتاحة:
                1. OVERDRAFT_PROTECTION - حماية السحب على المكشوف
                2. INSURANCE - تأمين على الحساب
                3. PREMIUM_SERVICES - خدمات مميزة (GOLD, PLATINUM, DIAMOND)
                """;
        return ResponseEntity.ok(info);
    }
}