package com.example.myfirstwebapp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myfirstwebapp.dto.BalanceResponse;
import com.example.myfirstwebapp.service.BankAccountService;

/**
 * Administrative endpoints. Restricted to ADMIN via method security
 * (@EnableMethodSecurity is configured in SecurityConfig); a non-admin user
 * gets HTTP 403.
 */
@RestController
@RequestMapping("/admin/accounts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountController {

    private final BankAccountService bankAccountService;

    public AdminAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/{username}/balance")
    public BalanceResponse balanceOf(@PathVariable String username) {
        return new BalanceResponse(bankAccountService.getBalance(username));
    }
}
