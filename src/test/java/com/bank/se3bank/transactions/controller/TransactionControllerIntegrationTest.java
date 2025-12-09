package com.bank.se3bank.transactions.controller;

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
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private String accountNumber;

    @BeforeEach
    void setupData() throws Exception {
        // register admin
        String registerJson = """
                {
                  "username":"txnadmin",
                  "email":"txnadmin@test.com",
                  "password":"password",
                  "firstName":"Txn",
                  "lastName":"Admin",
                  "roles":["ROLE_ADMIN"]
                }
                """;
        String registerResponse = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long userId = objectMapper.readTree(registerResponse).get("userId").asLong();

        // login
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"txnadmin\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        token = objectMapper.readTree(loginResponse).get("token").asText();

        // create account
        String createRequest = String.format("""
                {"accountType":"SAVINGS","userId":%d,"initialBalance":300.0}
                """, userId);
        String createResponse = mockMvc.perform(post("/api/accounts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        accountNumber = objectMapper.readTree(createResponse).get("accountNumber").asText();
    }

    @Test
    void createDepositTransaction_success() throws Exception {
        String body = """
                {
                  "transactionType":"DEPOSIT",
                  "toAccountNumber":"%s",
                  "amount":50.0,
                  "description":"test deposit"
                }
                """.formatted(accountNumber);

        String response = mockMvc.perform(post("/api/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        assertThat(node.get("transactionType").asText()).isEqualTo("DEPOSIT");
        String txnId = node.get("transactionId").asText();

        String fetched = mockMvc.perform(get("/api/transactions/reference/" + txnId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode fetchedNode = objectMapper.readTree(fetched);
        assertThat(fetchedNode.get("transactionId").asText()).isEqualTo(txnId);
    }
}

