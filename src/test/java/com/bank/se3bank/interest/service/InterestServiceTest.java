package com.bank.se3bank.interest.service;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.SavingsAccount;
import com.bank.se3bank.accounts.service.AccountService;
import com.bank.se3bank.interest.strategy.CompoundInterestStrategy;
import com.bank.se3bank.interest.strategy.InterestStrategy;
import com.bank.se3bank.interest.strategy.SimpleInterestStrategy;
import com.bank.se3bank.notifications.service.NotificationService;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.enums.AccountType;
import com.bank.se3bank.transactions.service.TransactionService;
import com.bank.se3bank.users.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

    @Mock
    private InterestCalculator interestCalculator;
    @Mock
    private AccountService accountService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private SimpleInterestStrategy simpleInterestStrategy;
    @Mock
    private CompoundInterestStrategy compoundInterestStrategy;

    @InjectMocks
    private InterestService interestService;

    private Account testAccount;
    private User testUser;
    private Map<String, InterestStrategy> strategiesMap;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testUser")
                .email("test@example.com")
                .build();

        // Use a concrete implementation like SavingsAccount since Account is abstract
        testAccount = SavingsAccount.builder()
                .id(100L)
                .accountNumber("ACC-100")
                .balance(1000.0)
                .status(AccountStatus.ACTIVE)
                .user(testUser)
                .createdAt(LocalDateTime.now().minusMonths(6))
                .build();
                
        // Use lenient() because not all tests will use this stub
        lenient().when(accountService.getAccountById(100L)).thenReturn(testAccount);

        strategiesMap = new HashMap<>();
        strategiesMap.put("simpleInterestStrategy", simpleInterestStrategy);
        strategiesMap.put("compoundInterestStrategy", compoundInterestStrategy);
        
        lenient().when(interestCalculator.getAllStrategies()).thenReturn(strategiesMap);
    }

    @Test
    @DisplayName("اختبار حساب الفائدة لحساب واحد")
    void testApplyInterestToAccount() {
        // Given
        Double expectedInterest = 50.0;
        when(interestCalculator.calculateInterest(testAccount)).thenReturn(expectedInterest);
        when(simpleInterestStrategy.getStrategyName()).thenReturn("فائدة بسيطة");
        testAccount.setInterestStrategyName("simpleInterestStrategy");

        // When
        Double actualInterest = interestService.applyInterestToAccount(100L);

        // Then
        assertEquals(expectedInterest, actualInterest);
        verify(accountService, times(1)).updateBalance(eq(100L), eq(1050.0));
        verify(transactionService, times(1)).createDepositTransaction(any(Account.class), eq(50.0), anyString());
        verify(notificationService, times(1)).sendInterestAddedNotification(any(User.class), any(Account.class), eq(50.0));
    }

    @Test
    @DisplayName("اختبار تطبيق الفائدة على جميع الحسابات")
    void testApplyInterestToAllAccounts() {
        // Given
        List<Account> accounts = new ArrayList<>();
        Account eligibleAccount = testAccount;
        
        // Use concrete class for ineligible account too
        Account ineligibleAccount = SavingsAccount.builder()
                .id(101L)
                .balance(0.0) // Zero balance
                .status(AccountStatus.ACTIVE)
                .build();

        accounts.add(eligibleAccount);
        accounts.add(ineligibleAccount);

        when(accountService.getAllAccounts()).thenReturn(accounts);
        // Eligible account logic
        when(accountService.getAccountById(100L)).thenReturn(eligibleAccount);
        when(interestCalculator.calculateInterest(eligibleAccount)).thenReturn(50.0);
        // Setting strategy name for mock
        eligibleAccount.setInterestStrategyName("simpleInterestStrategy");
        when(simpleInterestStrategy.getStrategyName()).thenReturn("Simple Interest");


        // When
        interestService.applyInterestToAllAccounts();

        // Then
        // Eligible account should be processed
        verify(accountService).updateBalance(eq(100L), anyDouble());
        // Ineligible account (zero balance) should not be processed for interest calculation logic inside applyInterestToAccount
        verify(accountService, times(0)).updateBalance(eq(101L), anyDouble());
    }

    @Test
    @DisplayName("اختبار تغيير استراتيجية الفائدة")
    void testChangeAccountInterestStrategy() {
        // Given
        String newStrategy = "compoundInterestStrategy";
        doNothing().when(interestCalculator).changeAccountStrategy(testAccount, newStrategy);
        
        // When
        interestService.changeAccountInterestStrategy(100L, newStrategy);

        // Then
        verify(interestCalculator).changeAccountStrategy(testAccount, newStrategy);
        verify(accountService).updateAccount(testAccount);
    }
    
    @Test
    @DisplayName("اختبار حساب الفائدة المستقبلية")
    void testCalculateFutureInterest() {
        // Given
        Integer months = 12;
        Double expectedFutureInterest = 120.0;
        when(interestCalculator.calculateFutureInterest(testAccount, months)).thenReturn(expectedFutureInterest);
        
        // When
        Double result = interestService.calculateFutureInterest(100L, months);
        
        // Then
        assertEquals(expectedFutureInterest, result);
        verify(interestCalculator).calculateFutureInterest(testAccount, months);
    }
    
    @Test
    @DisplayName("اختبار الحصول على تقرير الفائدة")
    void testGetInterestReport() {
        // Given
        when(interestCalculator.calculateInterest(testAccount)).thenReturn(10.0);
        when(interestCalculator.calculateFutureInterest(testAccount, 60)).thenReturn(600.0);
        testAccount.setInterestStrategyName("simpleInterestStrategy");
        when(simpleInterestStrategy.getStrategyName()).thenReturn("Simple Interest");
        
        // When
        InterestService.InterestReport report = interestService.getInterestReport(100L);
        
        // Then
        assertEquals(testAccount.getAccountNumber(), report.getAccountNumber());
        assertEquals(10.0, report.getMonthlyInterest());
        assertEquals(120.0, report.getYearlyInterest());
        assertEquals(600.0, report.getProjected5YearInterest());
    }
}