package com.bank.se3bank.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void initUser() throws Exception {
        String uniqueUser = "secured_" + java.util.UUID.randomUUID();
        String uniqueEmail = uniqueUser + "@test.com";
        String registerJson = """
                {
                  "username":"%s",
                  "email":"%s",
                  "password":"password",
                  "firstName":"Secure",
                  "lastName":"User",
                  "roles":["ROLE_ADMIN"]
                }
                """.formatted(uniqueUser, uniqueEmail);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        String loginJson = """
                {"username":"%s","password":"password"}
                """.formatted(uniqueUser);
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        token = (String) objectMapper.readValue(loginResponse, Map.class).get("token");
    }

    @Test
    void missingJwt_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/accounts/exists/ANY"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidJwt_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/accounts/exists/ANY")
                        .header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validJwt_allowsAccess() throws Exception {
        var response = mockMvc.perform(get("/api/accounts/exists/ANY")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(response).contains("exists");
    }
}

