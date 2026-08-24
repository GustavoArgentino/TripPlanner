---
schema: harness.handoff/v1
id: HANDOFF-TASK-002-01
task: TASK-002@1
attempt: 1
status: completed
author: agent:claude-code
workstream: frontend-auth
agent_role: role:frontend-specialist
execution_context: shared
thread_ref: this-session
created_at: 2026-08-24T00:00:00Z
model_tier_used: balanced
model_route_changes: none
execution_budget: none
---

# Handoff — TASK-002

## Result

Angular UI for register/login is implemented: reactive forms, a JWT-attaching HTTP interceptor, a route guard, and a minimal placeholder `/home` page to prove the guard works end to end.

- Execution context: this Claude Code session (no isolated/parallel context available); no adapter needed.

## Changes

- `frontend/src/app/core/config/api-config.ts`: single `API_BASE_URL` constant (no full environments setup yet — deferred until there's a real deployment target).
- `frontend/src/app/core/auth/{auth.models,jwt.util,auth.service,auth.interceptor,auth.guard}.ts`: token storage/decoding, register/login calls, Bearer-header interceptor, route guard.
- `frontend/src/app/features/auth/{login,register}/`: standalone components with reactive forms + Angular Material, sharing `_auth-form.scss`.
- `frontend/src/app/features/auth/auth.routes.ts`: lazy routes for `/login`, `/register`.
- `frontend/src/app/features/home/`: minimal placeholder page (`/home`, guarded) — scope addition beyond the original `write_set`, see TASK-002.md's Owned paths note.
- `frontend/src/app/app.routes.ts`, `frontend/src/app/app.config.ts`: wired routing, `provideHttpClient` with the interceptor, `provideAnimationsAsync()`.
- `frontend/package.json`/`package-lock.json`: added `@angular/animations` (was missing despite the Módulo 0 note claiming it was configured).

## Change unit and authority

- Coherent unit: frontend auth UI (Módulo 1 frontend half); depends on TASK-001 (backend contract).
- Split boundaries: backend vs. frontend, matching `write_set` ownership.
- Commit/integration/push/deploy/publication authority: not exercised this task — per explicit user instruction (2026-08-24), the workflow going forward is: this session prepares commits/tells the user the commit message, the user runs `git add`/`git commit`/`git push` themselves.

## Acceptance evidence

| Criterion | Result | Evidence |
| --- | --- | --- |
| Login form: validation, calls login, stores JWT, navigates, shows 401 error | pass (unit-level) | `auth.service.spec.ts` covers the service half; component behavior not exercised by a live browser this session (see Verification run) |
| Register form: validation, calls register, stores JWT, navigates, shows 409 error | pass (unit-level) | same as above |
| `authInterceptor` attaches Bearer only for API requests, only with a token | pass | `auth.interceptor.spec.ts` (3 cases: with token, without token, non-API URL) |
| `authGuard` redirects to `/login` when unauthenticated, allows when authenticated | pass | `auth.guard.spec.ts` (2 cases) |
| `isAuthenticated()`/`currentUserEmail()` correctly reflect token expiry over time | pass | `auth.service.spec.ts` regression test added after independent review (see Review request) |
| JWT decoding handles non-ASCII claim values correctly | pass | `jwt.util.spec.ts` regression test added after independent review |

## Verification run

- Command: `npx ng test --watch=false --browsers=ChromeHeadless` — 19/19 passed (17 initial + 2 regression tests added after review).
- Command: `npx ng build` — clean production build both before and after the review fixes; `login-component`/`register-component`/`home-component`/`auth-routes` all confirmed as separate lazy chunks.
- Not run: live in-browser walkthrough — the Claude in Chrome extension was not connected this session, so the actual UI (form rendering, click-through register → login → home → logout) was not visually verified. Also not run: a real end-to-end flow against the live backend (backend not running this session; DB credentials intentionally unavailable to this session, same limitation as TASK-001).
- Environment: Angular CLI 19.2.27, Karma 6.4.4 + Chrome Headless 151, same local machine as TASK-001.

## Execution budget

- Goal lineage: TASK-002, attempt 1.
- Usage: 1 implementation pass + 1 fix pass after independent review; 0 no-progress cycles; 0 context expansions.
- Decision: `continue` (task complete, no ceiling reached).
- Token/cost measurement: unavailable.

## Discoveries and risks

- `@angular/animations` was never actually added to `package.json` despite Módulo 0's note claiming `provideAnimationsAsync()` was configured — added it now; worth double-checking other Módulo 0 claims against actual code if it matters later.
- No environments/`fileReplacements` setup yet for the API base URL — fine for local dev against `localhost:8080`, but will need real config before any deployment.
- The `/home` placeholder is throwaway — Módulo 8 (Dashboard) will replace it; not worth investing more in its design.
- UI was not visually verified in a live browser this session (Claude in Chrome extension not connected) — recommend the user runs `ng serve` + the backend and clicks through register → login → home → logout at least once.

## Routing and authority

- Tier used and reason: balanced — bounded, deterministic Angular UI work with standard Material/reactive-forms patterns, no frontier-level ambiguity.
- Escalation/decomposition: none.
- Routing granted no additional permissions and removed no review or verification gate.

## Review request

- Independent review already run (forked `code-review` skill agent) before this handoff was written. It found 3 issues, all fixed in this same task:
  1. `isAuthenticated`/`currentUserEmail` were Angular `computed()` signals whose only tracked dependency was the token signal; since expiry is time-based, the memoized result went stale after the token expired without `login()`/`logout()` being called again (e.g. the guard could keep admitting an expired-token user on subsequent navigations). Fixed: converted to plain arrow-function methods that re-evaluate `isJwtExpired()` on every call. Added a regression test using `spyOn(Date, 'now')` to simulate time passing.
  2. `login.component.scss` and `register.component.scss` were byte-identical, duplicating the same 4 rules. Fixed: extracted into a shared `_auth-form.scss` Sass partial, both components now `@use` it.
  3. `decodeBase64Url` used `atob()` directly, which decodes to Latin1, not UTF-8 — any non-ASCII character in a JWT claim (e.g. an accented name) would come out mangled. Fixed: bytes are now re-decoded via `TextDecoder('utf-8')`. Added a regression test with an accented email in the `sub` claim (and had to fix the test helper itself, which had the same Latin1-vs-UTF-8 bug).
- Focus for any follow-up human review: none outstanding from this round.

## User-facing closeout

- Outcome: Frontend of Módulo 1 (Autenticação & Usuários) is implemented, self-tested, and independently reviewed with all findings fixed.
- Stage: Módulo 1 complete (backend + frontend). Next: Módulo 2 (Viagens/Trip) is not yet decomposed into tasks.
- Progress: TASK-002 completed.
- Material changes: see Changes above.
- Verification: `npx ng test --watch=false --browsers=ChromeHeadless` → 19/19 passed; `npx ng build` → clean. Live browser walkthrough and real backend integration left for the user.
- Lifecycle state: completed.
- Blockers: None.
- Next action: user reviews/commits/pushes this work (workflow changed per explicit instruction — this session no longer commits or pushes on its own); then decide whether to decompose Módulo 2 or first manually verify Módulo 1 end to end.
- Inspectable paths: `harness-state/TASK-002.md`, `harness-state/HANDOFF-TASK-002-01.md`, `harness-state/TASK-GRAPH.md`, `harness-state/PENDING.md`.
- Human action required: run `ng serve` (frontend) + the backend locally and click through the auth flow once, since it wasn't visually verified this session. Then provide the commit message you'll use (or ask this session for one) and push yourself.
