---
schema: harness.task/v1
id: TASK-003
graph: graph-main@4
revision: 1
status: active
assigned_to: agent:claude-code
reviewer: agent:code-review-fork
workstream: backend-trip
agent_role: role:backend-specialist
execution_context: shared
thread_policy: serialize-in-session
thread_ref: this-session
ownership_lease: lease:TASK-003
isolation: generic:exclusive-directory:backend-trip
updated_at: 2026-08-24T00:00:00Z
capability_manifest: none
rules_map: none
model_tier: balanced
model_reason: Bounded, well-specified owner-scoped CRUD with deterministic acceptance (tests pass); no frontier-level ambiguity.
execution_budget: none
review_profile: standard
max_review_rounds: 2
assurance_gate: none
---

# TASK-003 — Módulo 2 (backend): Viagens (Trip) CRUD

## Outcome

An authenticated user can create, list, view, update, and delete their own trips through the Spring Boot backend. A user can never see or modify another user's trips.

## Context to load

- `project-context@2` (`harness-state/PROJECT-CONTEXT.md`)
- `graph-main@4` (`harness-state/TASK-GRAPH.md`)
- `harness-state/HANDOFF-TASK-001-01.md` (auth contract: JWT, `SecurityContext` principal is the user's email; `User` entity has UUID `id`)
- `backend/src/main/java/com/gustavo/tripplanner/user/User.java`, `UserRepository.java` (existing owner-side entity)
- Study-vault module notes for Módulo 2 were referenced in `project-context@2` but are not reachable from this session (external vault path not present in the repo working tree) — proceeding from the approved project context's Scope/Constraints only; flag in the handoff if this later conflicts with the vault.

## Owned paths

- `backend/src/main/java/com/gustavo/tripplanner/trip/**`
- `backend/src/test/java/com/gustavo/tripplanner/trip/**`

## Constraints

- Single-user trips only — no sharing/collaboration fields or endpoints (per `project-context@2` Scope, confirmed out of scope D-004).
- Every endpoint resolves the owner from the authenticated JWT principal (never a client-supplied user id); ownership is enforced on read/update/delete (404, not 403, for another user's trip id, to avoid existence leakage — consistent with not revealing other users' data).
- `ddl-auto: update` acceptable for this dev-only stage (existing project convention).
- Do not change graph state or broaden the write set beyond `trip/**`; do not start TASK-004.
- No real third-party booking/reservation integrations (out of scope, D-004).

## Rules to load

- None formalized yet beyond this brief and the project context's Constraints section.

## Required capabilities

- Local Maven build/test (`./mvnw`), local Postgres reachable at `localhost:5432` with `DB_USERNAME`/`DB_PASSWORD` set, database `tripplanner` existing. No network/secrets beyond that.

## Acceptance criteria

- `POST /api/trips` creates a trip owned by the authenticated user (fields: name, destination, startDate, endDate, description — description optional); validation rejects missing name/destination/dates and `endDate` before `startDate`.
- `GET /api/trips` returns only the authenticated user's trips.
- `GET /api/trips/{id}` returns the trip if owned by the authenticated user, 404 otherwise (including when the id belongs to another user).
- `PUT /api/trips/{id}` updates a trip's fields if owned by the authenticated user, 404 otherwise.
- `DELETE /api/trips/{id}` deletes a trip if owned by the authenticated user, 404 otherwise.
- All endpoints require a valid JWT (401 without one), and are documented in Swagger UI.
- JUnit/Mockito tests cover: create, list scoped to owner, get own vs. other's trip (404), update own vs. other's trip, delete own vs. other's trip, validation failure (bad dates / missing fields).

## Verification

- `./mvnw test` passes.
- Manual check via Swagger UI or a REST client for create → list → get → update → delete flow, and a cross-user 404 check with two different registered users.

## Exit

Write a handoff with criterion-level evidence; do not self-accept. Independent review runs before the task is marked complete. On completion, dispatch TASK-004 (now unblocked) and write the Módulo 2 learning note in the study vault per the active learning profile, if the vault destination is reachable this session — otherwise note it as deferred.
