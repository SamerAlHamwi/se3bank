package com.bank.se3bank.security;

import com.bank.se3bank.shared.dto.AuthResponse;
import com.bank.se3bank.shared.dto.LoginRequest;
import com.bank.se3bank.shared.dto.RegisterRequest;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.repository.UserRepository;
import com.bank.se3bank.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateUniqueUser(request);

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .nationalId(request.getNationalId())
                .roles(request.getRoles() == null || request.getRoles().isEmpty()
                        ? null
                        : Set.copyOf(request.getRoles()))
                .build();

        User saved = userService.createUser(newUser);
        String token = jwtUtil.generateToken(saved);
        return AuthResponse.fromUser(saved, token);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User principal = (User) authentication.getPrincipal();
        userService.updateLastLogin(principal.getId());
        String token = jwtUtil.generateToken(principal);
        return AuthResponse.fromUser(principal, token);
    }

    public AuthResponse me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new IllegalStateException("No authenticated user found");
        }
        return AuthResponse.fromUser(user, null);
    }

    private void validateUniqueUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (request.getNationalId() != null && userRepository.existsByNationalId(request.getNationalId())) {
            throw new IllegalArgumentException("National ID already in use");
        }
    }
}

