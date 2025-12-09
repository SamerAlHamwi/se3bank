package com.bank.se3bank.shared.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void registerRequest_missingEmailFails() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user");
        request.setPassword("secret123");
        request.setFirstName("fn");
        request.setLastName("ln");
        // email missing

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void createAccountRequest_negativeBalanceFails() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setInitialBalance(-1.0);

        Set<ConstraintViolation<CreateAccountRequest>> violations = validator.validate(request);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("initialBalance"));
    }

    @Test
    void loginRequest_validPasses() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("secret");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }
}

