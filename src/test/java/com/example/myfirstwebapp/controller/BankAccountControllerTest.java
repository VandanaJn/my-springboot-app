package com.example.myfirstwebapp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Tests the current behavior of {@link BankAccountController}.
 *
 * The controller stores the balance in an instance field, and @WebMvcTest
 * reuses one controller instance across methods. @DirtiesContext rebuilds the
 * context (and so resets the balance to 0.0) before each test, keeping them
 * independent of execution order.
 */
@WebMvcTest(BankAccountController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void balanceStartsAtZero() throws Exception {
        mockMvc.perform(get("/account/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("Current balance: 0.0"));
    }

    @Test
    void depositIncreasesBalance() throws Exception {
        mockMvc.perform(post("/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposited 100.0. New balance: 100.0"));

        mockMvc.perform(get("/account/balance"))
                .andExpect(content().string("Current balance: 100.0"));
    }

    @Test
    void depositRejectsNonPositiveAmount() throws Exception {
        mockMvc.perform(post("/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": -5}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit amount must be positive."));
    }

    @Test
    void withdrawDecreasesBalance() throws Exception {
        mockMvc.perform(post("/account/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100}"));

        mockMvc.perform(post("/account/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 30}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdrew 30.0. New balance: 70.0"));
    }

    @Test
    void withdrawRejectsInsufficientFunds() throws Exception {
        mockMvc.perform(post("/account/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Insufficient funds. Current balance: 0.0"));
    }

    @Test
    void withdrawRejectsNonPositiveAmount() throws Exception {
        mockMvc.perform(post("/account/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 0}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdrawal amount must be positive."));
    }
}
