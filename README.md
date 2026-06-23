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

The bank endpoints live under `/account`. Amounts are sent as JSON.

| Method | Path | Body | Description |
|--------|------|------|-------------|
| `GET`  | `/account/balance` | — | Current balance |
| `POST` | `/account/deposit` | `{ "amount": 100 }` | Add money |
| `POST` | `/account/withdraw` | `{ "amount": 50 }` | Remove money (rejects overdraft) |

Example:

```bash
curl -X POST http://localhost:8080/account/deposit \
     -H "Content-Type: application/json" \
     -d '{ "amount": 100 }'
```

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
├── controller/   REST endpoints (BankAccountController)
├── service/      business rules (BankAccountService)
├── repository/   Spring Data JPA repositories
├── entity/       JPA entities (AppUser, Account)
└── dto/          request bodies (TransactionRequest)

docs/             architecture notes and ADRs
```

## Roadmap

- [x] **Phase 1** — JPA persistence layer: `AppUser` / `Account` entities,
      repositories, and a `BankAccountService` (BigDecimal, validation), test-first.
- [ ] **Phase 2** — Login: Spring Security session form login, DB-backed users
      with BCrypt ([ADR-002](docs/adr/0002-use-session-form-login.md)).
- [ ] **Phase 3** — Authorization: roles + per-account ownership; wire the
      controller to the logged-in user ([ADR-003](docs/adr/0003-roles-plus-ownership-authorization.md)).

> Note: the persistence layer is built and tested, but the controller still uses
> a temporary in-memory balance — it is wired to the database and the logged-in
> user in Phase 3.

## Architecture & decisions

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the component overview and
[docs/adr/](docs/adr/) for the architecture decision records.
