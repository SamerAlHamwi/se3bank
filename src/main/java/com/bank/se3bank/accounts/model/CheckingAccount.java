package com.bank.se3bank.accounts.model;

import com.bank.se3bank.shared.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("CHECKING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CheckingAccount extends Account {
    
    @Column(name = "checkbook_available")
    @Builder.Default
    private Boolean checkbookAvailable = true;
    
    @Column(name = "debit_card_number")
    private String debitCardNumber;
    
    @Override
    public void add(Account account) {
        throw new UnsupportedOperationException("لا يمكن إضافة حسابات إلى حساب جاري فردي");
    }
    
    @Override
    public void remove(Account account) {
        throw new UnsupportedOperationException("لا يمكن إزالة حسابات من حساب جاري فردي");
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    @Override
    public Double getTotalBalance() {
        return getBalance();
    }
    
    @Override
    public AccountType getAccountType() {
        return AccountType.CHECKING;
    }
}