package com.bank.se3bank.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateGroupRequest {
    
    @NotBlank(message = "اسم المجموعة مطلوب")
    private String groupName;
    
    private String description;
    
    @NotBlank(message = "نوع المجموعة مطلوب")
    private String groupType; // FAMILY, BUSINESS, JOINT
    
    @NotNull(message = "معرف المالك مطلوب")
    private Long ownerId;
    
    private Integer maxAccounts;
}