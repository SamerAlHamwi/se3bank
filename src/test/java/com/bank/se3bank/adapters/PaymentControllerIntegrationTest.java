package com.bank.se3bank.adapters;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setupUser() throws Exception {
        String registerJson = """
                {
                  "username":"payuser",
                  "email":"payuser@test.com",
                  "password":"password",
                  "firstName":"Pay",
                  "lastName":"User",
                  "roles":["ROLE_CUSTOMER"]
                }
                """;
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"payuser\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        token = objectMapper.readTree(loginResponse).get("token").asText();
    }

    @Test
    void processPayment_returnsSuccess() throws Exception {
        String request = """
                {
                  "accountNumber":"ACC-DEMO",
                  "recipient":"Demo Store",
                  "amount":40.5,
                  "currency":"USD",
                  "description":"Mock purchase"
                }
                """;

        String response = mockMvc.perform(post("/api/payments/process")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        assertThat(node.get("status").asText()).isEqualTo("SUCCESS");
        assertThat(node.get("transactionId").asText()).contains("stripe_");
    }
}

