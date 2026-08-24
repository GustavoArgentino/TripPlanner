---
schema: harness.task/v1
id: TASK-002
graph: graph-main@2
revision: 1
status: active
assigned_to: agent:claude-code
reviewer: agent:code-review-fork
workstream: frontend-auth
agent_role: role:frontend-specialist
execution_context: shared
thread_policy: serialize-in-session
thread_ref: this-session
ownership_lease: lease:TASK-002
isolation: generic:exclusive-directory:frontend-auth
updated_at: 2026-08-24T00:00:00Z
capability_manifest: none
rules_map: none
model_tier: balanced
model_reason: Bounded, well-specified Angular auth UI with deterministic acceptance (tests + build pass); no frontier-level ambiguity.
execution_budget: none
review_profile: standard
max_review_rounds: 2
assurance_gate: none
---

# TASK-002 — Módulo 1 (frontend): UI de login/registro

## Outcome

A user can register and log in through the Angular UI against the backend from TASK-001; the JWT is attached automatically to authenticated requests, and unauthenticated users are redirected away from protected routes.

## Context to load

- `project-context@2` (`harness-state/PROJECT-CONTEXT.md`)
- `graph-main@2` (`harness-state/TASK-GRAPH.md`)
- `harness-state/HANDOFF-TASK-001-01.md` (backend contract: `POST /api/auth/register`, `POST /api/auth/login`, `AuthResponse{token,tokenType}`, 401 on missing/invalid token)

## Owned paths

- `frontend/src/app/features/auth/**`
- `frontend/src/app/core/**`
- `frontend/src/app/features/home/**` (scope note: added during execution — a minimal placeholder page was needed to have something for the route guard to protect, since Módulo 8/Dashboard doesn't exist yet; it will be replaced wholesale when that module is built)
- `frontend/src/app/app.routes.ts`, `frontend/src/app/app.config.ts` (wiring)
- `frontend/package.json` (added `@angular/animations`, required by Angular Material for full component behavior — was missing despite the Módulo 0 note claiming it was configured)

## Constraints

- Standalone components, lazy-loaded routes (project convention from Módulo 0).
- No real backend URL configuration system yet — a single `API_BASE_URL` constant is used (`core/config/api-config.ts`); a proper environments setup is deferred until there's an actual deployment target.
- Single-user trips only — no multi-user/collaborative UI concerns here.

## Rules to load

- None formalized yet beyond this brief and the project context's Constraints section.

## Required capabilities

- Local Angular CLI build/test/serve (`npx ng build|test|serve`), Chrome (headless, for Karma) — available and used. Live browser interaction (Claude in Chrome) — attempted, extension not connected this session, so no interactive in-browser walkthrough was performed; unit tests + production build are the verification evidence instead.

## Acceptance criteria

- Login form (`/login`): email + password, client-side validation, calls `POST /api/auth/login`, stores JWT, navigates to `/home` on success, shows an error message on 401.
- Register form (`/register`): name + email + password (min 8 chars), calls `POST /api/auth/register`, stores JWT, navigates to `/home` on success, shows an error message on 409 (duplicate email).
- `authInterceptor` attaches `Authorization: Bearer <token>` only to requests targeting the API base URL, and only when a token is present.
- `authGuard` blocks navigation to `/home` and redirects to `/login` when not authenticated (no token, or expired token).
- `AuthService` exposes `isAuthenticated()` and `currentUserEmail()` derived from the stored JWT (decoded client-side, not re-validated — server remains the source of truth for actual authorization).

## Verification

- `npx ng test --watch=false --browsers=ChromeHeadless` — unit tests for `jwt.util`, `AuthService`, `authGuard`, `authInterceptor`.
- `npx ng build` — production build, catches template/type errors.
- Not run: live in-browser walkthrough (Claude in Chrome extension not connected this session) and a real end-to-end run against the live backend (backend not running this session, DB credentials not available to this session).

## Exit

Write a handoff with criterion-level evidence; do not self-accept. Independent review runs before the task is marked complete.
