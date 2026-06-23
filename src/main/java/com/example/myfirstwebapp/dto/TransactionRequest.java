package com.example.myfirstwebapp.dto;

// Simple data carrier for the JSON body of deposit/withdraw requests.
// A record auto-generates the constructor and the getAmount() accessor.
public record TransactionRequest(double amount) {
}
