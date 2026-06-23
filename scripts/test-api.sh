#!/usr/bin/env bash
#
# Manual API smoke test for the bank app, logging in as the seeded user "alice".
#
# Prereq:  app running ->  ./mvnw spring-boot:run
# Usage:   bash scripts/test-api.sh  [base_url]
#          (base_url defaults to http://localhost:8080)
#
set -uo pipefail

BASE_URL="${1:-http://localhost:8080}"
USERNAME="alice"
PASSWORD="password"
COOKIES="$(mktemp)"
trap 'rm -f "$COOKIES"' EXIT

# Read the (non-HttpOnly) CSRF token value out of the cookie jar.
csrf_token() { awk '$6=="XSRF-TOKEN"{print $7}' "$COOKIES"; }
hr() { printf -- '------------------------------------------------------------\n'; }

echo "Base URL: $BASE_URL    user: $USERNAME"
hr

# 1) Prime the session: GET the login page so the server issues a CSRF cookie.
curl -s -c "$COOKIES" "$BASE_URL/login" -o /dev/null
CSRF="$(csrf_token)"

# 2) Log in via the form (CSRF token submitted as the _csrf parameter).
#    -L follows the success redirect; the cookie jar is updated.
curl -s -b "$COOKIES" -c "$COOKIES" -L \
  --data-urlencode "username=$USERNAME" \
  --data-urlencode "password=$PASSWORD" \
  --data-urlencode "_csrf=$CSRF" \
  "$BASE_URL/login" -o /dev/null
echo "[1] logged in as $USERNAME"
hr

# 3) GET balance (GET needs no CSRF). Also refreshes the CSRF cookie.
echo "[2] GET  /account/balance"
curl -s -b "$COOKIES" -c "$COOKIES" "$BASE_URL/account/balance"; echo
hr

CSRF="$(csrf_token)"

# 4) Deposit (POST needs the CSRF token echoed in the X-XSRF-TOKEN header).
echo "[3] POST /account/deposit   {\"amount\":50.00}"
curl -s -b "$COOKIES" -c "$COOKIES" \
  -H "Content-Type: application/json" -H "X-XSRF-TOKEN: $CSRF" \
  -d '{"amount":50.00}' "$BASE_URL/account/deposit"; echo
hr

# 5) Withdraw.
echo "[4] POST /account/withdraw  {\"amount\":30.00}"
curl -s -b "$COOKIES" -c "$COOKIES" \
  -H "Content-Type: application/json" -H "X-XSRF-TOKEN: $CSRF" \
  -d '{"amount":30.00}' "$BASE_URL/account/withdraw"; echo
hr

# 6) Error / security cases — show just the HTTP status code.
echo "[5] error & security cases (expected code in parentheses):"
status() { curl -s -o /dev/null -w '%{http_code}' "$@"; }

echo "    deposit WITHOUT csrf token     -> $(status -b "$COOKIES" \
  -H 'Content-Type: application/json' -d '{"amount":50}' \
  "$BASE_URL/account/deposit")   (403)"

echo "    deposit negative amount        -> $(status -b "$COOKIES" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $CSRF" \
  -d '{"amount":-5}' "$BASE_URL/account/deposit")   (400)"

echo "    withdraw beyond balance        -> $(status -b "$COOKIES" \
  -H 'Content-Type: application/json' -H "X-XSRF-TOKEN: $CSRF" \
  -d '{"amount":999999}' "$BASE_URL/account/withdraw")   (409)"

echo "    alice (USER) hits /admin/**    -> $(status -b "$COOKIES" \
  "$BASE_URL/admin/accounts/alice/balance")   (403)"
hr
echo "done."
