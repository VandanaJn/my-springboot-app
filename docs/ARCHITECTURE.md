# Architecture

A small Spring Boot 4.1 / Java 26 learning app exposing a bank account REST API.

## Current (implemented)

- **Persistence:** file-based **H2** via Spring Data JPA
  ([ADR-001](adr/0001-use-file-based-h2-database.md)). `entity/AppUser` and
  `entity/Account` (balance as `BigDecimal`, [ADR-004](adr/0004-use-bigdecimal-for-money.md)),
  with `repository/` Spring Data repositories.
- **Domain logic:** `service/BankAccountService` — balance / deposit / withdraw
  with validation (positive amount, sufficient funds), per user.
- **Login:** Spring Security session form login (`JSESSIONID`), DB-backed users
  with BCrypt ([ADR-002](adr/0002-use-session-form-login.md)). `security/AppUserDetailsService`
  loads users; `config/SecurityConfig` defines the filter chain; `config/DataSeeder`
  seeds demo users.
- **Authorization:** roles (USER / ADMIN) plus per-account ownership
  ([ADR-003](adr/0003-roles-plus-ownership-authorization.md)).
  `controller/BankAccountController` acts on the logged-in user's own account;
  `controller/AdminAccountController` (`/admin/**`) is `ADMIN`-only via
  `@PreAuthorize`.
- **Errors:** `exception/GlobalExceptionHandler` maps domain exceptions to HTTP
  status codes (400 / 409 / 404); `@Valid` rejects bad input with 400.
- **Tests:** unit (Mockito), `@DataJpaTest`, and `@SpringBootTest` + MockMvc
  integration tests covering auth, ownership, and validation.

## Request flow

```
HTTP request
  → Security filter chain (authenticated? role allowed? CSRF valid?)
  → Controller (resolves the account from the logged-in principal)
  → BankAccountService (business rules, @Transactional)
  → Repository / JPA → H2
```

## Decisions

- [ADR-001 — Use file-based H2 as the local database](adr/0001-use-file-based-h2-database.md)
- [ADR-002 — Use session form login for authentication](adr/0002-use-session-form-login.md)
- [ADR-003 — Authorize with roles plus per-account ownership](adr/0003-roles-plus-ownership-authorization.md)
- [ADR-004 — Use BigDecimal for monetary amounts](adr/0004-use-bigdecimal-for-money.md)
