package com.bank.se3bank.accounts.service;

import com.bank.se3bank.accounts.factory.AccountFactory;
import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.AccountGroup;
import com.bank.se3bank.accounts.repository.AccountRepository;
import com.bank.se3bank.shared.dto.CreateAccountRequest;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.exceptions.AccountNotFoundException;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountFactory accountFactory;
    private final UserService userService;

    // ========== الدوال الموجودة سابقاً ==========
    
    /**
     * إنشاء حساب جديد باستخدام Factory Pattern
     */
    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        log.info("📝 إنشاء حساب جديد: {}", request.getAccountType());
        
        User user = userService.getUserById(request.getUserId());
        
        // استخدام Factory Pattern لإنشاء الحساب المناسب
        Account account = accountFactory.createAccount(request, user);
        
        Account savedAccount = accountRepository.save(account);
        
        log.info("✅ تم إنشاء حساب {} برقم {} للمستخدم {}", 
                savedAccount.getAccountType().getArabicName(),
                savedAccount.getAccountNumber(),
                user.getUsername());
        
        return savedAccount;
    }

    /**
     * الحصول على حساب بواسطة رقم الحساب
     */
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    /**
     * الحصول على حساب بواسطة ID
     */
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    /**
     * الحصول على جميع حسابات المستخدم
     */
    public List<Account> getUserAccounts(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    /**
     * تحديث رصيد الحساب
     */
    @Transactional
    public Account updateBalance(Long accountId, Double newBalance) {
        Account account = getAccountById(accountId);
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }

    /**
     * تغيير حالة الحساب
     */
    @Transactional
    public Account updateAccountStatus(Long accountId, AccountStatus status) {
        Account account = getAccountById(accountId);
        account.setStatus(status);
        return accountRepository.save(account);
    }

    /**
     * حذف حساب (تغيير الحالة إلى مغلق)
     */
    @Transactional
    public void closeAccount(Long accountId) {
        Account account = getAccountById(accountId);
        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
        log.info("تم إغلاق الحساب: {}", accountId);
    }

    /**
     * الحصول على إجمالي رصيد جميع حسابات المستخدم
     */
    public Double getTotalBalanceByUser(Long userId) {
        Double totalBalance = accountRepository.getTotalBalanceByUserId(userId);
        return totalBalance != null ? totalBalance : 0.0;
    }

    /**
     * إنشاء مجموعة حسابات (Composite Pattern)
     */
    @Transactional
    public AccountGroup createAccountGroup(String groupName, String description, 
                                          String groupType, User owner) {
        AccountGroup group = AccountGroup.builder()
                .groupName(groupName)
                .description(description)
                .groupType(groupType)
                .owner(owner)
                .build();
        
        log.info("تم إنشاء مجموعة حسابات: {} للمستخدم: {}", 
                groupName, owner.getUsername());
        
        return group;
    }

    /**
     * إضافة حساب إلى مجموعة (Composite Pattern)
     */
    @Transactional
    public void addAccountToGroup(Long accountId, AccountGroup group) {
        Account account = getAccountById(accountId);
        group.addAccount(account);
        log.info("تم إضافة الحساب {} إلى المجموعة {}", 
                account.getAccountNumber(), group.getGroupName());
    }

    /**
     * التحقق من وجود حساب بالرقم
     */
    public boolean accountExists(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }

    /**
     * الحصول على عدد حسابات المستخدم
     */
    public Long getAccountCountByUser(Long userId) {
        return accountRepository.countByUserId(userId);
    }

    // ========== الدوال الجديدة المطلوبة ==========

    /**
     * الحصول على جميع الحسابات في النظام
     * @return قائمة بجميع الحسابات
     */
    public List<Account> getAllAccounts() {
        log.info("📋 جلب جميع الحسابات في النظام");
        return accountRepository.findAll();
    }

    /**
     * تحديث معلومات حساب بشكل كامل
     * @param account الحساب المحدث
     * @return الحساب بعد التحديث
     */
    @Transactional
    public Account updateAccount(Account account) {
        log.info("🔄 تحديث حساب: {}", account.getAccountNumber());
        
        // التحقق من وجود الحساب
        Account existingAccount = getAccountById(account.getId());
        
        // تحديث الحقول المسموح بتحديثها
        existingAccount.setBalance(account.getBalance());
        existingAccount.setStatus(account.getStatus());
        existingAccount.setInterestRate(account.getInterestRate());
        existingAccount.setOverdraftLimit(account.getOverdraftLimit());
        existingAccount.setMinimumBalance(account.getMinimumBalance());
        existingAccount.setLastInterestCalculation(account.getLastInterestCalculation());
        
        // تحديث إستراتيجية الفائدة إذا كانت موجودة
        if (account.getInterestStrategyName() != null) {
            existingAccount.setInterestStrategyName(account.getInterestStrategyName());
        }
        
        // تحديث إجمالي الفائدة المكتسبة
        if (account.getTotalInterestEarned() != null) {
            existingAccount.setTotalInterestEarned(account.getTotalInterestEarned());
        }
        
        Account updatedAccount = accountRepository.save(existingAccount);
        
        log.info("✅ تم تحديث الحساب: {}", updatedAccount.getAccountNumber());
        return updatedAccount;
    }

    /**
     * التحقق إذا كان الحساب نشطاً
     * @param accountId معرف الحساب
     * @return true إذا كان الحساب نشطاً
     */
    public boolean isActive(Long accountId) {
        Account account = getAccountById(accountId);
        return account.getStatus() == AccountStatus.ACTIVE;
    }

    /**
     * التحقق إذا كان الحساب نشطاً برقم الحساب
     * @param accountNumber رقم الحساب
     * @return true إذا كان الحساب نشطاً
     */
    public boolean isActive(String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        return account.getStatus() == AccountStatus.ACTIVE;
    }

    // ========== دوال إضافية مفيدة ==========

    /**
     * الحصول على الحسابات النشطة فقط
     * @return قائمة الحسابات النشطة
     */
    public List<Account> getActiveAccounts() {
        log.info("📋 جلب الحسابات النشطة");
        return accountRepository.findByStatus(AccountStatus.ACTIVE);
    }

    /**
     * الحصول على الحسابات النشطة لمستخدم معين
     * @param userId معرف المستخدم
     * @return قائمة الحسابات النشطة للمستخدم
     */
    public List<Account> getUserActiveAccounts(Long userId) {
        log.info("📋 جلب الحسابات النشطة للمستخدم: {}", userId);
        return accountRepository.findByUserIdAndStatus(userId, AccountStatus.ACTIVE);
    }

    /**
     * تفعيل حساب (تغيير الحالة إلى نشط)
     * @param accountId معرف الحساب
     * @return الحساب المفعل
     */
    @Transactional
    public Account activateAccount(Long accountId) {
        log.info("✅ تفعيل الحساب: {}", accountId);
        return updateAccountStatus(accountId, AccountStatus.ACTIVE);
    }

    /**
     * تجميد حساب (تغيير الحالة إلى مجمد)
     * @param accountId معرف الحساب
     * @return الحساب المجمد
     */
    @Transactional
    public Account freezeAccount(Long accountId) {
        log.info("❄️ تجميد الحساب: {}", accountId);
        return updateAccountStatus(accountId, AccountStatus.FROZEN);
    }

    /**
     * إيقاف حساب مؤقتاً (تغيير الحالة إلى موقوف)
     * @param accountId معرف الحساب
     * @return الحساب الموقف
     */
    @Transactional
    public Account suspendAccount(Long accountId) {
        log.info("⏸️ إيقاف الحساب مؤقتاً: {}", accountId);
        return updateAccountStatus(accountId, AccountStatus.SUSPENDED);
    }

    /**
     * الحصول على إحصائيات الحسابات
     * @return إحصائيات الحسابات
     */
    public AccountStatistics getAccountStatistics() {
        List<Account> allAccounts = getAllAccounts();
        
        long totalAccounts = allAccounts.size();
        long activeAccounts = allAccounts.stream()
                .filter(account -> account.getStatus() == AccountStatus.ACTIVE)
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
        double averageBalance = totalAccounts > 0 ? totalBalance / totalAccounts : 0;
        
        return AccountStatistics.builder()
                .totalAccounts(totalAccounts)
                .activeAccounts(activeAccounts)
                .frozenAccounts(frozenAccounts)
                .suspendedAccounts(suspendedAccounts)
                .closedAccounts(closedAccounts)
                .totalBalance(totalBalance)
                .averageBalance(averageBalance)
                .build();
    }

    /**
     * البحث عن حسابات برقم الحساب أو اسم المستخدم
     * @param searchText نص البحث
     * @return قائمة الحسابات المطابقة
     */
    public List<Account> searchAccounts(String searchText) {
        log.info("🔍 البحث عن حسابات: {}", searchText);
        
        // البحث برقم الحساب
        try {
            Account account = getAccountByNumber(searchText);
            return List.of(account);
        } catch (AccountNotFoundException e) {
            // البحث في جميع الحسابات إذا كان النص جزءاً من رقم الحساب
            return accountRepository.findAll().stream()
                    .filter(account -> account.getAccountNumber().contains(searchText))
                    .toList();
        }
    }

    /**
     * الحصول على حسابات بنوع معين
     * @param accountType نوع الحساب
     * @return قائمة الحسابات من النوع المطلوب
     */
    public List<Account> getAccountsByType(com.bank.se3bank.shared.enums.AccountType accountType) {
        log.info("📋 جلب حسابات من النوع: {}", accountType.getArabicName());
        return accountRepository.findByAccountType(accountType);
    }

    /**
     * تحديث استراتيجية الفائدة لحساب
     * @param accountId معرف الحساب
     * @param strategyName اسم الاستراتيجية
     * @return الحساب المحدث
     */
    @Transactional
    public Account updateInterestStrategy(Long accountId, String strategyName) {
        log.info("🔄 تحديث استراتيجية الفائدة للحساب {} إلى {}", accountId, strategyName);
        
        Account account = getAccountById(accountId);
        account.setInterestStrategyName(strategyName);
        
        return accountRepository.save(account);
    }

    public boolean isAccountActive(Account account) {
        return account != null && account.getStatus() == AccountStatus.ACTIVE;
    }

    /**
     * التحقق إذا كان حساب مؤهلاً للفائدة
     */
    public boolean isAccountEligibleForInterest(Account account) {
        return isAccountActive(account) &&
            account.getBalance() > 0 &&
            (account.getLastInterestCalculation() == null ||
                account.getLastInterestCalculation().isBefore(LocalDateTime.now().minusDays(28)));
    }

    /**
     * DTO لإحصائيات الحسابات
     */
    @lombok.Data
    @lombok.Builder
    public static class AccountStatistics {
        private Long totalAccounts;
        private Long activeAccounts;
        private Long frozenAccounts;
        private Long suspendedAccounts;
        private Long closedAccounts;
        private Double totalBalance;
        private Double averageBalance;
    }
}