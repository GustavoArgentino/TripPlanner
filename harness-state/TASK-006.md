---
schema: harness.task/v1
id: TASK-006
graph: graph-main@8
revision: 1
status: active
assigned_to: agent:claude-code
reviewer: agent:code-review-fork
workstream: backend-itinerary
agent_role: role:backend-specialist
execution_context: shared
thread_policy: serialize-in-session
thread_ref: this-session
ownership_lease: lease:TASK-006
isolation: generic:exclusive-directory:backend-itinerary
updated_at: 2026-08-25T00:15:00Z
capability_manifest: none
rules_map: none
model_tier: balanced
model_reason: Bounded, well-specified nested owner-scoped CRUD with deterministic acceptance (tests pass); no frontier-level ambiguity.
execution_budget: none
review_profile: standard
max_review_rounds: 2
assurance_gate: none
---

# TASK-006 — Módulo 3 (backend): Itinerário

## Outcome

An authenticated user can add, list, view, update, and delete itinerary items (day-by-day activities) within their own trips. A user can never see or modify itinerary items belonging to another user's trip.

## Context to load

- `project-context@2` (`harness-state/PROJECT-CONTEXT.md`)
- `graph-main@8` (`harness-state/TASK-GRAPH.md`)
- `harness-state/HANDOFF-TASK-003-01.md` (Trip module contract/conventions: owner-scoped 404-not-403, `TripRepository`, `GlobalExceptionHandler` pattern)
- `backend/src/main/java/com/gustavo/tripplanner/trip/{Trip,TripRepository}.java` (existing Trip entity this nests under)

## Owned paths

- `backend/src/main/java/com/gustavo/tripplanner/itinerary/**`
- `backend/src/test/java/com/gustavo/tripplanner/itinerary/**`
- `backend/src/main/java/com/gustavo/tripplanner/config/GlobalExceptionHandler.java` (additive — new exception mappings only)

## Constraints

- Itinerary items are nested under a trip: `/api/trips/{tripId}/itinerary-items`. Ownership is always resolved through the trip (an item has no independent owner field) — ownership check is "does this trip belong to the authenticated user", same as TASK-003's pattern.
- A trip that doesn't belong to the authenticated user (or doesn't exist) → 404 on every nested endpoint, same non-leaking convention as Trip.
- An itinerary item's `date` must fall within its trip's `[startDate, endDate]` range — reject otherwise (400), mirroring TASK-003's date-validation pattern (a new `InvalidItineraryDateException`, not reusing `InvalidTripDatesException`).
- `ddl-auto: update` acceptable for this dev-only stage (existing project convention).
- Do not change graph state or broaden the write set beyond `itinerary/**` plus the additive `GlobalExceptionHandler` change; do not start TASK-007.

## Rules to load

- None formalized yet beyond this brief and the project context's Constraints section.

## Required capabilities

- Local Maven build/test (`./mvnw`), local Postgres reachable at `localhost:5432` with `DB_USERNAME`/`DB_PASSWORD` set, database `tripplanner` existing. No network/secrets beyond that.

## Acceptance criteria

- `POST /api/trips/{tripId}/itinerary-items` creates an item on the caller's own trip (fields: title, date, startTime optional, location optional, notes optional); validation rejects missing title/date and a date outside the trip's range; 404 if `tripId` isn't the caller's own trip.
- `GET /api/trips/{tripId}/itinerary-items` returns only that trip's items, ordered by date then startTime; 404 if not the caller's trip.
- `GET .../itinerary-items/{itemId}` returns the item if it belongs to the caller's trip, 404 otherwise (wrong trip, wrong item, or item belonging to a different trip than the one in the URL).
- `PUT .../itinerary-items/{itemId}` updates the item's fields under the same ownership/date-range rules, 404 otherwise.
- `DELETE .../itinerary-items/{itemId}` deletes the item under the same ownership rule, 404 otherwise.
- All endpoints require a valid JWT (401 without one) and appear in Swagger UI.
- JUnit/Mockito tests cover: create (incl. date-out-of-range and missing-field validation), list scoped to the trip, get own vs. another user's trip's item (404), update own vs. another's (404), delete own vs. another's (404).

## Verification

- `./mvnw test` passes (excluding the pre-existing `TripplannerApplicationTests.contextLoads` gap — `JWT_SECRET` intentionally unavailable to this session, same as TASK-001/003).
- Manual check via Swagger UI or a REST client: not run this session (same `JWT_SECRET` gap).

## Exit

Write a handoff with criterion-level evidence; do not self-accept. Independent review runs before the task is marked complete. On completion, dispatch TASK-007 (now unblocked).
