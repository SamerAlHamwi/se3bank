package com.bank.se3bank.accounts;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.AccountGroup;
import com.bank.se3bank.accounts.model.SavingsAccount;
import com.bank.se3bank.shared.enums.AccountStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountGroupCompositeTest {

    @Test
    void totalBalanceAggregatesChildren() {
        AccountGroup group = AccountGroup.builder()
                .groupName("Family")
                .groupType("SAVINGS")
                .build();

        Account a1 = SavingsAccount.builder().balance(100.0).status(AccountStatus.ACTIVE).build();
        Account a2 = SavingsAccount.builder().balance(50.0).status(AccountStatus.ACTIVE).build();
        group.add(a1);
        group.add(a2);

        assertThat(group.getTotalBalance()).isEqualTo(150.0);
        assertThat(group.getChildCount()).isEqualTo(2);
        assertThat(group.getAverageBalance()).isEqualTo(75.0);
    }

    @Test
    void transferWithinGroupMovesFunds() {
        AccountGroup group = AccountGroup.builder()
                .groupName("Team")
                .groupType("SAVINGS")
                .build();

        Account from = SavingsAccount.builder().accountNumber("A1").balance(200.0).status(AccountStatus.ACTIVE).build();
        Account to = SavingsAccount.builder().accountNumber("A2").balance(0.0).status(AccountStatus.ACTIVE).build();
        group.add(from);
        group.add(to);

        group.transferWithinGroup("A1", "A2", 75.0);

        assertThat(from.getBalance()).isEqualTo(125.0);
        assertThat(to.getBalance()).isEqualTo(75.0);
    }
}

