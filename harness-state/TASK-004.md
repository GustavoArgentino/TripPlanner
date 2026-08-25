---
schema: harness.task/v1
id: TASK-004
graph: graph-main@6
revision: 3
status: completed
assigned_to: agent:claude-code
reviewer: agent:code-review-fork
workstream: frontend-trip
agent_role: role:frontend-specialist
execution_context: shared
thread_policy: serialize-in-session
thread_ref: this-session
ownership_lease: lease:TASK-004
isolation: generic:exclusive-directory:frontend-trip
updated_at: 2026-08-24T00:00:00Z
capability_manifest: none
rules_map: none
model_tier: balanced
model_reason: Bounded, well-specified Angular CRUD UI with deterministic acceptance (tests + build pass); no frontier-level ambiguity.
execution_budget: none
review_profile: standard
max_review_rounds: 2
assurance_gate: satisfied (TASK-003 completed, assurance_status: passed)
---

# TASK-004 — Módulo 2 (frontend): UI de Viagens (Trip)

## Outcome

An authenticated user can list, create, edit, and delete their own trips through the Angular UI against the backend from TASK-003.

## Context to load

- `project-context@2` (`harness-state/PROJECT-CONTEXT.md`)
- `graph-main@4` (`harness-state/TASK-GRAPH.md`)
- `harness-state/HANDOFF-TASK-003-01.md` (backend contract, once TASK-003 completes)
- `harness-state/HANDOFF-TASK-002-01.md` (existing auth UI conventions: standalone components, `authInterceptor`, `authGuard`, `API_BASE_URL`)

## Owned paths

- `frontend/src/app/features/trips/**`
- `frontend/src/app/core/trips/**`
- `frontend/src/app/app.routes.ts` (wiring — additive only, do not touch auth routes)

## Constraints

- Standalone components, lazy-loaded routes (project convention from Módulo 0, followed in TASK-002).
- Route(s) protected by the existing `authGuard`; no new auth mechanism.
- Single-user trips only — no sharing/collaboration UI.
- Do not broaden the write set beyond `trips/**` and the additive route wiring.

## Rules to load

- None formalized yet beyond this brief and the project context's Constraints section.

## Required capabilities

- Local Angular CLI build/test/serve (`npx ng build|test|serve`), Chrome (headless, for Karma). Live browser interaction (Claude in Chrome) if connected this session.

## Acceptance criteria

- Trip list view: shows the authenticated user's trips (name, destination, dates); empty state when none.
- Create form: name, destination, start/end dates, optional description; client-side validation mirrors backend rules (required fields, end date not before start date); calls `POST /api/trips`.
- Edit form: same fields, pre-filled, calls `PUT /api/trips/{id}`.
- Delete action with confirmation, calls `DELETE /api/trips/{id}`.
- API errors (401, 404, validation 400) surface a user-visible message, not a silent failure.

## Verification

- `npx ng test --watch=false --browsers=ChromeHeadless`.
- `npx ng build`.
- Live in-browser walkthrough if Claude in Chrome is connected this session; otherwise note it as not run, same as TASK-002.

## Exit

Write a handoff with criterion-level evidence; do not self-accept. Independent review runs before the task is marked complete. On completion, Módulo 2 is fully closed; write the Módulo 2 learning note in the study vault per the active learning profile, if reachable.
