package com.bank.se3bank.accounts.service;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.AccountGroup;
import com.bank.se3bank.accounts.repository.AccountGroupRepository;
import com.bank.se3bank.accounts.repository.AccountRepository;
import com.bank.se3bank.shared.dto.CreateGroupRequest;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.service.UserService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

    private final AccountGroupRepository accountGroupRepository;
    private final AccountRepository accountRepository;
    private final UserService userService;
    
    /**
     * إنشاء مجموعة حسابات جديدة (Composite)
     */
    @Transactional
    public AccountGroup createGroup(CreateGroupRequest request) {
        log.info("🏢 إنشاء مجموعة حسابات جديدة: {}", request.getGroupName());
        
        User owner = userService.getUserById(request.getOwnerId());
        
        AccountGroup group = AccountGroup.builder()
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .groupType(request.getGroupType())
                .user(owner) // Set user for Parent Account entity
                .owner(owner) // Set owner for AccountGroup entity
                .maxAccounts(request.getMaxAccounts())
                .build();
        
        // توليد رقم حساب للمجموعة
        group.setAccountNumber(generateGroupAccountNumber(request.getGroupType()));
        
        AccountGroup savedGroup = accountGroupRepository.save(group);
        
        log.info("✅ تم إنشاء مجموعة حسابات {} برقم {} للمستخدم {}",
                savedGroup.getGroupName(),
                savedGroup.getAccountNumber(),
                owner.getUsername());
        
        return savedGroup;
    }

    /**
     * Overload لتسهيل إنشاء المجموعة من الحقول المباشرة
     */
    @Transactional
    public AccountGroup createGroup(String groupName, String description, String groupType, User owner) {
        CreateGroupRequest request = new CreateGroupRequest();
        request.setGroupName(groupName);
        request.setDescription(description);
        request.setGroupType(groupType);
        request.setOwnerId(owner.getId());
        return createGroup(request);
    }
    
    /**
     * إضافة حساب إلى مجموعة
     */
    @Transactional
    public AccountGroup addAccountToGroup(Long groupId, Long accountId) {
        AccountGroup group = getGroupById(groupId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("الحساب غير موجود: " + accountId));
        
        // التحقق من أن الحساب لا ينتمي لمجموعة أخرى
        if (account.getParentGroup() != null && !account.getParentGroup().equals(group)) {
            throw new IllegalStateException("الحساب ينتمي بالفعل لمجموعة أخرى");
        }
        
        group.add(account);
        AccountGroup savedGroup = accountGroupRepository.save(group);
        
        log.info("➕ تم إضافة الحساب {} إلى المجموعة {}",
                account.getAccountNumber(),
                group.getGroupName());
        
        return savedGroup;
    }
    
    /**
     * إزالة حساب من مجموعة
     */
    @Transactional
    public AccountGroup removeAccountFromGroup(Long groupId, Long accountId) {
        AccountGroup group = getGroupById(groupId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("الحساب غير موجود: " + accountId));
        
        group.remove(account);
        AccountGroup savedGroup = accountGroupRepository.save(group);
        
        log.info("➖ تم إزالة الحساب {} من المجموعة {}",
                account.getAccountNumber(),
                group.getGroupName());
        
        return savedGroup;
    }
    
    /**
     * الحصول على مجموعة بواسطة ID
     */
    public AccountGroup getGroupById(Long groupId) {
        return accountGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("المجموعة غير موجودة: " + groupId));
    }

    /**
     * الحصول على كل المجموعات
     */
    public List<AccountGroup> getAllGroups() {
        return accountGroupRepository.findAll();
    }
    
    /**
     * الحصول على جميع مجموعات المستخدم
     */
    public List<AccountGroup> getUserGroups(Long userId) {
        return accountGroupRepository.findByUserId(userId);
    }
    
    /**
     * الحصول على حسابات مجموعة معينة
     */
    public List<Account> getGroupAccounts(Long groupId) {
        AccountGroup group = getGroupById(groupId);
        return group.getChildAccounts();
    }
    
    /**
     * حساب إجمالي رصيد المجموعة
     */
    public Double getGroupTotalBalance(Long groupId) {
        AccountGroup group = getGroupById(groupId);
        return group.getTotalBalance();
    }
    
    /**
     * تحويل رصيد داخل المجموعة
     */
    @Transactional
    public void transferWithinGroup(Long groupId, String fromAccountNumber, 
                                   String toAccountNumber, Double amount) {
        AccountGroup group = getGroupById(groupId);
        group.transferWithinGroup(fromAccountNumber, toAccountNumber, amount);
        
        log.info("🔄 تم تحويل {} من {} إلى {} داخل المجموعة {}",
                amount, fromAccountNumber, toAccountNumber, group.getGroupName());
    }
    
    /**
     * تجميد/تفعيل جميع حسابات المجموعة
     */
    @Transactional
    public AccountGroup setGroupAccountsStatus(Long groupId, AccountStatus status) {
        AccountGroup group = getGroupById(groupId);
        group.setAllAccountsStatus(status);
        
        AccountGroup savedGroup = accountGroupRepository.save(group);
        
        log.info("🔒 تم تغيير حالة جميع حسابات المجموعة {} إلى {}",
                group.getGroupName(), status.getArabicName());
        
        return savedGroup;
    }
    
    /**
     * الحصول على إحصائيات المجموعة
     */
    public GroupStatistics getGroupStatistics(Long groupId) {
        AccountGroup group = getGroupById(groupId);
        
        long activeAccounts = group.getChildAccounts().stream()
                .filter(account -> account.getStatus() == AccountStatus.ACTIVE)
                .count();
        
        long frozenAccounts = group.getChildAccounts().stream()
                .filter(account -> account.getStatus() == AccountStatus.FROZEN)
                .count();
        
        double averageBalance = group.getAverageBalance();
        Account largestAccount = group.getLargestAccount();
        Account smallestAccount = group.getSmallestAccount();
        
        return GroupStatistics.builder()
                .groupId(groupId)
                .groupName(group.getGroupName())
                .totalAccounts(group.getChildCount())
                .activeAccounts(activeAccounts)
                .frozenAccounts(frozenAccounts)
                .totalBalance(group.getTotalBalance())
                .averageBalance(averageBalance)
                .largestAccountNumber(largestAccount != null ? largestAccount.getAccountNumber() : "N/A")
                .largestAccountBalance(largestAccount != null ? largestAccount.getBalance() : 0.0)
                .smallestAccountNumber(smallestAccount != null ? smallestAccount.getAccountNumber() : "N/A")
                .smallestAccountBalance(smallestAccount != null ? smallestAccount.getBalance() : 0.0)
                .build();
    }
    
    /**
     * توليد رقم حساب للمجموعة
     */
    private String generateGroupAccountNumber(String groupType) {
        String prefix = "GRP-" + groupType.substring(0, 3).toUpperCase() + "-";
        String timestamp = String.valueOf(System.currentTimeMillis() % 1000000);
        return prefix + timestamp;
    }
    
    /**
     * DTO لإحصائيات المجموعة
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GroupStatistics {
        private Long groupId;
        private String groupName;
        private Integer totalAccounts;
        private Long activeAccounts;
        private Long frozenAccounts;
        private Double totalBalance;
        private Double averageBalance;
        private String largestAccountNumber;
        private Double largestAccountBalance;
        private String smallestAccountNumber;
        private Double smallestAccountBalance;
    }
}