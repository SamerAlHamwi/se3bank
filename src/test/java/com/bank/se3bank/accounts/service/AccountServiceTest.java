package com.bank.se3bank.accounts.service;

import com.bank.se3bank.accounts.factory.AccountFactory;
import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.SavingsAccount;
import com.bank.se3bank.accounts.repository.AccountRepository;
import com.bank.se3bank.shared.dto.CreateAccountRequest;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.enums.AccountType;
import com.bank.se3bank.shared.exceptions.AccountNotFoundException;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountFactory accountFactory;
    @Mock
    private UserService userService;
    @Mock
    private GroupService groupService;

    @InjectMocks
    private AccountService accountService;

    private User user;
    private Account account;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("alice").roles(Set.of()).build();
        account = SavingsAccount.builder()
                .id(10L)
                .accountNumber("ACC123")
                .accountType(AccountType.SAVINGS)
                .balance(100.0)
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();
    }

    @Test
    void createAccount_shouldPersist() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(user.getId());
        request.setAccountType(AccountType.SAVINGS);
        request.setInitialBalance(100.0);

        given(userService.getUserById(user.getId())).willReturn(user);
        given(accountFactory.createAccount(request, user)).willReturn(account);
        given(accountRepository.save(any(Account.class))).willReturn(account);

        Account result = accountService.createAccount(request);

        assertThat(result.getAccountNumber()).isEqualTo("ACC123");
        verify(accountRepository).save(account);
    }

    @Test
    void getAccountById_notFoundThrows() {
        given(accountRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountById(99L))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void updateBalance_updatesAndSaves() {
        given(accountRepository.findById(account.getId())).willReturn(Optional.of(account));
        given(accountRepository.save(any(Account.class))).willReturn(account);

        Account updated = accountService.updateBalance(account.getId(), 150.0);

        assertThat(updated.getBalance()).isEqualTo(150.0);
        verify(accountRepository).save(account);
    }

    @Test
    void updateAccountStatus_respectsTransitions() {
        given(accountRepository.findById(account.getId())).willReturn(Optional.of(account));
        given(accountRepository.save(any(Account.class))).willReturn(account);

        Account updated = accountService.updateAccountStatus(account.getId(), AccountStatus.FROZEN);
        assertThat(updated.getStatus()).isEqualTo(AccountStatus.FROZEN);
    }
}

