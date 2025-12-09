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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountFactory accountFactory;
    private final UserService userService;

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
}