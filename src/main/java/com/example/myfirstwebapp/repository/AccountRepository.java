package com.example.myfirstwebapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.myfirstwebapp.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByOwnerUsername(String username);
}
