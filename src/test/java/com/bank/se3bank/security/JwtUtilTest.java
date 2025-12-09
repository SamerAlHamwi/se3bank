package com.bank.se3bank.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "TestSecretKey12345678901234567890");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 2000L);

        userDetails = User.withUsername("alice")
                .password("password")
                .roles("ADMIN")
                .build();
    }

    @Test
    void generateAndValidateToken_success() {
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.isTokenValid(token, userDetails));
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("alice");
        assertThat(jwtUtil.extractRoles(token)).contains("ROLE_ADMIN");
    }

    @Test
    void invalidToken_shouldFailValidation() {
        String invalid = "bad.token.value";
        assertFalse(jwtUtil.isTokenValid(invalid, userDetails));
    }

    @Test
    void expiredToken_shouldBeInvalid() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 10L);
        String token = jwtUtil.generateToken(userDetails);
        Thread.sleep(30L);
        assertFalse(jwtUtil.isTokenValid(token, userDetails));
    }
}

