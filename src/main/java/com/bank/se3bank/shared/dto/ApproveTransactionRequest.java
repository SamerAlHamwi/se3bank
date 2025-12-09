package com.bank.se3bank.shared.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApproveTransactionRequest {
    @NotNull
    private Long managerId;
    
    private String reason;
    private String comments;
}