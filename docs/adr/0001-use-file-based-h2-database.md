# ADR-001 — Use file-based H2 as the local database

- **Date:** 2026-06-23
- **Status:** Accepted

## Context

The bank app currently stores account balance in an in-memory field that resets
on every restart. We are adding real persistence (see the login/database/auth
plan) and need a "local database" — one with zero external setup that a learner
can run and inspect easily — while keeping a smooth path to PostgreSQL later.

## Options considered

### In-memory H2
- **Pro:** zero setup, fast, ideal for tests.
- **Con:** data is lost on every restart, so it can't demonstrate real
  persistence for the running app.

### File-based H2 *(chosen)*
- **Pro:** zero external setup; persists to a local file across restarts;
  first-class Spring Boot auto-config; built-in `/h2-console` to browse tables;
  SQL feature set close to PostgreSQL, so the future swap is small.
- **Con:** not a production server; still a single embedded engine.

### SQLite
- **Pro:** ubiquitous, excellent for embedded/single-user/desktop apps.
- **Con:** no official Spring Boot support; requires a community Hibernate
  dialect (`hibernate-community-dialects`) plus the `sqlite-jdbc` driver; no
  built-in console; single-writer with file locking, which fits a concurrent
  web server poorly; dynamic typing and limited `ALTER TABLE` cause friction
  with JPA/Hibernate `ddl-auto`; SQL is further from PostgreSQL.

## Decision

Use **file-based H2** (`jdbc:h2:file:./data/bankdb`) as the local database for
application runs. Tests may use in-memory H2 / `@DataJpaTest` for isolation and
speed.

## Rationale

- Best fit for a learning project: no install, browsable console, and behavior
  closest to the eventual PostgreSQL target.
- JPA abstracts the engine, so moving to PostgreSQL later is a dependency +
  `application.properties` change, not a code rewrite.
- SQLite would add dependencies and a community dialect now and teach habits
  (single-writer, dynamic typing) that don't transfer cleanly to a server DB.
- In-memory-only H2 was rejected because the running app must persist data
  across restarts; in-memory remains useful for tests.

## What is affected

- `pom.xml` — adds `spring-boot-starter-data-jpa` and `com.h2database:h2`.
- `src/main/resources/application.properties` — H2 file URL, `ddl-auto`,
  H2 console.
- Implemented as part of the login/database/auth plan. Supersedes nothing.
- A future ADR would record a move to PostgreSQL.
