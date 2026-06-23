package com.example.myfirstwebapp.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Role-based authorization for the admin endpoints: ADMIN can read any user's
 * account; a plain USER is forbidden.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminCanReadAnyAccountBalance() throws Exception {
        mockMvc.perform(get("/admin/accounts/alice/balance").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"balance\":100.00}"));
    }

    @Test
    void nonAdminUserIsForbidden() throws Exception {
        mockMvc.perform(get("/admin/accounts/alice/balance").with(user("alice").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unknownAccountReturns404() throws Exception {
        mockMvc.perform(get("/admin/accounts/ghost/balance").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }
}
