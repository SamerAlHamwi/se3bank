package com.bank.se3bank.accounts.decorators;

import com.bank.se3bank.accounts.model.Account;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * ديكور تأمين على الحساب
 * يوفر حماية ضد الاحتيال والخسائر
 */
@Entity
@DiscriminatorValue("INSURANCE")
@Getter
@Setter
@NoArgsConstructor
public class InsuranceDecorator extends AccountDecorator {
    
    @jakarta.persistence.Column(name = "coverage_amount")
    private Double coverageAmount;
    
    @jakarta.persistence.Column(name = "insurance_type")
    private String insuranceType; // FRAUD, THEFT, LOSS
    
    @jakarta.persistence.Column(name = "deductible_amount")
    private Double deductibleAmount = 100.0;
    
    @jakarta.persistence.Column(name = "claims_count")
    private Integer claimsCount = 0;
    
    @jakarta.persistence.Column(name = "max_claims_per_year")
    private Integer maxClaimsPerYear = 2;
    
    @jakarta.persistence.Column(name = "last_claim_date")
    private LocalDateTime lastClaimDate;
    
    public InsuranceDecorator(Account decoratedAccount, Double coverageAmount, String insuranceType) {
        super(decoratedAccount, "تأمين على الحساب");
        this.coverageAmount = coverageAmount;
        this.insuranceType = insuranceType;
        this.description = String.format("تأمين %s بقيمة %.2f", insuranceType, coverageAmount);
        this.monthlyFee = coverageAmount * 0.005; // 0.5% من قيمة التغطية
    }
    
    @Override
    public void applyMonthlyFee() {
        if (getDecoratedAccount() != null && isActive) {
            getDecoratedAccount().withdraw(monthlyFee);
            setBalance(getDecoratedAccount().getBalance());
        }
    }
    
    @Override
    public boolean isFeatureSupported(String feature) {
        return getAddedFeatures().contains(feature);
    }
    
    @Override
    public List<String> getAddedFeatures() {
        return Arrays.asList(
            "ACCOUNT_INSURANCE",
            "FRAUD_PROTECTION",
            "LOSS_COVERAGE"
        );
    }
    
    // ========== Insurance Specific Methods ==========
    
    /**
     * تقديم مطالبة تأمينية
     */
    public Double fileClaim(Double lossAmount, String description) {
        if (!isActive) {
            throw new IllegalStateException("التأمين غير مفعّل حالياً");
        }
        
        if (claimsCount >= maxClaimsPerYear) {
            throw new IllegalStateException("تم تجاوز الحد الأقصى للمطالبات لهذا العام");
        }
        
        if (lossAmount <= deductibleAmount) {
            throw new IllegalArgumentException("قيمة الخسارة أقل من المبلغ المقتطع");
        }
        
        if (lossAmount > coverageAmount) {
            throw new IllegalArgumentException("قيمة الخسارة تتجاوز قيمة التغطية");
        }
        
        // حساب المبلغ المستحق
        Double payoutAmount = lossAmount - deductibleAmount;
        
        // إيداع المبلغ في الحساب
        if (getDecoratedAccount() != null) {
            getDecoratedAccount().deposit(payoutAmount);
            setBalance(getDecoratedAccount().getBalance());
        }
        
        // تحديث الإحصائيات
        claimsCount++;
        lastClaimDate = LocalDateTime.now();
        
        return payoutAmount;
    }
    
    /**
     * التحقق من صلاحية المطالبة
     */
    public boolean canFileClaim() {
        return isActive && claimsCount < maxClaimsPerYear;
    }
    
    /**
     * إعادة تعيين عدد المطالبات (سنوياً)
     */
    public void resetClaimsCount() {
        this.claimsCount = 0;
    }
    
    /**
     * زيادة قيمة التغطية
     */
    public void increaseCoverage(Double additionalAmount) {
        if (additionalAmount <= 0) {
            throw new IllegalArgumentException("القيمة الإضافية يجب أن تكون أكبر من صفر");
        }
        
        this.coverageAmount += additionalAmount;
        this.monthlyFee = coverageAmount * 0.005; // إعادة حساب الرسوم
    }

    @Override
    public void add(Account account) {

    }

    @Override
    public void remove(Account account) {

    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public List<Account> getChildAccounts() {
        return List.of();
    }

    @Override
    public int getChildCount() {
        return 0;
    }
}