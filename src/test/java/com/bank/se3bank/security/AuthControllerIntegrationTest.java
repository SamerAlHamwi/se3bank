package com.bank.se3bank.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerLoginAndMe_flowSucceeds() throws Exception {
        // register
        String registerJson = """
                {
                  "username":"admin",
                  "email":"admin@test.com",
                  "password":"password",
                  "firstName":"Admin",
                  "lastName":"User",
                  "roles":["ROLE_ADMIN"]
                }
                """;
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        // login
        String loginJson = """
                {"username":"admin","password":"password"}
                """;
        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> body = objectMapper.readValue(loginResponse, Map.class);
        String token = (String) body.get("token");
        assertThat(token).isNotBlank();

        // me endpoint
        String meResponse = mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<?, ?> me = objectMapper.readValue(meResponse, Map.class);
        List<String> roles = objectMapper.convertValue(me.get("roles"), new com.fasterxml.jackson.core.type.TypeReference<>() {});
        assertThat(roles).contains("ROLE_ADMIN");
    }
}

