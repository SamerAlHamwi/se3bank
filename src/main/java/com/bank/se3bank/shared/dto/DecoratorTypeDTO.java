package com.bank.se3bank.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecoratorTypeDTO {
    private String type;
    private String displayName;
    private String description;
}
