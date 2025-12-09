package com.bank.se3bank.facade;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.service.AccountService;
import com.bank.se3bank.accounts.service.DecoratorService;
import com.bank.se3bank.accounts.service.GroupService;
import com.bank.se3bank.notifications.service.NotificationService;
import com.bank.se3bank.shared.dto.*;
import com.bank.se3bank.shared.enums.TransactionStatus;
import com.bank.se3bank.shared.exceptions.InsufficientBalanceException;
import com.bank.se3bank.shared.exceptions.InvalidTransactionException;
import com.bank.se3bank.transactions.model.Transaction;
import com.bank.se3bank.transactions.service.TransactionService;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * تطبيق Facade Pattern
 * واجهة مبسطة للعمليات البنكية المعقدة
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BankFacade {
    
    private final AccountService accountService;
    private final UserService userService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;
    private final GroupService groupService;
    private final DecoratorService decoratorService;
    
    // ========== Customer Operations ==========
    
    /**
     * فتح حساب جديد (عملية كاملة)
     */
    @Transactional
    public AccountOpenResponse openNewAccount(OpenAccountRequest request) {
        log.info("🏦 فتح حساب جديد للمستخدم: {}", request.getUserId());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. التحقق من المستخدم
            User user = userService.getUserById(request.getUserId());
            
            // 2. إنشاء الحساب باستخدام Factory Pattern
            CreateAccountRequest createRequest = new CreateAccountRequest();
            createRequest.setAccountType(request.getAccountType());
            createRequest.setUserId(request.getUserId());
            createRequest.setInitialBalance(request.getInitialBalance());
            createRequest.setInterestRate(request.getInterestRate());
            createRequest.setOverdraftLimit(request.getOverdraftLimit());
            
            Account account = accountService.createAccount(createRequest);
            
            // 3. إضافة ديكورات إذا طلب
            if (request.getDecorators() != null && !request.getDecorators().isEmpty()) {
                for (AddDecoratorRequest decoratorRequest : request.getDecorators()) {
                    decoratorRequest.setAccountId(account.getId());
                    decoratorService.addDecorator(decoratorRequest);
                }
            }
            
            // 4. إرسال إشعار
            notificationService.sendAccountCreatedNotification(user, account);
            
            // 5. تسجيل العملية
            logTransaction("ACCOUNT_OPEN", user.getId(), account.getId(), 
                          request.getInitialBalance(), "تم فتح حساب جديد");
            
            long duration = System.currentTimeMillis() - startTime;
            
            return AccountOpenResponse.builder()
                    .success(true)
                    .accountNumber(account.getAccountNumber())
                    .accountType(account.getAccountType())
                    .balance(account.getBalance())
                    .message("تم فتح الحساب بنجاح")
                    .processingTimeMs(duration)
                    .timestamp(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ فشل فتح الحساب: {}", e.getMessage());
            throw new InvalidTransactionException("فشل فتح الحساب: " + e.getMessage());
        }
    }
    
    /**
     * تحويل أموال (عملية كاملة)
     */
    @Transactional
    public TransferResponse transferMoney(TransferRequest request) {
        log.info("💸 تحويل أموال من {} إلى {} بمبلغ {}", 
                request.getFromAccountNumber(), 
                request.getToAccountNumber(), 
                request.getAmount());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. التحقق من الحسابات
            Account fromAccount = accountService.getAccountByNumber(request.getFromAccountNumber());
            Account toAccount = accountService.getAccountByNumber(request.getToAccountNumber());
            
            // 2. التحقق من الرصيد
            if (!fromAccount.canWithdraw(request.getAmount())) {
                throw new InsufficientBalanceException(
                        fromAccount.getBalance(), request.getAmount());
            }
            
            // 3. تنفيذ التحويل
            fromAccount.withdraw(request.getAmount());
            toAccount.deposit(request.getAmount());
            
            // 4. حفظ الحسابات المحدثة
            accountService.updateBalance(fromAccount.getId(), fromAccount.getBalance());
            accountService.updateBalance(toAccount.getId(), toAccount.getBalance());
            
            // 5. تسجيل المعاملة
            Transaction transaction = transactionService.createTransaction(
                    fromAccount, toAccount, request.getAmount(), request.getDescription());
            
            // 6. إرسال إشعارات
            notificationService.sendTransferNotification(
                    fromAccount.getUser(), toAccount.getUser(), 
                    request.getAmount(), transaction.getTransactionId());
            
            // 7. تسجيل التدقيق
            logTransaction("MONEY_TRANSFER", fromAccount.getUser().getId(), 
                          toAccount.getUser().getId(), request.getAmount(), 
                          request.getDescription());
            
            long duration = System.currentTimeMillis() - startTime;
            
            return TransferResponse.builder()
                    .success(true)
                    .transactionId(transaction.getTransactionId())
                    .fromAccount(fromAccount.getAccountNumber())
                    .toAccount(toAccount.getAccountNumber())
                    .amount(request.getAmount())
                    .newFromBalance(fromAccount.getBalance())
                    .newToBalance(toAccount.getBalance())
                    .status(TransactionStatus.COMPLETED)
                    .message("تم التحويل بنجاح")
                    .processingTimeMs(duration)
                    .timestamp(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ فشل التحويل: {}", e.getMessage());
            throw new InvalidTransactionException("فشل التحويل: " + e.getMessage());
        }
    }
    
    /**
     * سحب أموال (عملية كاملة)
     */
    @Transactional
    public WithdrawalResponse withdrawMoney(WithdrawalRequest request) {
        log.info("💰 سحب أموال من {} بمبلغ {}", 
                request.getAccountNumber(), request.getAmount());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. التحقق من الحساب
            Account account = accountService.getAccountByNumber(request.getAccountNumber());
            
            // 2. التحقق من الرصيد
            if (!account.canWithdraw(request.getAmount())) {
                throw new InsufficientBalanceException(
                        account.getBalance(), request.getAmount());
            }
            
            // 3. تنفيذ السحب
            Double oldBalance = account.getBalance();
            account.withdraw(request.getAmount());
            
            // 4. حفظ الحساب المحدث
            accountService.updateBalance(account.getId(), account.getBalance());
            
            // 5. تسجيل المعاملة
            Transaction transaction = transactionService.createWithdrawalTransaction(
                    account, request.getAmount(), request.getDescription());
            
            // 6. إرسال إشعار
            notificationService.sendWithdrawalNotification(
                    account.getUser(), request.getAmount(), 
                    oldBalance, account.getBalance());
            
            // 7. تسجيل التدقيق
            logTransaction("WITHDRAWAL", account.getUser().getId(), null, 
                          request.getAmount(), request.getDescription());
            
            long duration = System.currentTimeMillis() - startTime;
            
            return WithdrawalResponse.builder()
                    .success(true)
                    .transactionId(transaction.getTransactionId())
                    .accountNumber(account.getAccountNumber())
                    .amount(request.getAmount())
                    .oldBalance(oldBalance)
                    .newBalance(account.getBalance())
                    .status(TransactionStatus.COMPLETED)
                    .message("تم السحب بنجاح")
                    .processingTimeMs(duration)
                    .timestamp(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ فشل السحب: {}", e.getMessage());
            throw new InvalidTransactionException("فشل السحب: " + e.getMessage());
        }
    }
    
    /**
     * إيداع أموال (عملية كاملة)
     */
    @Transactional
    public DepositResponse depositMoney(DepositRequest request) {
        log.info("📥 إيداع أموال في {} بمبلغ {}", 
                request.getAccountNumber(), request.getAmount());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. التحقق من الحساب
            Account account = accountService.getAccountByNumber(request.getAccountNumber());
            
            // 2. تنفيذ الإيداع
            Double oldBalance = account.getBalance();
            account.deposit(request.getAmount());
            
            // 3. حفظ الحساب المحدث
            accountService.updateBalance(account.getId(), account.getBalance());
            
            // 4. تسجيل المعاملة
            Transaction transaction = transactionService.createDepositTransaction(
                    account, request.getAmount(), request.getDescription());
            
            // 5. إرسال إشعار
            notificationService.sendDepositNotification(
                    account.getUser(), request.getAmount(), 
                    oldBalance, account.getBalance());
            
            // 6. تسجيل التدقيق
            logTransaction("DEPOSIT", account.getUser().getId(), null, 
                          request.getAmount(), request.getDescription());
            
            long duration = System.currentTimeMillis() - startTime;
            
            return DepositResponse.builder()
                    .success(true)
                    .transactionId(transaction.getTransactionId())
                    .accountNumber(account.getAccountNumber())
                    .amount(request.getAmount())
                    .oldBalance(oldBalance)
                    .newBalance(account.getBalance())
                    .status(TransactionStatus.COMPLETED)
                    .message("تم الإيداع بنجاح")
                    .processingTimeMs(duration)
                    .timestamp(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ فشل الإيداع: {}", e.getMessage());
            throw new InvalidTransactionException("فشل الإيداع: " + e.getMessage());
        }
    }
    
    /**
     * الحصول على ملخص حساب (جميع المعلومات في مكان واحد)
     */
    public AccountSummary getAccountSummary(String accountNumber) {
        log.info("📊 إنشاء ملخص للحساب: {}", accountNumber);
        
        try {
            // 1. الحصول على الحساب الأساسي
            Account account = accountService.getAccountByNumber(accountNumber);
            
            // 2. الحصول على جميع الديكورات
            List<com.bank.se3bank.accounts.decorators.AccountDecorator> decorators = 
                    decoratorService.getActiveDecorators(account.getId());
            
            // 3. الحصول على المعاملات الأخيرة
            List<Transaction> recentTransactions = 
                    transactionService.getRecentTransactions(account.getId(), 10);
            
            // 4. الحصول على معلومات المستخدم
            User user = account.getUser();
            
            // 5. حساب الإحصائيات
            Double totalDeposits = transactionService.getTotalDeposits(account.getId());
            Double totalWithdrawals = transactionService.getTotalWithdrawals(account.getId());
            
            return AccountSummary.builder()
                    .accountNumber(account.getAccountNumber())
                    .accountType(account.getAccountType())
                    .balance(account.getBalance())
                    .availableBalance(account.getAvailableBalance())
                    .status(account.getStatus())
                    .createdAt(account.getCreatedAt())
                    .userName(user.getFullName())
                    .userEmail(user.getEmail())
                    .decorators(decorators.stream()
                            .map(d -> d.getDecoratorName())
                            .toList())
                    .recentTransactions(recentTransactions)
                    .totalDeposits(totalDeposits != null ? totalDeposits : 0.0)
                    .totalWithdrawals(totalWithdrawals != null ? totalWithdrawals : 0.0)
                    .netFlow((totalDeposits != null ? totalDeposits : 0.0) - 
                            (totalWithdrawals != null ? totalWithdrawals : 0.0))
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ فشل إنشاء الملخص: {}", e.getMessage());
            throw new InvalidTransactionException("فشل إنشاء الملخص: " + e.getMessage());
        }
    }
    
    /**
     * الحصول على ملخص المستخدم (جميع حساباتهم)
     */
    public UserSummary getUserSummary(Long userId) {
        log.info("👤 إنشاء ملخص للمستخدم: {}", userId);
        
        try {
            // 1. الحصول على المستخدم
            User user = userService.getUserById(userId);
            
            // 2. الحصول على جميع حسابات المستخدم
            List<Account> accounts = accountService.getUserAccounts(userId);
            
            // 3. حساب الإحصائيات
            Double totalBalance = accountService.getTotalBalanceByUser(userId);
            Integer totalAccounts = accounts.size();
            
            // 4. الحصول على مجموعات الحسابات
            List<com.bank.se3bank.accounts.model.AccountGroup> groups = 
                    groupService.getUserGroups(userId);
            
            // 5. حساب المعاملات الأخيرة عبر جميع الحسابات
            List<Transaction> recentTransactions = 
                    transactionService.getRecentTransactionsByUser(userId, 10);
            
            return UserSummary.builder()
                    .userId(userId)
                    .userName(user.getFullName())
                    .userEmail(user.getEmail())
                    .totalAccounts(totalAccounts)
                    .totalBalance(totalBalance)
                    .accounts(accounts)
                    .groups(groups)
                    .recentTransactions(recentTransactions)
                    .lastLogin(user.getLastLogin())
                    .memberSince(user.getCreatedAt())
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ فشل إنشاء ملخص المستخدم: {}", e.getMessage());
            throw new InvalidTransactionException("فشل إنشاء ملخص المستخدم: " + e.getMessage());
        }
    }
    
    /**
     * إنشاء مجموعة حسابات مع حساباتها
     */
    @Transactional
    public GroupCreationResponse createAccountGroup(GroupCreationRequest request) {
        log.info("🏢 إنشاء مجموعة حسابات: {}", request.getGroupName());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. إنشاء المجموعة
            CreateGroupRequest groupRequest = new CreateGroupRequest();
            groupRequest.setGroupName(request.getGroupName());
            groupRequest.setDescription(request.getDescription());
            groupRequest.setGroupType(request.getGroupType());
            groupRequest.setOwnerId(request.getOwnerId());
            groupRequest.setMaxAccounts(request.getMaxAccounts());
            
            com.bank.se3bank.accounts.model.AccountGroup group = 
                    groupService.createGroup(groupRequest);
            
            // 2. إضافة الحسابات للمجموعة
            for (Long accountId : request.getAccountIds()) {
                groupService.addAccountToGroup(group.getId(), accountId);
            }
            
            // 3. إرسال إشعار
            User owner = userService.getUserById(request.getOwnerId());
            notificationService.sendGroupCreatedNotification(owner, group);
            
            long duration = System.currentTimeMillis() - startTime;
            
            return GroupCreationResponse.builder()
                    .success(true)
                    .groupId(group.getId())
                    .groupName(group.getGroupName())
                    .totalAccounts(group.getChildCount())
                    .totalBalance(group.getTotalBalance())
                    .message("تم إنشاء المجموعة بنجاح")
                    .processingTimeMs(duration)
                    .timestamp(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ فشل إنشاء المجموعة: {}", e.getMessage());
            throw new InvalidTransactionException("فشل إنشاء المجموعة: " + e.getMessage());
        }
    }
    
    // ========== Helper Methods ==========
    
    private void logTransaction(String operation, Long fromUserId, Long toUserId, 
                               Double amount, String description) {
        Map<String, Object> auditLog = new HashMap<>();
        auditLog.put("operation", operation);
        auditLog.put("fromUserId", fromUserId);
        auditLog.put("toUserId", toUserId);
        auditLog.put("amount", amount);
        auditLog.put("description", description);
        auditLog.put("timestamp", LocalDateTime.now());
        auditLog.put("facadeOperation", true);
        
        log.info("📝 تدقيق عملية: {}", auditLog);
    }
    
    // ========== Simple Getters (Part of Facade) ==========
    
    public Account getAccountDetails(String accountNumber) {
        return accountService.getAccountByNumber(accountNumber);
    }
    
    public List<Transaction> getAccountTransactions(String accountNumber, int limit) {
        Account account = accountService.getAccountByNumber(accountNumber);
        return transactionService.getRecentTransactions(account.getId(), limit);
    }
    
    public Double getAccountBalance(String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        return account.getBalance();
    }
}