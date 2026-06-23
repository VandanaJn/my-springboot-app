# my-springboot-app

A learning project building a small **bank account REST API** with Spring Boot,
growing feature by feature: a simple controller first, then JPA persistence, and
(in progress) login and authorization.

Built with **Spring Boot 4.1** and **Java 26**, developed **test-first**.

## Tech stack

| Area | Choice |
|------|--------|
| Framework | Spring Boot 4.1 (Spring MVC) |
| Language | Java 26 |
| Persistence | Spring Data JPA / Hibernate |
| Database | File-based H2 ([ADR-001](docs/adr/0001-use-file-based-h2-database.md)) |
| Money type | `BigDecimal` ([ADR-004](docs/adr/0004-use-bigdecimal-for-money.md)) |
| Build | Maven (wrapper included) |
| Tests | JUnit 5, Mockito, MockMvc, `@DataJpaTest` |

## Getting started

Prerequisites: JDK 26. (Maven is bundled via the wrapper — no install needed.)

```bash
# run the app
./mvnw spring-boot:run

# run the tests
./mvnw test
```

The app starts on `http://localhost:8080`.

## API endpoints

All endpoints require login (session form login). The `/account` endpoints act
on the **logged-in user's own** account; `/admin` endpoints require the `ADMIN`
role. Responses are JSON like `{ "balance": 150.00 }`.

| Method | Path | Body | Access | Description |
|--------|------|------|--------|-------------|
| `GET`  | `/account/balance` | — | any logged-in user | Your balance |
| `POST` | `/account/deposit` | `{ "amount": 100 }` | any logged-in user | Add money (amount must be positive → 400) |
| `POST` | `/account/withdraw` | `{ "amount": 50 }` | any logged-in user | Remove money (overdraft → 409) |
| `GET`  | `/admin/accounts/{username}/balance` | — | `ADMIN` only | Read any user's balance (403 for non-admins) |

Demo users (created on startup): `alice` / `password` (USER, starts at 100.00)
and `admin` / `admin` (ADMIN).

Because state-changing requests are CSRF-protected, the simplest way to try the
API is to log in through the browser at `http://localhost:8080/login`, where the
login form and CSRF token are handled for you.

## Database

A file-based H2 database is created at `./data/bankdb` on first run. Browse it
while the app is running at:

```
http://localhost:8080/h2-console
```

Use JDBC URL `jdbc:h2:file:./data/bankdb`, user `sa`, no password.

## Project structure

```
src/main/java/com/example/myfirstwebapp/
├── controller/   REST endpoints (BankAccountController, AdminAccountController)
├── service/      business rules (BankAccountService)
├── repository/   Spring Data JPA repositories
├── entity/       JPA entities (AppUser, Account)
├── dto/          request/response bodies (TransactionRequest, BalanceResponse)
├── security/     UserDetailsService backed by the database
├── config/       SecurityConfig + DataSeeder
└── exception/    GlobalExceptionHandler (maps errors to HTTP status codes)

docs/             architecture notes and ADRs
```

## Roadmap

- [x] **Phase 1** — JPA persistence layer: `AppUser` / `Account` entities,
      repositories, and a `BankAccountService` (BigDecimal, validation), test-first.
- [x] **Phase 2** — Login: Spring Security session form login, DB-backed users
      with BCrypt ([ADR-002](docs/adr/0002-use-session-form-login.md)).
- [x] **Phase 3** — Authorization: roles + per-account ownership; controller
      wired to the logged-in user, role-gated `/admin` endpoints, and validation
      ([ADR-003](docs/adr/0003-roles-plus-ownership-authorization.md)).

## Architecture & decisions

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the component overview and
[docs/adr/](docs/adr/) for the architecture decision records.
