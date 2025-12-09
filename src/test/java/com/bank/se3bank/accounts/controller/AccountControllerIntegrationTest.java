package com.bank.se3bank.accounts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long userId;

    @BeforeEach
    void setupUser() throws Exception {
        String registerJson = """
                {
                  "username":"accadmin",
                  "email":"accadmin@test.com",
                  "password":"password",
                  "firstName":"Acc",
                  "lastName":"Admin",
                  "roles":["ROLE_ADMIN"]
                }
                """;
        String registerResponse = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode registerNode = objectMapper.readTree(registerResponse);
        userId = registerNode.get("userId").asLong();

        String loginJson = """
                {"username":"accadmin","password":"password"}
                """;
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        token = objectMapper.readTree(loginResponse).get("token").asText();
    }

    @Test
    void createAndFetchAccount_success() throws Exception {
        String createRequest = String.format("""
                {
                  "accountType":"SAVINGS",
                  "userId":%d,
                  "initialBalance":200.0
                }
                """, userId);

        String createResponse = mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode created = objectMapper.readTree(createResponse);
        Long accountId = created.get("id").asLong();

        String getResponse = mockMvc.perform(get("/api/accounts/" + accountId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode fetched = objectMapper.readTree(getResponse);
        assertThat(fetched.get("accountNumber").asText()).isNotBlank();
        assertThat(fetched.get("balance").asDouble()).isEqualTo(200.0);
    }
}

