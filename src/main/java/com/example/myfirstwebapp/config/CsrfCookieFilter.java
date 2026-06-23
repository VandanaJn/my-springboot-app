package com.example.myfirstwebapp.config;

import java.io.IOException;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Forces the (deferred) CSRF token to be loaded on every request so that the
 * XSRF-TOKEN cookie is actually written to the response. Without this, with
 * CookieCsrfTokenRepository the cookie is only emitted when a view renders the
 * token (e.g. the login page) — so JSON/API clients never receive it after login.
 *
 * This is the pattern from Spring Security's Single-Page-Application guidance.
 */
public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        if (csrfToken != null) {
            // Accessing the token value triggers the deferred load and cookie write.
            csrfToken.getToken();
        }
        filterChain.doFilter(request, response);
    }
}
