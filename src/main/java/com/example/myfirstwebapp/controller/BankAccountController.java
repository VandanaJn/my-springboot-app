package com.example.myfirstwebapp.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myfirstwebapp.dto.BalanceResponse;
import com.example.myfirstwebapp.dto.TransactionRequest;
import com.example.myfirstwebapp.service.BankAccountService;

import jakarta.validation.Valid;

/**
 * Account endpoints scoped to the currently logged-in user. The account is
 * resolved from the authenticated principal's name, so a user can only ever
 * read or modify their own account (ownership is enforced by construction).
 */
@RestController
@RequestMapping("/account")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance(Principal principal) {
        return new BalanceResponse(bankAccountService.getBalance(principal.getName()));
    }

    @PostMapping("/deposit")
    public BalanceResponse deposit(@Valid @RequestBody TransactionRequest request, Principal principal) {
        return new BalanceResponse(bankAccountService.deposit(principal.getName(), request.amount()));
    }

    @PostMapping("/withdraw")
    public BalanceResponse withdraw(@Valid @RequestBody TransactionRequest request, Principal principal) {
        return new BalanceResponse(bankAccountService.withdraw(principal.getName(), request.amount()));
    }
}
