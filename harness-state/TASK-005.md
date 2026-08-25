---
schema: harness.task/v1
id: TASK-005
graph: graph-main@7
revision: 1
status: completed
assigned_to: agent:claude-code
reviewer: agent:code-review-fork
workstream: frontend-shell
agent_role: role:frontend-specialist
execution_context: shared
thread_policy: serialize-in-session
thread_ref: this-session
ownership_lease: lease:TASK-005
isolation: generic:exclusive-directory:frontend-shell
updated_at: 2026-08-25T00:00:00Z
capability_manifest: none
rules_map: none
model_tier: balanced
model_reason: Bounded, well-specified UX fix (global nav/logout + a new static-content landing page); no frontier-level ambiguity.
execution_budget: none
review_profile: standard
max_review_rounds: 2
assurance_gate: none
---

# TASK-005 — UX fix: global logout/nav + public landing page

## Outcome

Requested directly by the user, out of band from the Módulo roadmap (explicitly pausing Módulo 3 decomposition to fix this first): (1) a logout control reachable from every authenticated page, not just `/home`; (2) an attractive public landing page at `/` representing the app's purpose, replacing the previous straight-to-`/login` redirect.

## Context to load

- `harness-state/HANDOFF-TASK-002-01.md`, `HANDOFF-TASK-004-01.md` (existing auth/trips conventions)
- `frontend/src/app/app.component.*`, `frontend/src/app/app.routes.ts`, `frontend/src/app/features/home/**` (pre-existing app shell, found to have a toolbar that was never wired to auth state)

## Owned paths

- `frontend/src/app/app.component.ts`, `.html`, `.scss`, `.spec.ts`
- `frontend/src/app/app.routes.ts`
- `frontend/src/app/features/home/home.component.ts`, `.html`
- `frontend/src/app/features/landing/**` (new)

## Constraints

- Reuse the existing `AuthService.isAuthenticated`/`currentUserEmail` pattern; no new auth mechanism.
- Standalone components, lazy-loaded landing route, matching project convention.
- Do not touch `frontend/src/app/features/trips/**`, `frontend/src/app/features/auth/**`, or any backend code — out of scope for this fix.

## Rules to load

- None formalized yet beyond this brief.

## Required capabilities

- Local Angular CLI build/test (`npx ng build|test`), Chrome (headless, for Karma). Live browser interaction (Claude in Chrome) — attempted, extension not connected this session.

## Acceptance criteria

- A logout action is reachable from `/home` and `/trips` (and any other authenticated route) without navigating back to `/home` first.
- `/` renders a public, unauthenticated-accessible landing page with hero imagery and a clear call to action (register/login if anonymous, "ver minhas viagens" if already authenticated) instead of redirecting straight to `/login`.
- No existing route, guard, or auth behavior regresses.

## Verification

- `npx ng test --watch=false --browsers=ChromeHeadless`.
- `npx ng build`.
- Live in-browser walkthrough: not run (Claude in Chrome extension not connected this session).

## Exit

Write a handoff with criterion-level evidence; do not self-accept. Independent review runs before the task is marked complete. Módulo 3 (Itinerário) remains un-decomposed per the user's explicit instruction to pause module progression until this fix landed.
