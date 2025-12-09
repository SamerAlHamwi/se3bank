package com.bank.se3bank.security;

import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("🔎 Loading user for authentication: {}", username);
        return userRepository.findByUsername(username)
                .map(User.class::cast)
                .or(() -> userRepository.findByEmail(username).map(User.class::cast))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}

