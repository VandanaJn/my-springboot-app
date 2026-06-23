package com.example.myfirstwebapp.service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.myfirstwebapp.entity.Account;
import com.example.myfirstwebapp.repository.AccountRepository;

/**
 * Domain logic for operating on a user's bank account: reading the balance and
 * applying deposits/withdrawals with validation. Amounts are BigDecimal
 * (see docs/adr/0004-use-bigdecimal-for-money.md).
 */
@Service
public class BankAccountService {

    private final AccountRepository accountRepository;

    public BankAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(String username) {
        return requireAccount(username).getBalance();
    }

    @Transactional
    public BigDecimal deposit(String username, BigDecimal amount) {
        requirePositive(amount);
        Account account = requireAccount(username);
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        return account.getBalance();
    }

    @Transactional
    public BigDecimal withdraw(String username, BigDecimal amount) {
        requirePositive(amount);
        Account account = requireAccount(username);
        if (amount.compareTo(account.getBalance()) > 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        return account.getBalance();
    }

    private Account requireAccount(String username) {
        return accountRepository.findByOwnerUsername(username)
                .orElseThrow(() -> new NoSuchElementException("No account for user: " + username));
    }

    private void requirePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
