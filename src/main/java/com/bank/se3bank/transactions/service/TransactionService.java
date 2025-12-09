package com.bank.se3bank.transactions.service;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.notifications.service.NotificationService;
import com.bank.se3bank.shared.enums.TransactionStatus;
import com.bank.se3bank.shared.enums.TransactionType;
import com.bank.se3bank.transactions.handlers.ApprovalChainFactory;
import com.bank.se3bank.transactions.handlers.ManagerApprovalHandler;
import com.bank.se3bank.transactions.handlers.TransactionHandler;
import com.bank.se3bank.transactions.model.Transaction;
import com.bank.se3bank.transactions.repository.TransactionRepository;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final ApprovalChainFactory approvalChainFactory;
    private final NotificationService notificationService;
    private final UserService userService;
    
    // ========== Create Transactions ==========
    
    /**
     * إنشاء معاملة تحويل
     */
    @Transactional
    public Transaction createTransaction(Account fromAccount, Account toAccount, 
                                         Double amount, String description) {
        log.info("💸 إنشاء معاملة تحويل من {} إلى {} بمبلغ {}", 
                fromAccount.getAccountNumber(), 
                toAccount.getAccountNumber(), 
                amount);
        
        Transaction transaction = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .transactionType(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description(description)
                .initiatedBy(fromAccount.getUser().getId())
                .build();
        
        return processTransaction(transaction);
    }
    
    /**
     * إنشاء معاملة سحب
     */
    @Transactional
    public Transaction createWithdrawalTransaction(Account account, Double amount, 
                                                   String description) {
        log.info("💰 إنشاء معاملة سحب من {} بمبلغ {}", 
                account.getAccountNumber(), amount);
        
        Transaction transaction = Transaction.builder()
                .fromAccount(account)
                .amount(amount)
                .transactionType(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.PENDING)
                .description(description)
                .initiatedBy(account.getUser().getId())
                .build();
        
        return processTransaction(transaction);
    }
    
    /**
     * إنشاء معاملة إيداع
     */
    @Transactional
    public Transaction createDepositTransaction(Account account, Double amount, 
                                                String description) {
        log.info("📥 إنشاء معاملة إيداع إلى {} بمبلغ {}", 
                account.getAccountNumber(), amount);
        
        Transaction transaction = Transaction.builder()
                .toAccount(account)
                .amount(amount)
                .transactionType(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .description(description)
                .initiatedBy(account.getUser().getId())
                .build();
        
        return processTransaction(transaction);
    }
    
    /**
     * إنشاء معاملة دفع
     */
    @Transactional
    public Transaction createPaymentTransaction(Account fromAccount, String payee, 
                                                Double amount, String description) {
        log.info("🧾 إنشاء معاملة دفع من {} إلى {} بمبلغ {}", 
                fromAccount.getAccountNumber(), payee, amount);
        
        Transaction transaction = Transaction.builder()
                .fromAccount(fromAccount)
                .amount(amount)
                .transactionType(TransactionType.PAYMENT)
                .status(TransactionStatus.PENDING)
                .description(description + " - " + payee)
                .initiatedBy(fromAccount.getUser().getId())
                .build();
        
        return processTransaction(transaction);
    }
    
    // ========== Process Transactions ==========
    
    /**
     * معالجة المعاملة باستخدام Chain of Responsibility
     */
    @Transactional
    public Transaction processTransaction(Transaction transaction) {
        log.info("⚙️ معالجة المعاملة {} باستخدام Chain of Responsibility", 
                transaction.getTransactionId());
        
        try {
            // إنشاء سلسلة الاعتماد
            TransactionHandler approvalChain = approvalChainFactory.createApprovalChain();
            
            // تشغيل السلسلة
            boolean processedSuccessfully = approvalChain.handle(transaction);
            
            // حفظ المعاملة
            Transaction savedTransaction = transactionRepository.save(transaction);
            
            if (processedSuccessfully) {
                log.info("✅ تمت معالجة المعاملة {} بنجاح. الحالة: {}", 
                        savedTransaction.getTransactionId(), 
                        savedTransaction.getStatus());
                
                // إرسال إشعارات إذا كانت ناجحة
                if (savedTransaction.getStatus() == TransactionStatus.COMPLETED) {
                    sendTransactionNotifications(savedTransaction);
                }
            } else {
                log.error("❌ فشلت معالجة المعاملة {}", savedTransaction.getTransactionId());
            }
            
            return savedTransaction;
            
        } catch (Exception e) {
            log.error("❌ خطأ في معالجة المعاملة: {}", e.getMessage());
            transaction.markAsFailed("خطأ في المعالجة: " + e.getMessage());
            return transactionRepository.save(transaction);
        }
    }
    
    /**
     * اعتماد معاملة بواسطة المدير
     */
    @Transactional
    public Transaction approveTransaction(Long transactionId, Long managerId, String comments) {
        log.info("👔 محاولة اعتماد المعاملة {} بواسطة المدير {}", transactionId, managerId);
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("المعاملة غير موجودة"));
        
        if (!transaction.requiresApproval()) {
            throw new IllegalStateException("المعاملة لا تتطلب اعتماداً");
        }
        
        // التحقق من أن المستخدم مدير
        User manager = userService.getUserById(managerId);
        if (!manager.hasRole(com.bank.se3bank.shared.enums.Role.ROLE_MANAGER)) {
            throw new SecurityException("المستخدم ليس مديراً");
        }
        
        // استخدام ManagerApprovalHandler
        ManagerApprovalHandler managerHandler = new ManagerApprovalHandler();
        managerHandler.approveTransaction(transaction, managerId);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // إرسال إشعارات
        sendTransactionNotifications(savedTransaction);
        
        log.info("✅ تم اعتماد المعاملة {} بواسطة المدير {}", 
                savedTransaction.getTransactionId(), managerId);
        
        return savedTransaction;
    }
    
    /**
     * رفض معاملة بواسطة المدير
     */
    @Transactional
    public Transaction rejectTransaction(Long transactionId, Long managerId, 
                                         String reason, String comments) {
        log.info("👔 محاولة رفض المعاملة {} بواسطة المدير {}", transactionId, managerId);
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("المعاملة غير موجودة"));
        
        if (!transaction.requiresApproval()) {
            throw new IllegalStateException("المعاملة لا تتطلب اعتماداً");
        }
        
        // التحقق من أن المستخدم مدير
        User manager = userService.getUserById(managerId);
        if (!manager.hasRole(com.bank.se3bank.shared.enums.Role.ROLE_MANAGER)) {
            throw new SecurityException("المستخدم ليس مديراً");
        }
        
        // استخدام ManagerApprovalHandler
        ManagerApprovalHandler managerHandler = new ManagerApprovalHandler();
        managerHandler.rejectTransaction(transaction, managerId, reason);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("❌ تم رفض المعاملة {} بواسطة المدير {}", 
                savedTransaction.getTransactionId(), managerId);
        
        return savedTransaction;
    }
    
    // ========== Query Methods ==========
    
    /**
     * الحصول على المعاملات الأخيرة لحساب
     */
    public List<Transaction> getRecentTransactions(Long accountId, int limit) {
        // استخدام الدوال Native مع LIMIT
        return transactionRepository.findRecentTransactionsByAccountNative(accountId, limit);
    }
    
    /**
     * الحصول على المعاملات الأخيرة للمستخدم
     */
    public List<Transaction> getRecentTransactionsByUser(Long userId, int limit) {
        // استخدام الدوال Native مع LIMIT
        return transactionRepository.findRecentTransactionsByUserNative(userId, limit);
    }
    
    /**
     * الحصول على إجمالي الإيداعات لحساب
     */
    public Double getTotalDeposits(Long accountId) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now();
        
        // استخدام الدالة المبسطة
        Double total = transactionRepository.getTotalCompletedDeposits(accountId, startOfMonth, endOfMonth);
        return total != null ? total : 0.0;
    }
    
    /**
     * الحصول على إجمالي السحوبات لحساب
     */
    public Double getTotalWithdrawals(Long accountId) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now();
        
        // استخدام الدالة المبسطة
        Double total = transactionRepository.getTotalCompletedWithdrawals(accountId, startOfMonth, endOfMonth);
        return total != null ? total : 0.0;
    }
    
    /**
     * الحصول على معاملات بانتظار الاعتماد
     */
    public List<Transaction> getPendingApprovalTransactions() {
        return transactionRepository.findPendingApprovalTransactions(TransactionStatus.PENDING_APPROVAL);
    }
    
    /**
     * الحصول على معاملة بواسطة ID
     */
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("المعاملة غير موجودة"));
    }
    
    /**
     * الحصول على معاملة بواسطة transactionId
     */
    public Transaction getTransactionByTransactionId(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("المعاملة غير موجودة"));
    }
    
    /**
     * الحصول على جميع معاملات حساب
     */
    public List<Transaction> getAccountTransactions(Long accountId, 
                                                    LocalDateTime startDate, 
                                                    LocalDateTime endDate) {
        return transactionRepository.findTransactionsByAccountAndDateRange(
                accountId, startDate, endDate);
    }
    
    /**
     * الحصول على معاملات مستخدم تم اعتمادها بواسطة مدير معين
     */
    public List<Transaction> getApprovedTransactionsByManager(Long managerId) {
        return transactionRepository.findApprovedTransactionsByUser(
                managerId, TransactionStatus.COMPLETED);
    }
    
    // ========== Helper Methods ==========
    
    /**
     * إرسال إشعارات للمعاملة
     */
    private void sendTransactionNotifications(Transaction transaction) {
        try {
            if (transaction.getStatus() == TransactionStatus.COMPLETED) {
                switch (transaction.getTransactionType()) {
                    case TRANSFER:
                        if (transaction.getFromAccount() != null && transaction.getToAccount() != null) {
                            notificationService.sendTransferNotification(
                                    transaction.getFromAccount().getUser(),
                                    transaction.getToAccount().getUser(),
                                    transaction.getAmount(),
                                    transaction.getTransactionId()
                            );
                        }
                        break;
                        
                    case WITHDRAWAL:
                        if (transaction.getFromAccount() != null) {
                            Double oldBalance = transaction.getFromAccount().getBalance() + transaction.getAmount();
                            notificationService.sendWithdrawalNotification(
                                    transaction.getFromAccount().getUser(),
                                    transaction.getAmount(),
                                    oldBalance,
                                    transaction.getFromAccount().getBalance()
                            );
                        }
                        break;
                        
                    case DEPOSIT:
                        if (transaction.getToAccount() != null) {
                            Double oldBalance = transaction.getToAccount().getBalance() - transaction.getAmount();
                            notificationService.sendDepositNotification(
                                    transaction.getToAccount().getUser(),
                                    transaction.getAmount(),
                                    oldBalance,
                                    transaction.getToAccount().getBalance()
                            );
                        }
                        break;
                        
                    case PAYMENT:
                    case INTEREST:
                    case FEE:
                        // لا إشعارات لهذه الأنواع حالياً
                        break;
                }
            }
        } catch (Exception e) {
            log.error("❌ فشل إرسال إشعارات المعاملة: {}", e.getMessage());
        }
    }
    
    /**
     * إلغاء معاملة
     */
    @Transactional
    public Transaction cancelTransaction(Long transactionId, Long userId, String reason) {
        Transaction transaction = getTransactionById(transactionId);
        
        // التحقق من الصلاحيات
        Long initiatedBy = transaction.getInitiatedBy();
        if (initiatedBy == null || !initiatedBy.equals(userId)) {
            throw new SecurityException("لا يمكن إلغاء معاملة ليست لك");
        }
        
        if (transaction.getStatus() != TransactionStatus.PENDING && 
            transaction.getStatus() != TransactionStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("لا يمكن إلغاء معاملة تمت معالجتها");
        }
        
        transaction.markAsCancelled();
        transaction.setFailureReason("ملغي من قبل المستخدم: " + reason);
        
        log.info("🗑️ تم إلغاء المعاملة {} بواسطة المستخدم {}", transactionId, userId);
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * معالجة المعاملات المعلقة تلقائياً (للمهام المجدولة)
     */
    @Transactional
    public void processPendingTransactions() {
        List<Transaction> pendingTransactions = transactionRepository
                .findByStatus(TransactionStatus.PENDING);
        
        log.info("⏳ معالجة {} معاملة معلقة", pendingTransactions.size());
        
        for (Transaction transaction : pendingTransactions) {
            try {
                processTransaction(transaction);
            } catch (Exception e) {
                log.error("❌ فشل معالجة المعاملة {}: {}", 
                        transaction.getTransactionId(), e.getMessage());
            }
        }
    }
    
    /**
     * الحصول على إحصائيات المعاملات
     */
    public TransactionStatistics getTransactionStatistics(Long accountId) {
        Double totalDeposits = getTotalDeposits(accountId);
        Double totalWithdrawals = getTotalWithdrawals(accountId);
        List<Transaction> recentTransactions = getRecentTransactions(accountId, 5);
        
        return TransactionStatistics.builder()
                .accountId(accountId)
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .netFlow(totalDeposits - totalWithdrawals)
                .recentTransactionCount(recentTransactions.size())
                .build();
    }
    
    /**
     * DTO لإحصائيات المعاملات
     */
    @lombok.Data
    @lombok.Builder
    public static class TransactionStatistics {
        private Long accountId;
        private Double totalDeposits;
        private Double totalWithdrawals;
        private Double netFlow;
        private Integer recentTransactionCount;
    }
}