// 📁 src/main/java/com/bank/se3bank/accounts/service/AccountService.java (المكتمل)
package com.bank.se3bank.accounts.service;

import com.bank.se3bank.accounts.factory.AccountFactory;
import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.AccountGroup;
import com.bank.se3bank.accounts.model.SavingsAccount;
import com.bank.se3bank.accounts.repository.AccountRepository;
import com.bank.se3bank.shared.dto.CreateAccountRequest;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.enums.AccountType;
import com.bank.se3bank.shared.exceptions.AccountNotFoundException;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountFactory accountFactory;
    private final UserService userService;
    private final GroupService groupService;

    // ========== CRUD Operations ==========
    
    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        log.info("📝 إنشاء حساب جديد: {}", request.getAccountType());
        
        User user = userService.getUserById(request.getUserId());
        Account account = accountFactory.createAccount(request, user);
        
        Account savedAccount = accountRepository.save(account);
        
        log.info("✅ تم إنشاء حساب {} برقم {} للمستخدم {}", 
                savedAccount.getAccountType().getArabicName(),
                savedAccount.getAccountNumber(),
                user.getUsername());
        
        return savedAccount;
    }
    
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
    
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
    
    @Transactional
    public Account updateAccount(Account account) {
        Account existing = getAccountById(account.getId());
        
        // تحديث الحقول المسموح بها
        existing.setBalance(account.getBalance());
        existing.setStatus(account.getStatus());
        existing.setInterestRate(account.getInterestRate());
        existing.setOverdraftLimit(account.getOverdraftLimit());
        existing.setMinimumBalance(account.getMinimumBalance());
        existing.setInterestStrategyName(account.getInterestStrategyName());
        existing.setLastInterestCalculation(account.getLastInterestCalculation());
        existing.setTotalInterestEarned(account.getTotalInterestEarned());
        
        return accountRepository.save(existing);
    }
    
    @Transactional
    public void deleteAccount(Long accountId) {
        Account account = getAccountById(accountId);
        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
        log.info("🗑️ تم إغلاق الحساب: {}", accountId);
    }
    
    // ========== Query Operations ==========
    
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    public Page<Account> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }
    
    public List<Account> getUserAccounts(Long userId) {
        return accountRepository.findByUserId(userId);
    }
    
    public List<Account> getActiveAccounts() {
        return accountRepository.findByStatus(AccountStatus.ACTIVE);
    }
    
    public List<Account> getUserActiveAccounts(Long userId) {
        return accountRepository.findByUserIdAndStatus(userId, AccountStatus.ACTIVE);
    }
    
    public List<Account> getAccountsByType(AccountType accountType) {
        return accountRepository.findByAccountType(accountType);
    }
    
    public List<Account> searchAccounts(String searchText) {
        return accountRepository.searchAccounts(searchText);
    }
    
    public Page<Account> searchAccounts(String searchText, Pageable pageable) {
        return accountRepository.searchAccounts(searchText, pageable);
    }
    
    // ========== Status Management ==========
    
    @Transactional
    public Account updateAccountStatus(Long accountId, AccountStatus status) {
        Account account = getAccountById(accountId);
        
        // التحقق من صحة الانتقال
        if (!account.getStatus().canTransitionTo(status)) {
            throw new IllegalStateException("انتقال غير مسموح من " + 
                    account.getStatus().getArabicName() + " إلى " + 
                    status.getArabicName());
        }
        
        account.setStatus(status);
        
        // تسجيل تاريخ التغيير إذا لزم
        if (status == AccountStatus.ACTIVE) {
            account.setUpdatedAt(LocalDateTime.now());
        }
        
        log.info("🔄 تغيير حالة الحساب {} من {} إلى {}", 
                account.getAccountNumber(),
                account.getStatus().getArabicName(),
                status.getArabicName());
        
        return accountRepository.save(account);
    }
    
    @Transactional
    public Account activateAccount(Long accountId) {
        return updateAccountStatus(accountId, AccountStatus.ACTIVE);
    }
    
    @Transactional
    public Account freezeAccount(Long accountId) {
        return updateAccountStatus(accountId, AccountStatus.FROZEN);
    }
    
    @Transactional
    public Account suspendAccount(Long accountId) {
        return updateAccountStatus(accountId, AccountStatus.SUSPENDED);
    }
    
    @Transactional
    public Account closeAccount(Long accountId) {
        return updateAccountStatus(accountId, AccountStatus.CLOSED);
    }
    
    public boolean isActive(Long accountId) {
        Account account = getAccountById(accountId);
        return account.getStatus().isActive();
    }
    
    public boolean isActive(String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        return account.getStatus().isActive();
    }
    
    // ========== Balance Operations ==========
    
    @Transactional
    public Account updateBalance(Long accountId, Double newBalance) {
        Account account = getAccountById(accountId);
        
        if (newBalance < 0 && account.getOverdraftLimit() == null) {
            throw new IllegalStateException("لا يمكن أن يكون الرصيد سالباً بدون حماية السحب على المكشوف");
        }
        
        if (account.getMinimumBalance() != null && newBalance < account.getMinimumBalance()) {
            log.warn("⚠️ رصيد الحساب {} أقل من الحد الأدنى المطلوب", account.getAccountNumber());
        }
        
        account.setBalance(newBalance);
        
        log.info("💰 تحديث رصيد الحساب {} إلى {}", 
                account.getAccountNumber(), newBalance);
        
        return accountRepository.save(account);
    }
    
    @Transactional
    public Account deposit(Long accountId, Double amount) {
        Account account = getAccountById(accountId);
        Double oldBalance = account.getBalance();
        account.deposit(amount);
        
        log.info("📥 إيداع {} في الحساب {} (من {} إلى {})", 
                amount, account.getAccountNumber(), oldBalance, account.getBalance());
        
        return accountRepository.save(account);
    }
    
    @Transactional
    public Account withdraw(Long accountId, Double amount) {
        Account account = getAccountById(accountId);
        Double oldBalance = account.getBalance();
        
        // التحقق من حدود السحب لحسابات التوفير
        if (account instanceof SavingsAccount savingsAccount) {
            if (!savingsAccount.canWithdrawThisMonth()) {
                throw new IllegalStateException("تم تجاوز حد السحب الشهري");
            }
            savingsAccount.setWithdrawalsThisMonth(savingsAccount.getWithdrawalsThisMonth() + 1);
        }
        
        account.withdraw(amount);
        
        log.info("💰 سحب {} من الحساب {} (من {} إلى {})", 
                amount, account.getAccountNumber(), oldBalance, account.getBalance());
        
        return accountRepository.save(account);
    }
    
    @Transactional
    public Account transfer(Long fromAccountId, Long toAccountId, Double amount) {
        Account fromAccount = getAccountById(fromAccountId);
        Account toAccount = getAccountById(toAccountId);
        
        fromAccount.transferTo(toAccount, amount);
        
        log.info("💸 تحويل {} من {} إلى {}", 
                amount, fromAccount.getAccountNumber(), toAccount.getAccountNumber());
        
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        
        return fromAccount;
    }
    
    public Double getTotalBalanceByUser(Long userId) {
        Double total = accountRepository.getTotalBalanceByUserId(userId);
        return total != null ? total : 0.0;
    }
    
    public Double getAvailableBalance(Long accountId) {
        Account account = getAccountById(accountId);
        return account.getAvailableBalance();
    }
    
    // ========== Interest Operations ==========
    
    @Transactional
    public Account applyInterest(Long accountId, Double interestAmount) {
        Account account = getAccountById(accountId);
        
        account.addInterest(interestAmount);
        account.setLastInterestCalculation(LocalDateTime.now());
        
        log.info("📈 تطبيق فائدة {} على الحساب {} (إجمالي الفوائد: {})", 
                interestAmount, account.getAccountNumber(), account.getTotalInterestEarned());
        
        return accountRepository.save(account);
    }
    
    @Transactional
    public Account updateInterestStrategy(Long accountId, String strategyName) {
        Account account = getAccountById(accountId);
        account.setInterestStrategyName(strategyName);
        
        log.info("🔄 تحديث استراتيجية الفائدة للحساب {} إلى {}", 
                account.getAccountNumber(), strategyName);
        
        return accountRepository.save(account);
    }
    
    // ========== Group Operations ==========
    
    @Transactional
    public AccountGroup createAccountGroup(String groupName, String description, 
                                          String groupType, User owner) {
        return groupService.createGroup(groupName, description, groupType, owner);
    }
    
    @Transactional
    public void addAccountToGroup(Long accountId, Long groupId) {
        groupService.addAccountToGroup(groupId, accountId);
    }
    
    @Transactional
    public void removeAccountFromGroup(Long accountId, Long groupId) {
        groupService.removeAccountFromGroup(groupId, accountId);
    }
    
    public List<AccountGroup> getUserGroups(Long userId) {
        return groupService.getUserGroups(userId);
    }
    
    // ========== Validation & Checks ==========
    
    public boolean accountExists(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }
    
    public Long getAccountCountByUser(Long userId) {
        return accountRepository.countByUserId(userId);
    }
    
    public boolean canWithdraw(Long accountId, Double amount) {
        Account account = getAccountById(accountId);
        return account.canWithdraw(amount);
    }
    
    public boolean isEligibleForInterest(Long accountId) {
        Account account = getAccountById(accountId);
        return account.getBalance() > 0 && 
               account.getStatus().isActive() &&
               (account.getLastInterestCalculation() == null ||
                account.getLastInterestCalculation().isBefore(LocalDateTime.now().minusDays(28)));
    }
    
    // ========== Statistics & Reports ==========
    
    public AccountStatistics getAccountStatistics() {
        List<Account> allAccounts = getAllAccounts();
        
        long totalAccounts = allAccounts.size();
        long activeAccounts = allAccounts.stream()
                .filter(account -> account.getStatus().isActive())
                .count();
        long frozenAccounts = allAccounts.stream()
                .filter(account -> account.getStatus() == AccountStatus.FROZEN)
                .count();
        long suspendedAccounts = allAccounts.stream()
                .filter(account -> account.getStatus() == AccountStatus.SUSPENDED)
                .count();
        long closedAccounts = allAccounts.stream()
                .filter(account -> account.getStatus() == AccountStatus.CLOSED)
                .count();
        
        double totalBalance = allAccounts.stream()
                .mapToDouble(Account::getBalance)
                .sum();
        double totalInterestEarned = allAccounts.stream()
                .mapToDouble(account -> account.getTotalInterestEarned() != null ? 
                        account.getTotalInterestEarned() : 0.0)
                .sum();
        double averageBalance = totalAccounts > 0 ? totalBalance / totalAccounts : 0;
        
        // إحصائيات حسب النوع
        Map<AccountType, Long> accountsByType = allAccounts.stream()
                .collect(Collectors.groupingBy(Account::getAccountType, Collectors.counting()));
        
        return AccountStatistics.builder()
                .totalAccounts(totalAccounts)
                .activeAccounts(activeAccounts)
                .frozenAccounts(frozenAccounts)
                .suspendedAccounts(suspendedAccounts)
                .closedAccounts(closedAccounts)
                .totalBalance(totalBalance)
                .totalInterestEarned(totalInterestEarned)
                .averageBalance(averageBalance)
                .accountsByType(accountsByType)
                .build();
    }
    
    public AccountSummary getAccountSummary(Long accountId) {
        Account account = getAccountById(accountId);
        
        return AccountSummary.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .availableBalance(account.getAvailableBalance())
                .status(account.getStatus())
                .interestRate(account.getInterestRate())
                .overdraftLimit(account.getOverdraftLimit())
                .minimumBalance(account.getMinimumBalance())
                .interestStrategyName(account.getInterestStrategyName())
                .totalInterestEarned(account.getTotalInterestEarned())
                .lastInterestCalculation(account.getLastInterestCalculation())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
    
    // ========== DTOs ==========
    
    @lombok.Data
    @lombok.Builder
    public static class AccountStatistics {
        private Long totalAccounts;
        private Long activeAccounts;
        private Long frozenAccounts;
        private Long suspendedAccounts;
        private Long closedAccounts;
        private Double totalBalance;
        private Double totalInterestEarned;
        private Double averageBalance;
        private Map<AccountType, Long> accountsByType;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class AccountSummary {
        private Long accountId;
        private String accountNumber;
        private AccountType accountType;
        private Double balance;
        private Double availableBalance;
        private AccountStatus status;
        private Double interestRate;
        private Double overdraftLimit;
        private Double minimumBalance;
        private String interestStrategyName;
        private Double totalInterestEarned;
        private LocalDateTime lastInterestCalculation;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}