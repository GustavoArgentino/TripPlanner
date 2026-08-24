---
schema: harness.task/v1
id: TASK-001
graph: graph-main@1
revision: 1
status: active
assigned_to: agent:claude-code
reviewer: agent:fresh-reviewer
workstream: backend-auth
agent_role: role:backend-specialist
execution_context: shared
thread_policy: serialize-in-session
thread_ref: this-session
ownership_lease: lease:TASK-001
isolation: generic:exclusive-directory:backend-auth
updated_at: 2026-08-24T00:00:00Z
capability_manifest: none
rules_map: none
model_tier: balanced
model_reason: Bounded, well-specified CRUD/auth implementation with deterministic acceptance (tests pass); no frontier-level ambiguity.
execution_budget: none
review_profile: standard
max_review_rounds: 2
assurance_gate: none
---

# TASK-001 — Módulo 1 (backend): Autenticação & Usuários

## Outcome

A user can register and log in against the Spring Boot backend, receiving a JWT on success; protected endpoints require a valid JWT.

## Context to load

- `project-context@2` (`harness-state/PROJECT-CONTEXT.md`)
- `graph-main@1` (`harness-state/TASK-GRAPH.md`)
- Study-vault module 0 note for established conventions (`Obsidian-Estudos/Estudos/TripPlanner/00 - Setup.md`)

## Owned paths

- `backend/src/main/java/com/gustavo/tripplanner/**`
- `backend/src/test/java/com/gustavo/tripplanner/**`
- `backend/src/main/resources/application.yml`
- `backend/pom.xml`

## Constraints

- No real credentials/secrets committed; JWT signing secret via environment variable, same pattern as `DB_PASSWORD`.
- Passwords hashed with BCrypt, never stored/logged in plain text.
- `ddl-auto: update` acceptable for this dev-only stage (already the project's convention).
- Single-user trips only — no multi-user/collaborative modeling in the User entity beyond what auth needs.
- Do not change graph state or broaden the write set; do not start TASK-002.

## Rules to load

- None formalized yet beyond this brief and the project context's Constraints section.

## Required capabilities

- Local Maven build/test (`./mvnw`), local Postgres reachable at `localhost:5432` with `DB_USERNAME`/`DB_PASSWORD` set, database `tripplanner` existing. No network/secrets beyond that.

## Acceptance criteria

- `POST /api/auth/register` creates a user with hashed password; duplicate email is rejected with a clear error.
- `POST /api/auth/login` returns a JWT on valid credentials, 401 on invalid credentials.
- A sample protected endpoint rejects requests without a valid JWT (401) and accepts them with one.
- New endpoints appear in Swagger UI (`/swagger-ui.html`).
- JUnit/Mockito tests cover: successful register, duplicate-email register, successful login, wrong-password login, access denied without token.

## Verification

- `./mvnw test` passes.
- Manual check via Swagger UI or a REST client for register → login → access protected endpoint flow.

## Exit

Write a handoff with criterion-level evidence; do not self-accept. Independent review runs before the task is marked complete. On completion, write the Módulo 1 learning note in the study vault per the active learning profile.
