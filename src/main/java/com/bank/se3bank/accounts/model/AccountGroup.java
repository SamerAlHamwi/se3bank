package com.bank.se3bank.accounts.model;

import jakarta.persistence.*;
import lombok.*;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.shared.enums.AccountStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountGroup {
    
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
}