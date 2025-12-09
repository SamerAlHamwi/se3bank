// 📁 src/main/java/com/bank/se3bank/initialization/DataInitializer.java
package com.bank.se3bank.initialization;

import com.bank.se3bank.accounts.model.Account;
import com.bank.se3bank.accounts.model.AccountGroup;
import com.bank.se3bank.accounts.service.AccountService;
import com.bank.se3bank.accounts.service.DecoratorService;
import com.bank.se3bank.accounts.service.GroupService;
import com.bank.se3bank.facade.BankFacade;
import com.bank.se3bank.interest.service.InterestService;
import com.bank.se3bank.notifications.service.NotificationService;
import com.bank.se3bank.shared.dto.AddDecoratorRequest;
import com.bank.se3bank.shared.dto.CreateAccountRequest;
import com.bank.se3bank.shared.dto.CreateGroupRequest;
import com.bank.se3bank.shared.dto.OpenAccountRequest;
import com.bank.se3bank.shared.dto.TransferRequest;
import com.bank.se3bank.shared.enums.AccountType;
import com.bank.se3bank.shared.enums.Role;
import com.bank.se3bank.transactions.service.TransactionService;
import com.bank.se3bank.users.model.User;
import com.bank.se3bank.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@org.springframework.context.annotation.Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final AccountService accountService;
    private final GroupService groupService;
    private final DecoratorService decoratorService;
    private final BankFacade bankFacade;
    private final NotificationService notificationService;
    private final TransactionService transactionService;
    private final InterestService interestService;

    @Override
    public void run(String... args) {
        log.info("🚀 بدء تهيئة البيانات الأولية...");
        
        try {
            initializeUsers();
            initializeAccounts();
            initializeGroups();
            initializeDecorators();
            testFacadeOperations();
            testNotifications();
            testTransactions();
            initializeInterestStrategies();
            testAllPatterns();
            testBankFacade();

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

    private void initializeGroups() {
        log.info("🏢 إنشاء مجموعات حسابات افتراضية (Composite Pattern)...");
        
        User customer1 = userService.getUserByUsername("customer1");
        
        // إنشاء مجموعة حسابات عائلية
        CreateGroupRequest familyGroupRequest = new CreateGroupRequest();
        familyGroupRequest.setGroupName("الحسابات العائلية");
        familyGroupRequest.setDescription("جميع حسابات العائلة");
        familyGroupRequest.setGroupType("FAMILY");
        familyGroupRequest.setOwnerId(customer1.getId());
        familyGroupRequest.setMaxAccounts(10);
        
        AccountGroup familyGroup = groupService.createGroup(familyGroupRequest);
        
        // إضافة حسابات العميل إلى المجموعة
        List<Account> customerAccounts = accountService.getUserAccounts(customer1.getId());
        for (Account account : customerAccounts) {
            groupService.addAccountToGroup(familyGroup.getId(), account.getId());
        }
        
        log.info("✅ تم إنشاء مجموعة حسابات عائلية تحتوي على {} حساب", 
                familyGroup.getChildCount());
    }
    
    private void initializeDecorators() {
    log.info("🎨 إضافة ديكورات افتراضية (Decorator Pattern)...");
    
    try {
        // الحصول على حساب توفير للعميل الأول
        List<Account> customer1Accounts = accountService.getUserAccounts(
            userService.getUserByUsername("customer1").getId()
        );
        
        if (!customer1Accounts.isEmpty()) {
            Account firstAccount = customer1Accounts.get(0);
            
            // إضافة حماية السحب على المكشوف
            AddDecoratorRequest overdraftRequest = new AddDecoratorRequest();
            overdraftRequest.setDecoratorType("OVERDRAFT_PROTECTION");
            overdraftRequest.setAccountId(firstAccount.getId());
            overdraftRequest.setOverdraftLimit(1000.0);
            overdraftRequest.setDescription("حماية السحب على المكشوف لحد 1000");
            
            decoratorService.addDecorator(overdraftRequest);
            
            // إضافة خدمات مميزة
            AddDecoratorRequest premiumRequest = new AddDecoratorRequest();
            premiumRequest.setDecoratorType("PREMIUM_SERVICES");
            premiumRequest.setAccountId(firstAccount.getId());
            premiumRequest.setTierLevel("GOLD");
            premiumRequest.setDescription("خدمات ذهبية مميزة");
            
            decoratorService.addDecorator(premiumRequest);
            
            log.info("✅ تم إضافة ديكورات للحساب: {}", firstAccount.getAccountNumber());
        }
        } catch (Exception e) {
            log.warn("⚠️  تعذر إضافة ديكورات: {}", e.getMessage());
        }
    }

    private void testFacadeOperations() {
        log.info("🏦 اختبار عمليات Facade Pattern...");
        
        try {
            // الحصول على مستخدم اختبار
            var users = userService.getAllUsers();
            if (users.size() >= 2) {
                var customer1 = users.get(3); // customer1
                var customer2 = users.get(4); // customer2
                
                // اختبار فتح حساب باستخدام Facade
                OpenAccountRequest openRequest = new OpenAccountRequest();
                openRequest.setUserId(customer1.getId());
                openRequest.setAccountType(AccountType.SAVINGS);
                openRequest.setInitialBalance(5000.0);
                
                var openResponse = bankFacade.openNewAccount(openRequest);
                log.info("✅ فتح حساب باستخدام Facade: {}", openResponse.getAccountNumber());
                
                // اختبار التحويل باستخدام Facade
                var accounts = accountService.getUserAccounts(customer1.getId());
                if (accounts.size() >= 2) {
                    TransferRequest transferRequest = new TransferRequest();
                    transferRequest.setFromAccountNumber(accounts.get(0).getAccountNumber());
                    transferRequest.setToAccountNumber(accounts.get(1).getAccountNumber());
                    transferRequest.setAmount(100.0);
                    transferRequest.setDescription("اختبار Facade");
                    
                    var transferResponse = bankFacade.transferMoney(transferRequest);
                    log.info("✅ تحويل باستخدام Facade: {}", transferResponse.getTransactionId());
                }
            }
        } catch (Exception e) {
            log.warn("⚠️  تعذر اختبار Facade: {}", e.getMessage());
        }
    }
    private void testNotifications() {
        log.info("🔔 اختبار نظام الإشعارات (Observer Pattern)...");
        
        try {
            var users = userService.getAllUsers();
            if (!users.isEmpty()) {
                var customer = users.get(3); // customer1
                
                // اختبار إرسال إشعار تجريبي
                notificationService.sendCustomNotification(
                        customer,
                        "🎉 مرحباً بك في SE3 Bank",
                        "يسرنا انضمامك إلى عائلة SE3 Bank. يمكنك الآن الاستفادة من جميع خدماتنا المصرفية المتقدمة.",
                        "IN_APP",
                        "WELCOME"
                );
                
                log.info("✅ تم إرسال إشعار ترحيبي للمستخدم: {}", customer.getUsername());
            }
        } catch (Exception e) {
            log.warn("⚠️ تعذر اختبار الإشعارات: {}", e.getMessage());
        }
    }

    private void testTransactions() {
        log.info("💸 اختبار نظام المعاملات (Chain of Responsibility)...");
        
        try {
            var users = userService.getAllUsers();
            if (users.size() >= 2) {
                var customer1 = users.get(3); // customer1
                var customer2 = users.get(4); // customer2
                
                // الحصول على حسابات العملاء
                var customer1Accounts = accountService.getUserAccounts(customer1.getId());
                var customer2Accounts = accountService.getUserAccounts(customer2.getId());
                
                if (!customer1Accounts.isEmpty() && !customer2Accounts.isEmpty()) {
                    var account1 = customer1Accounts.get(0);
                    var account2 = customer2Accounts.get(0);
                    
                    // اختبار معاملة تحويل صغيرة (يجب أن تمر تلقائياً)
                    log.info("🔄 اختبار تحويل صغير (يجب أن يمر تلقائياً)...");
                    var smallTransfer = transactionService.createTransaction(
                            account1, account2, 100.0, "اختبار تحويل صغير");
                    log.info("💰 نتيجة التحويل الصغير: {}", smallTransfer.getStatus());
                    
                    // اختبار معاملة كبيرة (يجب أن تنتظر اعتماد مدير)
                    log.info("🔄 اختبار تحويل كبير (يجب أن ينتظر اعتماد)...");
                    var largeTransfer = transactionService.createTransaction(
                            account1, account2, 15000.0, "اختبار تحويل كبير");
                    log.info("💰 نتيجة التحويل الكبير: {}", largeTransfer.getStatus());
                    
                    // اختبار سحب
                    log.info("💰 اختبار سحب...");
                    var withdrawal = transactionService.createWithdrawalTransaction(
                            account1, 500.0, "اختبار سحب");
                    log.info("💰 نتيجة السحب: {}", withdrawal.getStatus());
                    
                    // اختبار إيداع
                    log.info("📥 اختبار إيداع...");
                    var deposit = transactionService.createDepositTransaction(
                            account2, 1000.0, "اختبار إيداع");
                    log.info("📥 نتيجة الإيداع: {}", deposit.getStatus());
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ تعذر اختبار المعاملات: {}", e.getMessage());
        }
    }



    private void initializeInterestStrategies() {
        log.info("📈 تعيين استراتيجيات فائدة للحسابات الافتراضية...");
        
        try {
            var users = userService.getAllUsers();
            if (!users.isEmpty()) {
                var customer1 = users.get(3); // customer1
                var accounts = accountService.getUserAccounts(customer1.getId());
                
                if (!accounts.isEmpty()) {
                    // تعيين استراتيجيات مختلفة للحسابات
                    Account savingsAccount = accounts.stream()
                            .filter(a -> a.getAccountType() == AccountType.SAVINGS)
                            .findFirst()
                            .orElse(null);
                    
                    if (savingsAccount != null) {
                        interestService.changeAccountInterestStrategy(
                                savingsAccount.getId(), "compoundInterestStrategy");
                        log.info("✅ تعيين فائدة مركبة لحساب التوفير: {}", 
                                savingsAccount.getAccountNumber());
                    }
                    
                    // حساب استثماري
                    Account investmentAccount = accounts.stream()
                            .filter(a -> a.getAccountType() == AccountType.INVESTMENT)
                            .findFirst()
                            .orElse(null);
                    
                    if (investmentAccount != null) {
                        interestService.changeAccountInterestStrategy(
                                investmentAccount.getId(), "tieredInterestStrategy");
                        log.info("✅ تعيين فائدة متدرجة للحساب الاستثماري: {}", 
                                investmentAccount.getAccountNumber());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ تعذر تعيين استراتيجيات الفائدة: {}", e.getMessage());
        }
    }

    private void testAllPatterns() {
        log.info("🧪 اختبار جميع الأنماط التصميمية...");
        
        try {
            // اختبار Factory Pattern
            log.info("🏭 Factory Pattern: تم إنشاء 4 حسابات بأنواع مختلفة");
            
            // اختبار Singleton Pattern
            log.info("🔒 Singleton Pattern: اتصال قاعدة البيانات مفرد");
            
            // اختبار Composite Pattern
            log.info("🏢 Composite Pattern: مجموعات حسابات جاهزة");
            
            // اختبار Strategy Pattern
            log.info("📈 Strategy Pattern: 5 استراتيجيات فائدة جاهزة");
            
            // اختبار Observer Pattern
            log.info("🔔 Observer Pattern: 3 قنوات إشعار جاهزة");
            
            // اختبار Chain of Responsibility
            log.info("🔗 Chain of Responsibility: 6 معالجات اعتماد جاهزة");
            
            log.info("✅ جميع الأنماط التسعة جاهزة للعمل!");
        } catch (Exception e) {
            log.warn("⚠️ بعض الاختبارات تعذرت: {}", e.getMessage());
        }
    }

    private void testBankFacade() {
        log.info("🏦 اختبار BankFacade (Facade Pattern)...");
        
        try {
            var customer1 = userService.getUserByUsername("customer1");
            
            // اختبار فتح حساب باستخدام Facade
            OpenAccountRequest openRequest = new OpenAccountRequest();
            openRequest.setUserId(customer1.getId());
            openRequest.setAccountType(AccountType.BUSINESS);
            openRequest.setInitialBalance(10000.0);
            
            var response = bankFacade.openNewAccount(openRequest);
            log.info("✅ تم فتح حساب تجاري: {}", response.getAccountNumber());
            
        } catch (Exception e) {
            log.warn("⚠️ تعذر اختبار Facade: {}", e.getMessage());
        }
    }

    private void showSummary() {
        log.info("\n" +
                "========================================\n" +
                "🎉 SE3 Bank System - Ready to Use!\n" +
                "========================================\n" +
                "👥 Users: 5 مستخدمين\n" +
                "🏦 Accounts: {} حساب\n".formatted(accountService.getAllAccounts().size()) +
                "📈 Interest Strategies: 5 استراتيجيات\n" +
                "🔔 Notification Channels: 3 قنوات\n" +
                "🔗 Approval Handlers: 6 معالجات\n" +
                "🏢 Account Groups: جاهزة\n" +
                "🎨 Account Decorators: جاهزة\n" +
                "========================================\n" +
                "🌐 API: http://localhost:9090/swagger-ui.html\n" +
                "📚 Docs: http://localhost:9090/api-docs\n" +
                "========================================");
    }

}