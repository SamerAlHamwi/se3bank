package com.bank.se3bank.accounts.controller;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.AccountGroup;
import com.bank.se3bank.accounts.service.GroupService;
import com.bank.se3bank.shared.dto.CreateGroupRequest;
import com.bank.se3bank.shared.enums.AccountStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "إدارة مجموعات الحسابات", description = "عمليات إنشاء وإدارة مجموعات الحسابات (Composite Pattern)")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @Operation(summary = "إنشاء مجموعة حسابات", 
               description = "إنشاء مجموعة حسابات جديدة باستخدام Composite Pattern")
    public ResponseEntity<AccountGroup> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        AccountGroup group = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @GetMapping("/{groupId}")
    @Operation(summary = "الحصول على مجموعة", description = "الحصول على معلومات مجموعة بواسطة ID")
    public ResponseEntity<AccountGroup> getGroup(@PathVariable Long groupId) {
        AccountGroup group = groupService.getGroupById(groupId);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "مجموعات المستخدم", description = "الحصول على جميع مجموعات المستخدم")
    public ResponseEntity<List<AccountGroup>> getUserGroups(@PathVariable Long userId) {
        List<AccountGroup> groups = groupService.getUserGroups(userId);
        return ResponseEntity.ok(groups);
    }

    @PostMapping("/{groupId}/accounts/{accountId}")
    @Operation(summary = "إضافة حساب للمجموعة", description = "إضافة حساب موجود إلى مجموعة حسابات")
    public ResponseEntity<AccountGroup> addAccountToGroup(
            @PathVariable Long groupId,
            @PathVariable Long accountId) {
        AccountGroup group = groupService.addAccountToGroup(groupId, accountId);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{groupId}/accounts/{accountId}")
    @Operation(summary = "إزالة حساب من المجموعة", description = "إزالة حساب من مجموعة حسابات")
    public ResponseEntity<AccountGroup> removeAccountFromGroup(
            @PathVariable Long groupId,
            @PathVariable Long accountId) {
        AccountGroup group = groupService.removeAccountFromGroup(groupId, accountId);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/{groupId}/accounts")
    @Operation(summary = "حسابات المجموعة", description = "الحصول على جميع حسابات المجموعة")
    public ResponseEntity<List<Account>> getGroupAccounts(@PathVariable Long groupId) {
        List<Account> accounts = groupService.getGroupAccounts(groupId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{groupId}/balance")
    @Operation(summary = "رصيد المجموعة", description = "الحصول على إجمالي رصيد جميع حسابات المجموعة")
    public ResponseEntity<Double> getGroupTotalBalance(@PathVariable Long groupId) {
        Double totalBalance = groupService.getGroupTotalBalance(groupId);
        return ResponseEntity.ok(totalBalance);
    }

    @PostMapping("/{groupId}/transfer")
    @Operation(summary = "تحويل داخل المجموعة", description = "تحويل رصيد بين حسابات داخل نفس المجموعة")
    public ResponseEntity<Void> transferWithinGroup(
            @PathVariable Long groupId,
            @RequestParam String fromAccount,
            @RequestParam String toAccount,
            @RequestParam Double amount) {
        groupService.transferWithinGroup(groupId, fromAccount, toAccount, amount);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{groupId}/status")
    @Operation(summary = "تغيير حالة حسابات المجموعة", 
               description = "تغيير حالة جميع حسابات المجموعة (تفعيل/تجميد/إغلاق)")
    public ResponseEntity<AccountGroup> setGroupAccountsStatus(
            @PathVariable Long groupId,
            @RequestParam AccountStatus status) {
        AccountGroup group = groupService.setGroupAccountsStatus(groupId, status);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/{groupId}/statistics")
    @Operation(summary = "إحصائيات المجموعة", 
               description = "الحصول على إحصائيات مفصلة عن المجموعة وحساباتها")
    public ResponseEntity<GroupService.GroupStatistics> getGroupStatistics(@PathVariable Long groupId) {
        GroupService.GroupStatistics statistics = groupService.getGroupStatistics(groupId);
        return ResponseEntity.ok(statistics);
    }
}