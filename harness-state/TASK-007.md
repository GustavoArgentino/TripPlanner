---
schema: harness.task/v1
id: TASK-007
graph: graph-main@9
revision: 2
status: active
assigned_to: agent:claude-code
reviewer: agent:code-review-fork
workstream: frontend-itinerary
agent_role: role:frontend-specialist
execution_context: shared
thread_policy: serialize-in-session
thread_ref: this-session
ownership_lease: lease:TASK-007
isolation: generic:exclusive-directory:frontend-itinerary
updated_at: 2026-08-25T00:55:00Z
capability_manifest: none
rules_map: none
model_tier: balanced
model_reason: Bounded, well-specified Angular CRUD UI with deterministic acceptance (tests + build pass); no frontier-level ambiguity.
execution_budget: none
review_profile: standard
max_review_rounds: 2
assurance_gate: satisfied (TASK-006 completed, assurance_status: passed)
---

# TASK-007 — Módulo 3 (frontend): Itinerário

## Outcome

From the trip list, a user can open a trip's detail page and add, list, edit, and delete that trip's itinerary items, against the backend from TASK-006.

## Context to load

- `project-context@2` (`harness-state/PROJECT-CONTEXT.md`)
- `graph-main@8` (`harness-state/TASK-GRAPH.md`)
- `harness-state/HANDOFF-TASK-006-01.md` (backend contract, once TASK-006 completes)
- `harness-state/HANDOFF-TASK-004-01.md` (existing trips UI conventions: `TripService`, `_trip-shared.scss`, date handling as plain `YYYY-MM-DD` strings)

## Owned paths

- `frontend/src/app/features/trips/trip-detail/**` (new)
- `frontend/src/app/core/itinerary/**` (new)
- `frontend/src/app/features/trips/trips.routes.ts` (additive route)
- `frontend/src/app/features/trips/trip-list/trip-list.component.html` (additive — link from a trip card into its detail page)
- `frontend/src/app/features/trips/trip-list/trip-list.component.scss` (scope expansion during execution — styling for the new link; see handoff)
- `frontend/src/app/features/trips/trip-list/trip-list.component.ts` (scope expansion during review remediation — switched to a shared `formatDate` util; see handoff)
- `frontend/src/app/features/trips/trip-date.util.ts` (new — scope expansion during review remediation, shared date formatter)

## Constraints

- Standalone components, lazy-loaded routes, matching project convention.
- Route protected by the existing `authGuard` (inherited from the parent `/trips` route — no new guard needed).
- Do not touch `frontend/src/app/features/auth/**` or backend code.
- Do not broaden the write set beyond what's listed above.

## Rules to load

- None formalized yet beyond this brief and the project context's Constraints section.

## Required capabilities

- Local Angular CLI build/test (`npx ng build|test`), Chrome (headless, for Karma). Live browser interaction (Claude in Chrome) if connected this session.

## Acceptance criteria

- Trip detail page (e.g. `/trips/:id`): shows the trip's own info (name, destination, dates) and its itinerary items, ordered by date/time; empty state when there are none.
- Create form: title, date, optional start time, optional location, optional notes; client-side validation mirrors backend rules (required title/date, date within the trip's own date range); calls `POST .../itinerary-items`.
- Edit form: same fields, pre-filled, calls `PUT .../itinerary-items/{id}`.
- Delete action with confirmation, calls `DELETE .../itinerary-items/{id}`.
- API errors (401/404/400) surface a user-visible message.
- The trip list links each trip into its detail page (e.g. clicking the trip name), without removing the existing edit/delete actions.

## Verification

- `npx ng test --watch=false --browsers=ChromeHeadless`.
- `npx ng build`.
- Live in-browser walkthrough if Claude in Chrome is connected this session; otherwise note as not run.

## Exit

Write a handoff with criterion-level evidence; do not self-accept. Independent review runs before the task is marked complete. On completion, Módulo 3 is fully closed.
