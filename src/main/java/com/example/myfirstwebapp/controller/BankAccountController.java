package com.example.myfirstwebapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.myfirstwebapp.dto.TransactionRequest;

@RestController
@RequestMapping("/account")
public class BankAccountController {

    // Simple in-memory balance (resets every time the app restarts).
    private double balance = 0.0;

    // GET http://localhost:8080/account/balance
    @GetMapping("/balance")
    public String getBalance() {
        return "Current balance: " + balance;
    }

    // POST http://localhost:8080/account/deposit   body: { "amount": 100 }
    @PostMapping("/deposit")
    public String deposit(@RequestBody TransactionRequest request) {
        double amount = request.amount();
        if (amount <= 0) {
            return "Deposit amount must be positive.";
        }
        balance += amount;
        return "Deposited " + amount + ". New balance: " + balance;
    }

    // POST http://localhost:8080/account/withdraw   body: { "amount": 50 }
    @PostMapping("/withdraw")
    public String withdraw(@RequestBody TransactionRequest request) {
        double amount = request.amount();
        if (amount <= 0) {
            return "Withdrawal amount must be positive.";
        }
        if (amount > balance) {
            return "Insufficient funds. Current balance: " + balance;
        }
        balance -= amount;
        return "Withdrew " + amount + ". New balance: " + balance;
    }
}
