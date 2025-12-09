package com.bank.se3bank.shared.dto;

import com.bank.se3bank.shared.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String phoneNumber;
    private String address;

    @Pattern(regexp = "^[0-9]{10,20}$", message = "الرقم القومي يجب أن يكون أرقام فقط")
    private String nationalId;

    private Set<Role> roles;
}

