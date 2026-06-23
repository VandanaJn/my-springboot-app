package com.example.myfirstwebapp.config;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

/**
 * Spring Security configuration: session form login backed by database users
 * (see AppUserDetailsService) with BCrypt password hashing.
 *
 * - All requests require authentication, except the H2 console.
 * - CSRF protection is enabled (correct for a session/cookie app) and exposed
 *   via a cookie so JS/curl clients can read and echo the token; the H2 console
 *   is exempted because it manages its own forms.
 * - @EnableMethodSecurity turns on @PreAuthorize for Phase 3 role checks.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form.permitAll())
                .logout(logout -> logout.permitAll())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // Plain handler (not the default XOR/masking one) so clients can submit
                        // the RAW token read from the XSRF-TOKEN cookie in the X-XSRF-TOKEN header.
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers(PathRequest.toH2Console()))
                .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
