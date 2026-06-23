# ADR-004 — Use BigDecimal for monetary amounts

- **Date:** 2026-06-23
- **Status:** Accepted

## Context

The bank app currently represents balances and transaction amounts as `double`.
As persistence and real transaction logic are added, the money type must be
correct for currency arithmetic.

## Options considered

### `double` (current)
- **Pro:** simple, already in place.
- **Con:** binary floating point cannot represent many decimal values exactly
  (e.g. `0.1 + 0.2 != 0.3`), causing rounding errors that are unacceptable for
  money.

### `BigDecimal` *(chosen)*
- **Pro:** exact base-10 arithmetic with explicit scale and rounding; the
  standard JVM type for currency; maps cleanly to a SQL `DECIMAL`/`NUMERIC`
  column via JPA.
- **Con:** more verbose (method calls instead of `+`/`-`); must choose a scale
  and rounding mode.

### Integer minor units (e.g. cents as `long`)
- **Pro:** exact and compact.
- **Con:** loses an explicit decimal scale; error-prone conversions at the API
  boundary; less readable for a learning project.

## Decision

Use **`BigDecimal`** for balances and amounts throughout the domain, API, and
persistence (SQL `DECIMAL`). Replaces the existing `double`.

## Rationale

- Correctness: money must not suffer floating-point rounding.
- `BigDecimal` is the idiomatic JVM/JPA choice and maps directly to a decimal
  column.
- Minor-unit integers were rejected as less readable and more conversion-prone
  for this context.

## What is affected

- `dto/TransactionRequest` (amount type + `@Positive`), `entity/Account`
  (balance), `service/BankAccountService`, `controller/BankAccountController`,
  and the controller tests (response formatting changes from `double`).
