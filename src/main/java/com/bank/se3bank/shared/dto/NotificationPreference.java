package com.bank.se3bank.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {
    private Long userId;
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private Boolean inAppEnabled;
    private Boolean lowBalanceAlert;
    private Boolean transferAlert;
    private Boolean loginAlert;
    private Boolean marketingEmails;
    private Boolean monthlyStatement;
}