// 📁 src/main/java/com/bank/se3bank/interest/controller/InterestController.java
package com.bank.se3bank.interest.controller;

import com.bank.se3bank.interest.service.InterestService;
import com.bank.se3bank.shared.dto.ChangeInterestStrategyRequest;
import com.bank.se3bank.shared.enums.AccountType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/interest")
@RequiredArgsConstructor
@Tag(name = "إدارة الفائدة", description = "عمليات حساب وإدارة الفائدة البنكية (Strategy Pattern)")
public class InterestController {
    
    private final InterestService interestService;
    
    @PostMapping("/accounts/{accountId}/apply")
    @Operation(summary = "تطبيق الفائدة على حساب", 
               description = "تطبيق الفائدة على حساب معين باستخدام الاستراتيجية المناسبة")
    public ResponseEntity<Double> applyInterest(@PathVariable Long accountId) {
        Double interestAmount = interestService.applyInterestToAccount(accountId);
        return ResponseEntity.ok(interestAmount);
    }
    
    @PostMapping("/apply-all")
    @Operation(summary = "تطبيق الفائدة على جميع الحسابات", 
               description = "تطبيق الفائدة على جميع الحسابات المؤهلة")
    public ResponseEntity<Void> applyInterestToAll() {
        interestService.applyInterestToAllAccounts();
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/accounts/{accountId}/change-strategy")
    @Operation(summary = "تغيير استراتيجية الفائدة", 
               description = "تغيير استراتيجية حساب الفائدة لحساب معين")
    public ResponseEntity<Void> changeInterestStrategy(
            @PathVariable Long accountId,
            @Valid @RequestBody ChangeInterestStrategyRequest request) {
        
        interestService.changeAccountInterestStrategy(accountId, request.getStrategyName());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/accounts/{accountId}/report")
    @Operation(summary = "تقرير الفائدة", 
               description = "الحصول على تقرير مفصل عن الفائدة لحساب معين")
    public ResponseEntity<InterestService.InterestReport> getInterestReport(@PathVariable Long accountId) {
        InterestService.InterestReport report = interestService.getInterestReport(accountId);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/accounts/{accountId}/future/{months}")
    @Operation(summary = "حساب الفائدة المستقبلية", 
               description = "حساب الفائدة المستقبلية لحساب معين")
    public ResponseEntity<Double> calculateFutureInterest(
            @PathVariable Long accountId,
            @PathVariable Integer months) {
        
        Double futureInterest = interestService.calculateFutureInterest(accountId, months);
        return ResponseEntity.ok(futureInterest);
    }
    
    @GetMapping("/strategies")
    @Operation(summary = "الاستراتيجيات المتاحة", 
               description = "الحصول على جميع استراتيجيات الفائدة المتاحة")
    public ResponseEntity<Map<String, com.bank.se3bank.interest.strategy.InterestStrategy>> 
            getAllStrategies() {
        
        // Note: في تطبيق حقيقي، نرجع DTO بدلاً من الـ Strategy نفسها
        var strategies = interestService.getSupportedStrategies(null);
        return ResponseEntity.ok(strategies);
    }
    
    @GetMapping("/strategies/{accountType}")
    @Operation(summary = "الاستراتيجيات المدعومة", 
               description = "الحصول على استراتيجيات الفائدة المدعومة لنوع حساب معين")
    public ResponseEntity<Map<String, com.bank.se3bank.interest.strategy.InterestStrategy>> 
            getSupportedStrategies(@PathVariable AccountType accountType) {
        
        var strategies = interestService.getSupportedStrategies(accountType);
        return ResponseEntity.ok(strategies);
    }
    
    @GetMapping("/accounts/{accountId}/compare")
    @Operation(summary = "مقارنة الاستراتيجيات", 
               description = "مقارنة أداء استراتيجيتين فائدة لحساب معين")
    public ResponseEntity<com.bank.se3bank.interest.service.InterestCalculator.InterestComparison> 
            compareStrategies(
                    @PathVariable Long accountId,
                    @RequestParam String strategy1,
                    @RequestParam String strategy2) {
        
        var comparison = interestService.compareStrategiesForAccount(accountId, strategy1, strategy2);
        return ResponseEntity.ok(comparison);
    }
    
    @GetMapping("/accounts/{accountId}/rate")
    @Operation(summary = "معدل الفائدة الفعلي", 
               description = "حساب معدل الفائدة الفعلي للحساب")
    public ResponseEntity<Double> getEffectiveInterestRate(@PathVariable Long accountId) {
        // سيتم حسابها في Service
        return ResponseEntity.ok(2.5); // مثال
    }
}