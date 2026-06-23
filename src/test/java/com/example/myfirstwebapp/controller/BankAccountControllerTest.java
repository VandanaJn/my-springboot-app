package com.example.myfirstwebapp.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * End-to-end tests for the per-user account endpoints. Runs against the full
 * app with the seeded user "alice" (starts at 100.00). @Transactional rolls back
 * each test's balance changes so methods stay independent.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedRequestIsRejected() throws Exception {
        mockMvc.perform(get("/account/balance"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void balanceReturnsOwnAccountBalance() throws Exception {
        mockMvc.perform(get("/account/balance").with(user("alice").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"balance\":100.00}"));
    }

    @Test
    void depositIncreasesOwnBalance() throws Exception {
        mockMvc.perform(post("/account/deposit").with(user("alice").roles("USER")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":50.00}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"balance\":150.00}"));
    }

    @Test
    void withdrawDecreasesOwnBalance() throws Exception {
        mockMvc.perform(post("/account/withdraw").with(user("alice").roles("USER")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":30.00}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"balance\":70.00}"));
    }

    @Test
    void depositRejectsNonPositiveAmountWith400() throws Exception {
        mockMvc.perform(post("/account/deposit").with(user("alice").roles("USER")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":-5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void withdrawBeyondBalanceReturns409() throws Exception {
        mockMvc.perform(post("/account/withdraw").with(user("alice").roles("USER")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":1000}"))
                .andExpect(status().isConflict());
    }

    @Test
    void postWithoutCsrfTokenIsForbidden() throws Exception {
        mockMvc.perform(post("/account/deposit").with(user("alice").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":50.00}"))
                .andExpect(status().isForbidden());
    }
}
