package com.bank.se3bank.users.controller;

import com.bank.se3bank.shared.enums.Role;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "إدارة المستخدمين", description = "عمليات إدارة حسابات المستخدمين")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "إنشاء مستخدم جديد", description = "تسجيل مستخدم جديد في النظام")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "الحصول على مستخدم", description = "الحصول على معلومات مستخدم بواسطة ID")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "بحث باسم المستخدم", description = "الحصول على مستخدم بواسطة اسم المستخدم")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "جميع المستخدمين", description = "الحصول على قائمة جميع المستخدمين")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{userId}/role")
    @Operation(summary = "إضافة دور", description = "إضافة دور للمستخدم")
    public ResponseEntity<User> addRole(
            @PathVariable Long userId,
            @RequestParam Role role) {
        User user = userService.addRoleToUser(userId, role);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}/status")
    @Operation(summary = "تغيير حالة المستخدم", description = "تفعيل أو تعطيل حساب المستخدم")
    public ResponseEntity<User> setUserStatus(
            @PathVariable Long userId,
            @RequestParam Boolean isActive) {
        User user = userService.setUserActiveStatus(userId, isActive);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    @Operation(summary = "بحث عن مستخدمين", description = "البحث عن المستخدمين بالاسم")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        List<User> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{userId}/login")
    @Operation(summary = "تسجيل الدخول", description = "تحديث وقت آخر تسجيل دخول")
    public ResponseEntity<Void> recordLogin(@PathVariable Long userId) {
        userService.updateLastLogin(userId);
        return ResponseEntity.ok().build();
    }
}