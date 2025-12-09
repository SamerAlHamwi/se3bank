package com.bank.se3bank.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreationResponse {
    private Boolean success;
    private Long groupId;
    private String groupName;
    private Integer totalAccounts;
    private Double totalBalance;
    private String message;
    private Long processingTimeMs;
    private LocalDateTime timestamp;
}