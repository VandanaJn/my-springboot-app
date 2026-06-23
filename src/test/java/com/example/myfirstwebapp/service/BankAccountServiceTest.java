package com.example.myfirstwebapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.myfirstwebapp.entity.Account;
import com.example.myfirstwebapp.entity.AppUser;
import com.example.myfirstwebapp.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private BankAccountService service;

    private Account account;

    @BeforeEach
    void setUp() {
        AppUser alice = new AppUser("alice", "hash", Set.of("USER"));
        account = new Account(alice, new BigDecimal("100.00"));
    }

    private void givenAccountExists() {
        when(accountRepository.findByOwnerUsername("alice")).thenReturn(Optional.of(account));
    }

    @Test
    void getBalanceReturnsCurrentBalance() {
        givenAccountExists();

        assertThat(service.getBalance("alice")).isEqualByComparingTo("100.00");
    }

    @Test
    void depositIncreasesBalanceAndPersists() {
        givenAccountExists();

        BigDecimal newBalance = service.deposit("alice", new BigDecimal("50.00"));

        assertThat(newBalance).isEqualByComparingTo("150.00");
        assertThat(account.getBalance()).isEqualByComparingTo("150.00");
        verify(accountRepository).save(account);
    }

    @Test
    void withdrawDecreasesBalanceAndPersists() {
        givenAccountExists();

        BigDecimal newBalance = service.withdraw("alice", new BigDecimal("30.00"));

        assertThat(newBalance).isEqualByComparingTo("70.00");
        assertThat(account.getBalance()).isEqualByComparingTo("70.00");
        verify(accountRepository).save(account);
    }

    @Test
    void depositRejectsNonPositiveAmount() {
        assertThatThrownBy(() -> service.deposit("alice", new BigDecimal("-5.00")))
                .isInstanceOf(IllegalArgumentException.class);

        verify(accountRepository, never()).save(any());
    }

    @Test
    void withdrawRejectsNonPositiveAmount() {
        assertThatThrownBy(() -> service.withdraw("alice", BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);

        verify(accountRepository, never()).save(any());
    }

    @Test
    void withdrawRejectsInsufficientFunds() {
        givenAccountExists();

        assertThatThrownBy(() -> service.withdraw("alice", new BigDecimal("150.00")))
                .isInstanceOf(IllegalStateException.class);

        assertThat(account.getBalance()).isEqualByComparingTo("100.00");
        verify(accountRepository, never()).save(any());
    }

    @Test
    void operationsOnUnknownAccountThrow() {
        when(accountRepository.findByOwnerUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getBalance("ghost"))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }
}
