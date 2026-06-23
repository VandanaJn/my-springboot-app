package com.example.myfirstwebapp.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * End-to-end security behavior against the full application context, using the
 * demo users created by DataSeeder. Runs on the in-memory test database
 * (see src/test/resources/application.properties).
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedRequestIsRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/account/balance"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void seededUserCanLogInWithCorrectPassword() throws Exception {
        mockMvc.perform(formLogin().user("alice").password("password"))
                .andExpect(authenticated().withUsername("alice"));
    }

    @Test
    void loginIsRejectedWithWrongPassword() throws Exception {
        mockMvc.perform(formLogin().user("alice").password("wrong-password"))
                .andExpect(unauthenticated());
    }

    @Test
    void authenticatedUserCanReachProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/account/balance").with(user("alice").roles("USER")))
                .andExpect(status().isOk());
    }
}
