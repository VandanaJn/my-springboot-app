package com.example.myfirstwebapp.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.example.myfirstwebapp.entity.Account;
import com.example.myfirstwebapp.entity.AppUser;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void findsAccountByOwnerUsername() {
        AppUser alice = new AppUser("alice", "hash", Set.of("USER"));
        entityManager.persist(alice);
        entityManager.persist(new Account(alice, new BigDecimal("100.00")));
        entityManager.flush();

        Optional<Account> found = accountRepository.findByOwnerUsername("alice");

        assertThat(found).isPresent();
        assertThat(found.get().getOwner().getUsername()).isEqualTo("alice");
        assertThat(found.get().getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void returnsEmptyWhenNoAccountForUsername() {
        assertThat(accountRepository.findByOwnerUsername("ghost")).isEmpty();
    }
}
