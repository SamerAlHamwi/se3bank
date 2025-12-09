package com.bank.se3bank.transactions.service;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.notifications.service.NotificationService;
import com.bank.se3bank.shared.enums.TransactionStatus;
import com.bank.se3bank.shared.enums.TransactionType;
import com.bank.se3bank.transactions.handlers.ApprovalChainFactory;
import com.bank.se3bank.transactions.handlers.TransactionHandler;
import com.bank.se3bank.transactions.model.Transaction;
import com.bank.se3bank.transactions.repository.TransactionRepository;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
    private TransactionHandler transactionHandler;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = Transaction.builder()
                .id(1L)
                .transactionId("TXN1")
                .transactionType(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .amount(100.0)
                .build();
    }

    @Test
    void processTransaction_runsApprovalChain() {
        given(approvalChainFactory.createApprovalChain()).willReturn(transactionHandler);
        given(transactionHandler.handle(any(Transaction.class))).willReturn(true);
        given(transactionRepository.save(any(Transaction.class))).willReturn(transaction);

        Transaction result = transactionService.processTransaction(transaction);

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.PENDING);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void approveTransaction_requiresManagerRole() {
        User manager = User.builder().id(2L).build();
        given(transactionRepository.findById(1L)).willReturn(Optional.of(transaction));
        given(userService.getUserById(2L)).willReturn(manager);

        assertThatThrownBy(() -> transactionService.approveTransaction(1L, 2L, "ok"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectTransaction_notFoundThrows() {
        given(transactionRepository.findById(99L)).willReturn(Optional.empty());
        assertThatThrownBy(() -> transactionService.rejectTransaction(99L, 1L, "r", "c"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

