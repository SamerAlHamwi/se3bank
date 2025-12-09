package com.bank.se3bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())       // تعطيل CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()   // كل المسارات مفتوحة
                )
                .formLogin(form -> form.disable())   // إلغاء صفحة تسجيل الدخول بالكامل
                .httpBasic(httpBasic -> httpBasic.disable()) // إلغاء Basic Auth
                .logout(logout -> logout.disable()); // إلغاء logout page أيضًا

        return http.build();
    }
}
