package com.bank.se3bank.accounts.service;

import com.bank.se3bank.accounts.factory.AccountFactory;
import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.CheckingAccount;
import com.bank.se3bank.accounts.model.SavingsAccount;
import com.bank.se3bank.accounts.repository.AccountRepository;
import com.bank.se3bank.shared.dto.CreateAccountRequest;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.enums.AccountType;
import com.bank.se3bank.shared.exceptions.AccountNotFoundException;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    private SavingsAccount savingsAccount;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("alice").roles(Set.of()).build();
        
        // Use concrete class CheckingAccount
        account = CheckingAccount.builder()
                .id(10L)
                .accountNumber("ACC123")
                .accountType(AccountType.CHECKING)
                .balance(100.0)
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();
        
        savingsAccount = SavingsAccount.builder()
                .id(11L)
                .accountNumber("SAV123")
                .accountType(AccountType.SAVINGS)
                .balance(500.0)
                .status(AccountStatus.ACTIVE)
                .user(user)
                .withdrawalsThisMonth(0)
                .build();
    }

    @Test
    @DisplayName("اختبار إنشاء حساب جديد")
    void createAccount_shouldPersist() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(user.getId());
        request.setAccountType(AccountType.CHECKING);
        request.setInitialBalance(100.0);

        given(userService.getUserById(user.getId())).willReturn(user);
        given(accountFactory.createAccount(request, user)).willReturn(account);
        given(accountRepository.save(any(Account.class))).willReturn(account);

        Account result = accountService.createAccount(request);

        assertThat(result.getAccountNumber()).isEqualTo("ACC123");
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("اختبار البحث عن حساب غير موجود")
    void getAccountById_notFoundThrows() {
        given(accountRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccountById(99L))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    @DisplayName("اختبار تحديث الرصيد")
    void updateBalance_updatesAndSaves() {
        given(accountRepository.findById(account.getId())).willReturn(Optional.of(account));
        given(accountRepository.save(any(Account.class))).willReturn(account);

        Account updated = accountService.updateBalance(account.getId(), 150.0);

        assertThat(updated.getBalance()).isEqualTo(150.0);
        verify(accountRepository).save(account);
    }
    
    @Test
    @DisplayName("اختبار الإيداع")
    void deposit_increaseBalance() {
        given(accountRepository.findById(account.getId())).willReturn(Optional.of(account));
        given(accountRepository.save(any(Account.class))).willReturn(account);
        
        Account updated = accountService.deposit(account.getId(), 50.0);
        
        assertThat(updated.getBalance()).isEqualTo(150.0); // 100 + 50
    }

    @Test
    @DisplayName("اختبار السحب من حساب جاري")
    void withdraw_decreaseBalance() {
        given(accountRepository.findById(account.getId())).willReturn(Optional.of(account));
        given(accountRepository.save(any(Account.class))).willReturn(account);
        
        Account updated = accountService.withdraw(account.getId(), 40.0);
        
        assertThat(updated.getBalance()).isEqualTo(60.0); // 100 - 40
    }
    
    @Test
    @DisplayName("اختبار السحب من حساب توفير (تجاوز الحد)")
    void withdraw_savingsAccountLimit_throwsException() {
        savingsAccount.setWithdrawalsThisMonth(6); 
        
        given(accountRepository.findById(savingsAccount.getId())).willReturn(Optional.of(savingsAccount));
        
        assertThatThrownBy(() -> accountService.withdraw(savingsAccount.getId(), 10.0))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("تجاوز حد السحب");
    }

    @Test
    @DisplayName("اختبار تغيير حالة الحساب (انتقال صحيح)")
    void updateAccountStatus_respectsTransitions() {
        given(accountRepository.findById(account.getId())).willReturn(Optional.of(account));
        given(accountRepository.save(any(Account.class))).willReturn(account);

        Account updated = accountService.updateAccountStatus(account.getId(), AccountStatus.FROZEN);
        assertThat(updated.getStatus()).isEqualTo(AccountStatus.FROZEN);
    }
    
    @Test
    @DisplayName("اختبار تغيير حالة الحساب (انتقال خاطئ)")
    void updateAccountStatus_invalidTransition_throwsException() {
        account.setStatus(AccountStatus.CLOSED);
        given(accountRepository.findById(account.getId())).willReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.updateAccountStatus(account.getId(), AccountStatus.ACTIVE))
                .isInstanceOf(IllegalStateException.class);
    }
    
    @Test
    @DisplayName("اختبار التحويل بين الحسابات")
    void transfer_movesMoneyCorrectly() {
        Account toAccount = CheckingAccount.builder()
                .id(20L)
                .accountNumber("ACC999")
                .balance(50.0)
                .status(AccountStatus.ACTIVE)
                .build();
        
        given(accountRepository.findById(account.getId())).willReturn(Optional.of(account));
        given(accountRepository.findById(toAccount.getId())).willReturn(Optional.of(toAccount));
        
        accountService.transfer(account.getId(), toAccount.getId(), 30.0);
        
        assertThat(account.getBalance()).isEqualTo(70.0); // 100 - 30
        assertThat(toAccount.getBalance()).isEqualTo(80.0); // 50 + 30
        
        verify(accountRepository).save(account);
        verify(accountRepository).save(toAccount);
    }
    
    @Test
    @DisplayName("اختبار تطبيق الفائدة")
    void applyInterest_addsToBalanceAndUpdatesDate() {
        given(accountRepository.findById(account.getId())).willReturn(Optional.of(account));
        given(accountRepository.save(any(Account.class))).willReturn(account);
        
        accountService.applyInterest(account.getId(), 5.0);
        
        assertThat(account.getBalance()).isEqualTo(105.0);
        assertThat(account.getLastInterestCalculation()).isNotNull();
    }
}