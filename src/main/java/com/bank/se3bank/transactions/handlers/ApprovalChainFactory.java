// 📁 src/main/java/com/bank/se3bank/transactions/handlers/ApprovalChainFactory.java
package com.bank.se3bank.transactions.handlers;

import com.bank.se3bank.transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * مصنع لإنشاء سلسلة الاعتماد باستخدام Chain of Responsibility
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApprovalChainFactory {
    
    private final TransactionRepository transactionRepository;
    
    /**
     * إنشاء سلسلة الاعتماد الكاملة
     */
    public TransactionHandler createApprovalChain() {
        log.info("🔗 إنشاء سلسلة اعتماد المعاملات (Chain of Responsibility)...");
        
        // إنشاء المعالجات
        TransactionHandler balanceCheck = new BalanceCheckHandler();
        TransactionHandler fraudDetection = new FraudDetectionHandler(transactionRepository);
        TransactionHandler amlCompliance = new AMLComplianceHandler();
        TransactionHandler limitCheck = new LimitCheckHandler();
        TransactionHandler autoApprove = new AutoApprovalHandler(5000.0); // حد 5000 للاعتماد التلقائي
        TransactionHandler managerApprove = new ManagerApprovalHandler();
        
        // بناء السلسلة
        balanceCheck.setNextHandler(fraudDetection);
        fraudDetection.setNextHandler(amlCompliance);
        amlCompliance.setNextHandler(limitCheck);
        limitCheck.setNextHandler(autoApprove);
        autoApprove.setNextHandler(managerApprove);
        
        log.info("✅ تم إنشاء سلسلة اعتماد مكونة من 6 معالجات");
        
        return balanceCheck; // أول معالج في السلسلة
    }
    
    /**
     * إنشاء سلسلة مبسطة للاختبار
     */
    public TransactionHandler createSimpleChain() {
        TransactionHandler balanceCheck = new BalanceCheckHandler();
        TransactionHandler autoApprove = new AutoApprovalHandler(1000.0);
        TransactionHandler managerApprove = new ManagerApprovalHandler();
        
        balanceCheck.setNextHandler(autoApprove);
        autoApprove.setNextHandler(managerApprove);
        
        return balanceCheck;
    }
    
    /**
     * إنشاء سلسلة للمعاملات الصغيرة (لا تحتاج اعتماد مدير)
     */
    public TransactionHandler createSmallTransactionChain() {
        TransactionHandler balanceCheck = new BalanceCheckHandler();
        TransactionHandler limitCheck = new LimitCheckHandler();
        TransactionHandler autoApprove = new AutoApprovalHandler(10000.0);
        
        balanceCheck.setNextHandler(limitCheck);
        limitCheck.setNextHandler(autoApprove);
        
        return balanceCheck;
    }
}