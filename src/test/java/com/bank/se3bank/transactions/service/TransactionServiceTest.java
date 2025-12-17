package com.bank.se3bank.transactions.service;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.SavingsAccount;
import com.bank.se3bank.accounts.repository.AccountRepository;
import com.bank.se3bank.notifications.service.NotificationService;
import com.bank.se3bank.shared.enums.Role;
import com.bank.se3bank.shared.enums.TransactionStatus;
import com.bank.se3bank.shared.enums.TransactionType;
import com.bank.se3bank.transactions.handlers.ApprovalChainFactory;
import com.bank.se3bank.transactions.handlers.TransactionHandler;
import com.bank.se3bank.transactions.model.Transaction;
import com.bank.se3bank.transactions.repository.TransactionRepository;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ApprovalChainFactory approvalChainFactory;
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserService userService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionHandler approvalChain;

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private User manager;
    private Account account;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("user").roles(Set.of(Role.ROLE_CUSTOMER)).build();
        manager = User.builder().id(2L).username("admin").roles(Set.of(Role.ROLE_MANAGER)).build();
        
        // Use concrete class SavingsAccount instead of abstract Account
        account = SavingsAccount.builder()
                .id(10L)
                .balance(1000.0)
                .user(user)
                .build();

        transaction = Transaction.builder()
                .id(100L)
                .transactionId("TXN-100")
                .fromAccount(account)
                .amount(500.0)
                .transactionType(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.PENDING_APPROVAL)
                .build();
    }

    @Test
    @DisplayName("اختبار اعتماد المعاملة بنجاح")
    void approveTransaction_success() {
        given(transactionRepository.findById(100L)).willReturn(Optional.of(transaction));
        given(userService.getUserById(manager.getId())).willReturn(manager);
        given(transactionRepository.save(any(Transaction.class))).willAnswer(invocation -> invocation.getArgument(0));

        Transaction approved = transactionService.approveTransaction(100L, manager.getId(), "Approved");

        assertThat(approved.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        verify(transactionRepository).save(transaction);
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("اختبار رفض المعاملة من قبل المدير")
    void rejectTransaction_success() {
        given(transactionRepository.findById(100L)).willReturn(Optional.of(transaction));
        given(userService.getUserById(manager.getId())).willReturn(manager);
        given(transactionRepository.save(any(Transaction.class))).willAnswer(invocation -> invocation.getArgument(0));

        Transaction rejected = transactionService.rejectTransaction(100L, manager.getId(), "High Risk", "Rejected");

        assertThat(rejected.getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(rejected.getFailureReason()).contains("High Risk");
        verify(transactionRepository).save(transaction);
    }

    @Test
    @DisplayName("اختبار إنشاء معاملة سحب جديدة")
    void createWithdrawalTransaction_createsAndProcesses() {
        given(approvalChainFactory.createApprovalChain()).willReturn(approvalChain);
        given(approvalChain.handle(any(Transaction.class))).willReturn(true);
        given(transactionRepository.save(any(Transaction.class))).willAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            if (t.getStatus() == TransactionStatus.PENDING) {
                 t.setStatus(TransactionStatus.COMPLETED);
            }
            return t;
        });

        Transaction result = transactionService.createWithdrawalTransaction(account, 100.0, "ATM Withdrawal");

        assertThat(result.getAmount()).isEqualTo(100.0);
        assertThat(result.getTransactionType()).isEqualTo(TransactionType.WITHDRAWAL);
        verify(approvalChain).handle(any(Transaction.class));
    }
    
    @Test
    @DisplayName("اختبار فشل الاعتماد لغير المدراء")
    void approveTransaction_nonManager_throwsException() {
        given(transactionRepository.findById(100L)).willReturn(Optional.of(transaction));
        given(userService.getUserById(user.getId())).willReturn(user); // User is not manager

        assertThatThrownBy(() -> transactionService.approveTransaction(100L, user.getId(), "Ok"))
                .isInstanceOf(SecurityException.class);
    }
}