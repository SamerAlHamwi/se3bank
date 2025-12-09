// 📁 src/main/java/com/bank/se3bank/initialization/DataInitializer.java
package com.bank.se3bank.initialization;

import com.bank.se3bank.accounts.service.AccountService;
import com.bank.se3bank.shared.dto.CreateAccountRequest;
import com.bank.se3bank.shared.enums.AccountType;
import com.bank.se3bank.shared.enums.Role;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final AccountService accountService;

    @Override
    public void run(String... args) {
        log.info("🚀 بدء تهيئة البيانات الأولية...");
        
        try {
            initializeUsers();
            initializeAccounts();
            
            log.info("✅ تم تهيئة البيانات الأولية بنجاح!");
        } catch (Exception e) {
            log.error("❌ خطأ في تهيئة البيانات: {}", e.getMessage());
        }
    }

    private void initializeUsers() {
        if (userService.getAllUsers().isEmpty()) {
            log.info("👥 إنشاء مستخدمين افتراضيين...");
            
            // مدير النظام
            User admin = User.builder()
                    .username("admin")
                    .email("admin@bank.com")
                    .password("admin123")
                    .firstName("أحمد")
                    .lastName("المدير")
                    .phoneNumber("+966500000001")
                    .nationalId("1010101010")
                    .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_MANAGER))
                    .build();
            userService.createUser(admin);

            // مدير فرع
            User manager = User.builder()
                    .username("manager")
                    .email("manager@bank.com")
                    .password("manager123")
                    .firstName("محمد")
                    .lastName("المدير")
                    .phoneNumber("+966500000002")
                    .nationalId("2020202020")
                    .roles(Set.of(Role.ROLE_MANAGER))
                    .build();
            userService.createUser(manager);

            // محصل
            User teller = User.builder()
                    .username("teller")
                    .email("teller@bank.com")
                    .password("teller123")
                    .firstName("خالد")
                    .lastName("المحصل")
                    .phoneNumber("+966500000003")
                    .nationalId("3030303030")
                    .roles(Set.of(Role.ROLE_TELLER))
                    .build();
            userService.createUser(teller);

            // عميل
            User customer1 = User.builder()
                    .username("customer1")
                    .email("customer1@bank.com")
                    .password("customer123")
                    .firstName("سارة")
                    .lastName("العتيبي")
                    .phoneNumber("+966500000004")
                    .nationalId("4040404040")
                    .roles(Set.of(Role.ROLE_CUSTOMER))
                    .build();
            userService.createUser(customer1);

            User customer2 = User.builder()
                    .username("customer2")
                    .email("customer2@bank.com")
                    .password("customer123")
                    .firstName("فهد")
                    .lastName("الجبري")
                    .phoneNumber("+966500000005")
                    .nationalId("5050505050")
                    .roles(Set.of(Role.ROLE_CUSTOMER))
                    .build();
            userService.createUser(customer2);

            log.info("✅ تم إنشاء 5 مستخدمين افتراضيين");
        }
    }

    private void initializeAccounts() {
        // إنشاء حسابات للعميل الأول
        User customer1 = userService.getUserByUsername("customer1");
        
        CreateAccountRequest savingsRequest = new CreateAccountRequest();
        savingsRequest.setAccountType(AccountType.SAVINGS);
        savingsRequest.setUserId(customer1.getId());
        savingsRequest.setInitialBalance(5000.0);
        savingsRequest.setInterestRate(2.5);
        accountService.createAccount(savingsRequest);

        CreateAccountRequest checkingRequest = new CreateAccountRequest();
        checkingRequest.setAccountType(AccountType.CHECKING);
        checkingRequest.setUserId(customer1.getId());
        checkingRequest.setInitialBalance(3000.0);
        checkingRequest.setOverdraftLimit(1000.0);
        accountService.createAccount(checkingRequest);

        // إنشاء حسابات للعميل الثاني
        User customer2 = userService.getUserByUsername("customer2");
        
        CreateAccountRequest loanRequest = new CreateAccountRequest();
        loanRequest.setAccountType(AccountType.LOAN);
        loanRequest.setUserId(customer2.getId());
        loanRequest.setInitialBalance(0.0);
        loanRequest.setLoanAmount(10000.0);
        loanRequest.setLoanTermMonths(24);
        loanRequest.setAnnualInterestRate(7.5);
        accountService.createAccount(loanRequest);

        CreateAccountRequest investmentRequest = new CreateAccountRequest();
        investmentRequest.setAccountType(AccountType.INVESTMENT);
        investmentRequest.setUserId(customer2.getId());
        investmentRequest.setInitialBalance(20000.0);
        investmentRequest.setRiskLevel("MEDIUM");
        investmentRequest.setInvestmentType("STOCKS");
        accountService.createAccount(investmentRequest);

        log.info("✅ تم إنشاء 4 حسابات افتراضية");
    }
}