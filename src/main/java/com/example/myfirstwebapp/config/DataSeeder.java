package com.example.myfirstwebapp.config;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.myfirstwebapp.entity.Account;
import com.example.myfirstwebapp.entity.AppUser;
import com.example.myfirstwebapp.repository.AccountRepository;
import com.example.myfirstwebapp.repository.AppUserRepository;

/**
 * Seeds demo users on startup so the app is usable immediately.
 * Idempotent: a user is only created if it does not already exist.
 *
 * Demo credentials (development only):
 *   alice / password  (ROLE_USER, starts with a 100.00 account)
 *   admin / admin     (ROLE_ADMIN)
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AppUserRepository userRepository, AccountRepository accountRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUser("alice", "password", Set.of("USER"), new BigDecimal("100.00"));
        seedUser("admin", "admin", Set.of("ADMIN"), null);
    }

    private void seedUser(String username, String rawPassword, Set<String> roles, BigDecimal initialBalance) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }
        AppUser user = userRepository.save(new AppUser(username, passwordEncoder.encode(rawPassword), roles));
        if (initialBalance != null) {
            accountRepository.save(new Account(user, initialBalance));
        }
    }
}
