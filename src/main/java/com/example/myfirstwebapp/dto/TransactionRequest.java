package com.example.myfirstwebapp.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * JSON body for deposit/withdraw requests, e.g. { "amount": 100.00 }.
 * Bean Validation rejects a missing or non-positive amount with HTTP 400
 * before the controller logic runs.
 */
public record TransactionRequest(

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be positive")
        BigDecimal amount) {
}
