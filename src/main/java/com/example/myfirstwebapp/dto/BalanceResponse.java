package com.example.myfirstwebapp.dto;

import java.math.BigDecimal;

/**
 * JSON response carrying an account balance, e.g. { "balance": 150.00 }.
 */
public record BalanceResponse(BigDecimal balance) {
}
