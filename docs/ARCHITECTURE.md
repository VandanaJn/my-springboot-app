# Architecture

A small Spring Boot 4.1 / Java 26 learning app exposing a bank account REST API.

## Current (implemented)

- **`controller/BankAccountController`** — REST endpoints under `/account`
  (`GET /balance`, `POST /deposit`, `POST /withdraw`).
- **`dto/TransactionRequest`** — record carrying the JSON `amount` body.
- **State:** balance held in an in-memory field on the controller (resets on
  restart; no users, no security).
- **Tests:** `@WebMvcTest` + MockMvc covering controller behavior.

## Planned (per the login/database/auth plan)

- **Persistence:** file-based **H2** via Spring Data JPA — see
  [ADR-001](adr/0001-use-file-based-h2-database.md). `AppUser` and `Account`
  entities, repositories, and a `BankAccountService` (balance as `BigDecimal`).
- **Login:** session form login (`JSESSIONID`), DB-backed users with BCrypt.
- **Authorization:** roles (USER / ADMIN) plus per-account ownership; admin-only
  endpoints under `/admin/**`.

## Decisions

- [ADR-001 — Use file-based H2 as the local database](adr/0001-use-file-based-h2-database.md)
- [ADR-002 — Use session form login for authentication](adr/0002-use-session-form-login.md)
- [ADR-003 — Authorize with roles plus per-account ownership](adr/0003-roles-plus-ownership-authorization.md)
- [ADR-004 — Use BigDecimal for monetary amounts](adr/0004-use-bigdecimal-for-money.md)
