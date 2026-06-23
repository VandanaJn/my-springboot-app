# ADR-002 — Use session form login for authentication

- **Date:** 2026-06-23
- **Status:** Accepted

## Context

The app is adding authentication. It is a learning project built on Spring Boot
4.1 / Spring Security 7, currently a small REST API with no users. We need a
login mechanism that teaches core Spring Security concepts without excessive
machinery, while still following current best practices.

## Options considered

### Session form login *(chosen)*
- **Pro:** built into Spring Security with minimal config; classic, well-
  documented flow (`UsernamePasswordAuthenticationFilter`, `JSESSIONID` cookie);
  no token issuance/signing/refresh to manage; easy to reason about for a first
  app.
- **Con:** stateful (server-side sessions); requires CSRF handling (on by
  default in Security 7); less natural for SPAs/mobile/horizontal scaling.

### JWT stateless tokens (OAuth2 Resource Server)
- **Pro:** the 2026 REST/SPA standard; stateless and scales horizontally.
- **Con:** significantly more moving parts for a beginner — token issuance,
  signing keys, expiry, refresh-token rotation, `spring-boot-starter-security-oauth2-resource-server`.

### HTTP Basic
- **Pro:** simplest possible; great for curl.
- **Con:** credentials sent on every request; no real login/logout UX; weaker
  fit for a browser-facing app.

## Decision

Use **session form login** with Spring Security 7: `formLogin()` defaults,
server-side sessions, BCrypt-hashed DB users, and CSRF protection enabled
(`CookieCsrfTokenRepository`), with `/h2-console/**` exempted.

## Rationale

- Teaches the fundamentals (`SecurityFilterChain`, `UserDetailsService`,
  `PasswordEncoder`, authorization rules) without token complexity.
- CSRF stays on, which is the correct default for a cookie/session app.
- JWT was deferred as a possible future enhancement, not a current need — the
  app has no SPA/mobile client requiring stateless auth.

## What is affected

- `config/SecurityConfig` (`SecurityFilterChain`, `formLogin`, CSRF),
  `security/AppUserDetailsService`, `config/DataSeeder`.
- A future move to JWT would be recorded as a new ADR superseding this one.
