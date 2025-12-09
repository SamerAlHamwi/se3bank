package com.bank.se3bank.users.service;

import com.bank.se3bank.shared.enums.Role;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("john")
                .email("john@test.com")
                .password("raw")
                .roles(Set.of())
                .build();
    }

    @Test
    void createUser_setsDefaultRoleAndEncodesPassword() {
        given(passwordEncoder.encode("raw")).willReturn("ENCODED");
        given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        User saved = userService.createUser(user);

        assertThat(saved.getPassword()).isEqualTo("ENCODED");
        assertThat(saved.getRoles()).contains(Role.ROLE_CUSTOMER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUserByUsername_notFoundThrows() {
        given(userRepository.findByUsername("missing")).willReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserByUsername("missing"));
    }
}

