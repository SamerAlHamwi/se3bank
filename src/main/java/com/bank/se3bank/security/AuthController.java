package com.bank.se3bank.security;

import com.bank.se3bank.shared.dto.AuthResponse;
import com.bank.se3bank.shared.dto.LoginRequest;
import com.bank.se3bank.shared.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "المصادقة", description = "تسجيل الدخول والتسجيل وبيانات المستخدم الحالية")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "تسجيل مستخدم جديد", description = "إنشاء حساب جديد وإرجاع رمز JWT")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "تسجيل الدخول", description = "مصادقة المستخدم وإرجاع رمز JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @Operation(summary = "المستخدم الحالي", description = "إرجاع بيانات المستخدم الحالية (يتطلب JWT)")
    public ResponseEntity<AuthResponse> me() {
        return ResponseEntity.ok(authService.me());
    }
}

