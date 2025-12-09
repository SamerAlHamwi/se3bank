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
 * ديكور الخدمات المميزة
 * يوفر مميزات إضافية للعملاء المميزين
 */
@Entity
@DiscriminatorValue("PREMIUM_SERVICES")
@Getter
@Setter
@NoArgsConstructor
public class PremiumServicesDecorator extends AccountDecorator {
    
    @jakarta.persistence.Column(name = "tier_level")
    private String tierLevel; // GOLD, PLATINUM, DIAMOND
    
    @jakarta.persistence.Column(name = "priority_banking")
    private Boolean priorityBanking = true;
    
    @jakarta.persistence.Column(name = "free_atm_withdrawals")
    private Integer freeAtmWithdrawals = 10;
    
    @jakarta.persistence.Column(name = "free_wire_transfers")
    private Integer freeWireTransfers = 5;
    
    @jakarta.persistence.Column(name = "dedicated_support")
    private Boolean dedicatedSupport = true;
    
    @jakarta.persistence.Column(name = "investment_advice")
    private Boolean investmentAdvice = true;
    
    @jakarta.persistence.Column(name = "atm_withdrawals_used")
    private Integer atmWithdrawalsUsed = 0;
    
    @jakarta.persistence.Column(name = "wire_transfers_used")
    private Integer wireTransfersUsed = 0;
    
    @jakarta.persistence.Column(name = "last_benefits_reset")
    private LocalDateTime lastBenefitsReset;
    
    public PremiumServicesDecorator(Account decoratedAccount, String tierLevel) {
        super(decoratedAccount, "الخدمات المميزة");
        this.tierLevel = tierLevel;
        this.description = String.format("خدمات %s المميزة", tierLevel);
        setMonthlyFeeBasedOnTier(tierLevel);
        this.lastBenefitsReset = LocalDateTime.now();
    }
    
    private void setMonthlyFeeBasedOnTier(String tierLevel) {
        switch (tierLevel.toUpperCase()) {
            case "GOLD":
                this.monthlyFee = 50.0;
                this.freeAtmWithdrawals = 10;
                this.freeWireTransfers = 5;
                break;
            case "PLATINUM":
                this.monthlyFee = 100.0;
                this.freeAtmWithdrawals = 20;
                this.freeWireTransfers = 10;
                break;
            case "DIAMOND":
                this.monthlyFee = 200.0;
                this.freeAtmWithdrawals = 999; // غير محدود
                this.freeWireTransfers = 999;  // غير محدود
                break;
            default:
                this.monthlyFee = 50.0;
        }
    }
    
    @Override
    public void applyMonthlyFee() {
        if (getDecoratedAccount() != null && isActive) {
            getDecoratedAccount().withdraw(monthlyFee);
            setBalance(getDecoratedAccount().getBalance());
            
            // إعادة تعيين المزايا الشهرية
            resetMonthlyBenefits();
        }
    }
    
    @Override
    public boolean isFeatureSupported(String feature) {
        return getAddedFeatures().contains(feature);
    }
    
    @Override
    public List<String> getAddedFeatures() {
        List<String> features = Arrays.asList(
            "PREMIUM_SERVICES",
            "PRIORITY_BANKING",
            "FREE_ATM_WITHDRAWALS",
            "FREE_WIRE_TRANSFERS"
        );
        
        if (dedicatedSupport) features.add("DEDICATED_SUPPORT");
        if (investmentAdvice) features.add("INVESTMENT_ADVICE");
        
        return features;
    }
    
    // ========== Premium Services Methods ==========
    
    /**
     * استخدام سحب من ماكينة الصراف الآلي
     */
    public boolean useAtmWithdrawal() {
        if (atmWithdrawalsUsed < freeAtmWithdrawals) {
            atmWithdrawalsUsed++;
            return true; // مجاني
        }
        return false; // مدفوع
    }
    
    /**
     * استخدام تحويل مصرفي
     */
    public boolean useWireTransfer() {
        if (wireTransfersUsed < freeWireTransfers) {
            wireTransfersUsed++;
            return true; // مجاني
        }
        return false; // مدفوع
    }
    
    /**
     * إعادة تعيين المزايا الشهرية
     */
    public void resetMonthlyBenefits() {
        // إعادة التعيين في أول كل شهر
        LocalDateTime now = LocalDateTime.now();
        if (lastBenefitsReset == null || 
            lastBenefitsReset.getMonthValue() != now.getMonthValue()) {
            atmWithdrawalsUsed = 0;
            wireTransfersUsed = 0;
            lastBenefitsReset = now;
        }
    }
    
    /**
     * ترقية مستوى الخدمة
     */
    public void upgradeTier(String newTierLevel) {
        this.tierLevel = newTierLevel;
        setMonthlyFeeBasedOnTier(newTierLevel);
        this.description = String.format("خدمات %s المميزة", newTierLevel);
    }
    
    /**
     * الحصول على المزايا المتبقية
     */
    public PremiumBenefits getRemainingBenefits() {
        return PremiumBenefits.builder()
                .remainingAtmWithdrawals(Math.max(0, freeAtmWithdrawals - atmWithdrawalsUsed))
                .remainingWireTransfers(Math.max(0, freeWireTransfers - wireTransfersUsed))
                .tierLevel(tierLevel)
                .build();
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

    /**
     * DTO للمزايا المتبقية
     */
    @lombok.Data
    @lombok.Builder
    public static class PremiumBenefits {
        private Integer remainingAtmWithdrawals;
        private Integer remainingWireTransfers;
        private String tierLevel;
        private LocalDateTime nextReset;
    }
}