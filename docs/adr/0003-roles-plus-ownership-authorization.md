# ADR-003 — Authorize with roles plus per-account ownership

- **Date:** 2026-06-23
- **Status:** Accepted

## Context

With authentication in place (see [ADR-002](0002-use-session-form-login.md)), the
bank app needs an authorization model. Accounts hold money, so access control
must prevent one user from touching another user's account, while still allowing
an administrator to oversee all accounts.

## Options considered

### Authenticated-only
- **Pro:** simplest — any logged-in user passes.
- **Con:** any user could read/modify any account. Unacceptable for a bank app.

### Roles only (USER / ADMIN)
- **Pro:** simple, guards endpoints by role.
- **Con:** does not stop one USER from accessing another USER's account; no
  per-resource protection.

### Roles + per-account ownership *(chosen)*
- **Pro:** users are confined to their own account (ownership), and role-gated
  endpoints (`/admin/**`) give ADMIN broader access. Realistic for a bank.
- **Con:** slightly more logic — ownership must be enforced, not just role
  checks.

## Decision

Use **roles (USER / ADMIN) plus per-account ownership**. Account-mutating
endpoints derive "my account" from the authenticated principal, so a user
structurally cannot name another user's account. ADMIN-only endpoints under
`/admin/**` are guarded with `@PreAuthorize("hasRole('ADMIN')")` /
`@EnableMethodSecurity`.

## Rationale

- Ownership is enforced by construction (the controller resolves the account
  from the principal), which is simpler and safer than passing an account id and
  checking it.
- Roles handle the cross-cutting admin capability cleanly.
- Authenticated-only and roles-only were rejected because neither prevents
  cross-user account access.

## What is affected

- `controller/BankAccountController` (resolves principal's account),
  `controller/AdminAccountController` (`/admin/**`, role-gated),
  `entity/Account` (owner relationship), `config/SecurityConfig`
  (`@EnableMethodSecurity`).
