package com.bank.se3bank.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GroupCreationRequest {
    @NotBlank
    private String groupName;
    
    private String description;
    private String groupType;
    
    @NotNull
    private Long ownerId;
    
    private Integer maxAccounts;
    private List<Long> accountIds;
}