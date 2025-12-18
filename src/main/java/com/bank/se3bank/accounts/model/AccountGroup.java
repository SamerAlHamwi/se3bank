package com.bank.se3bank.accounts.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import com.bank.se3bank.users.model.User;
import com.bank.se3bank.shared.enums.AccountStatus;
import com.bank.se3bank.shared.enums.AccountType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "account_groups")
@DiscriminatorValue("ACCOUNT_GROUP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccountGroup extends Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "group_name", nullable = false)
    private String groupName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "group_type")
    private String groupType; // FAMILY, BUSINESS, JOINT
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @OneToMany(mappedBy = "parentGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Composite Pattern methods
    public void addAccount(Account account) {
        if (!accounts.contains(account)) {
            accounts.add(account);
            account.setParentGroup(this);
        }
    }
    
    public void removeAccount(Account account) {
        if (accounts.remove(account)) {
            account.setParentGroup(null);
        }
    }
    
    public Double getTotalGroupBalance() {
        return accounts.stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }
    
    public int getAccountCount() {
        return accounts.size();
    }
    
    public List<Account> getActiveAccounts() {
        return accounts.stream()
                .filter(account -> account.getStatus() == AccountStatus.ACTIVE)
                .toList();
    }

    @OneToMany(mappedBy = "parentGroup", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Account> childAccounts = new ArrayList<>();
    
    @Column(name = "max_accounts")
    private Integer maxAccounts;
    
    // ========== Composite Pattern Implementation ==========
    
    @Override
    public void add(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("الحساب لا يمكن أن يكون فارغاً");
        }
        
        if (maxAccounts != null && childAccounts.size() >= maxAccounts) {
            throw new IllegalStateException("تم الوصول إلى الحد الأقصى لعدد الحسابات في المجموعة");
        }
        
        if (!childAccounts.contains(account)) {
            childAccounts.add(account);
            account.setParentGroup(this);
        }
    }
    
    @Override
    public void remove(Account account) {
        if (childAccounts.remove(account)) {
            account.setParentGroup(null);
        }
    }
    
    @Override
    public boolean isComposite() {
        return true;
    }
    
    @Override
    public List<Account> getChildAccounts() {
        return new ArrayList<>(childAccounts);
    }
    
    @Override
    public int getChildCount() {
        return childAccounts.size();
    }
    
    @Override
    public Double getTotalBalance() {
        return childAccounts.stream()
                .filter(account -> account.getStatus() == AccountStatus.ACTIVE)
                .mapToDouble(Account::getBalance)
                .sum();
    }
    
    @Override
    public Double getBalance() {
        return getTotalBalance(); // للمجموعة، الرصيد هو مجموع أرصدة الحسابات الفرعية
    }
    
    @Override
    public AccountType getAccountType() {
        return AccountType.ACCOUNT_GROUP;
    }
    
    // ========== Group Specific Methods ==========
    
    /**
     * الحصول على حسابات فرعية حسب النوع
     */
    public List<Account> getAccountsByType(AccountType type) {
        return childAccounts.stream()
                .filter(account -> account.getAccountType() == type)
                .collect(Collectors.toList());
    }
    
    /**
     * التحقق إذا كانت المجموعة تحتوي على حساب معين
     */
    public boolean containsAccount(Account account) {
        return childAccounts.contains(account);
    }
    
    /**
     * التحقق إذا كانت المجموعة تحتوي على حساب برقم معين
     */
    public boolean containsAccount(String accountNumber) {
        return childAccounts.stream()
                .anyMatch(account -> account.getAccountNumber().equals(accountNumber));
    }
    
    /**
     * نقل رصيد بين حسابات داخل المجموعة
     */
    public void transferWithinGroup(String fromAccountNumber, String toAccountNumber, Double amount) {
        Account fromAccount = childAccounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(fromAccountNumber))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("الحساب المرسل غير موجود في المجموعة"));
        
        Account toAccount = childAccounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(toAccountNumber))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("الحساب المستقبل غير موجود في المجموعة"));
        
        fromAccount.transferTo(toAccount, amount);
    }
    
    /**
     * تجميد/تفعيل جميع الحسابات في المجموعة
     */
    public void setAllAccountsStatus(AccountStatus status) {
        childAccounts.forEach(account -> account.setStatus(status));
    }
    
    /**
     * حساب متوسط رصيد الحسابات في المجموعة
     */
    public Double getAverageBalance() {
        if (childAccounts.isEmpty()) {
            return 0.0;
        }
        return getTotalBalance() / childAccounts.size();
    }
    
    /**
     * الحصول على أكبر حساب في المجموعة
     */
    public Account getLargestAccount() {
        return childAccounts.stream()
                .max((a1, a2) -> Double.compare(a1.getBalance(), a2.getBalance()))
                .orElse(null);
    }
    
    /**
     * الحصول على أصغر حساب في المجموعة
     */
    public Account getSmallestAccount() {
        return childAccounts.stream()
                .min((a1, a2) -> Double.compare(a1.getBalance(), a2.getBalance()))
                .orElse(null);
    }
    
    /**
     * التحقق إذا كانت جميع الحسابات في المجموعة نشطة
     */
    public boolean areAllAccountsActive() {
        return childAccounts.stream()
                .allMatch(account -> account.getStatus() == AccountStatus.ACTIVE);
    }
    
    /**
     * إغلاق جميع الحسابات في المجموعة
     */
    public void closeAllAccounts() {
        childAccounts.forEach(account -> account.setStatus(AccountStatus.CLOSED));
    }
    
    @Override
    public String toString() {
        return String.format("AccountGroup{name='%s', type='%s', accounts=%d, totalBalance=%.2f}",
                groupName, groupType, childAccounts.size(), getTotalBalance());
    }
}