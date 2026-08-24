---
schema: harness.handoff/v1
id: HANDOFF-TASK-001-01
task: TASK-001@1
attempt: 1
status: completed
author: agent:claude-code
workstream: backend-auth
agent_role: role:backend-specialist
execution_context: shared
thread_ref: this-session
created_at: 2026-08-24T00:00:00Z
model_tier_used: balanced
model_route_changes: none
execution_budget: none
---

# Handoff — TASK-001

## Result

Backend register/login with Spring Security + JWT is implemented and passes its tests. A sample protected endpoint (`GET /api/users/me`) correctly returns 401 without a valid token and 200 with one.

- Execution context: this Claude Code session (no isolated/parallel context available); no adapter needed.

## Changes

- `backend/pom.xml`: added `jjwt-api`/`jjwt-impl`/`jjwt-jackson` 0.12.6.
- `backend/src/main/java/com/gustavo/tripplanner/user/{User,Role,UserRepository,UserController,UserResponse}.java`: user entity/repository + protected `/api/users/me`.
- `backend/src/main/java/com/gustavo/tripplanner/auth/{dto/*,AuthController,AuthService,JwtService,AppUserDetailsService,EmailAlreadyInUseException}.java`: register/login flow, JWT issuance/validation.
- `backend/src/main/java/com/gustavo/tripplanner/config/{SecurityConfig,JwtAuthenticationFilter,GlobalExceptionHandler}.java`: stateless JWT security chain (401 entry point), JWT filter, error mapping.
- `backend/src/main/resources/application.yml`, `backend/.env.example`: `JWT_SECRET`/`JWT_EXPIRATION_MINUTES`.
- `backend/src/test/java/.../auth/AuthServiceTest.java`, `.../user/UserControllerSecurityTest.java`: new tests.

## Change unit and authority

- Coherent unit: backend auth module (Módulo 1 backend half); frontend auth UI (TASK-002) is a separate unit that depends on this one.
- Split boundaries: backend vs. frontend, matching `write_set` ownership in the task graph.
- Commit/integration/push/deploy/publication authority: not exercised — no commit was made; changes remain in the working tree pending the user's own review/commit.

## Acceptance evidence

| Criterion | Result | Evidence |
| --- | --- | --- |
| `POST /api/auth/register` creates user, rejects duplicate email | pass | `AuthServiceTest.registerCreatesUserAndReturnsToken`, `.registerRejectsDuplicateEmail`, `.registerRejectsConcurrentDuplicateEmailRace` |
| `POST /api/auth/login` returns JWT on valid creds, 401 on invalid | pass | `AuthServiceTest.loginReturnsTokenOnValidCredentials`, `.loginRejectsInvalidCredentials`; `GlobalExceptionHandler` maps `AuthenticationException` → 401 |
| Protected endpoint rejects missing/invalid token (401), accepts valid one | pass | `UserControllerSecurityTest.rejectsRequestWithoutToken` (401), `.allowsRequestWithValidToken` |
| Endpoints appear in Swagger UI | not-run | Requires running app against local Postgres; springdoc auto-generates from `@RestController`s, not independently re-verified this session |
| JUnit/Mockito coverage for the above | pass | 8/8 tests in `AuthServiceTest` + `UserControllerSecurityTest` |

## Verification run

- Command: `./mvnw -Dtest=AuthServiceTest,UserControllerSecurityTest test` — 8/8 passed.
- Command: `./mvnw test-compile` (offline) — clean compile, main + test sources.
- Not run: `./mvnw test` (full suite, includes `TripplannerApplicationTests.contextLoads`) and a live Swagger check — both need the user's local Postgres reachable with `DB_USERNAME`/`DB_PASSWORD`, plus `JWT_SECRET`, set as environment variables; those secrets are intentionally not available to this session.
- Environment: local Maven (offline-capable, already-cached dependencies), Java 24 runtime, Spring Boot 4.1.1 / Spring Security 7.1.1 (confirmed via local `.m2` — note API packages moved from Boot 3.x defaults: `@WebMvcTest` is now `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`, and `@MockBean` is replaced by `org.springframework.test.context.bean.override.mockito.MockitoBean`).

## Execution budget

- Goal lineage: TASK-001, attempt 1.
- Usage: 1 implementation pass + 1 fix pass after independent review; 0 no-progress cycles; 0 context expansions.
- Decision: `continue` (task complete, no ceiling reached).
- Token/cost measurement: unavailable.

## Discoveries and risks

- Spring Boot 4.1.1 in this project is a real, already-released version with renamed test packages/annotations vs. the Boot 3.x patterns I initially assumed; corrected during implementation (see Verification run).
- `ddl-auto: update` will create the `users` table automatically on next app boot against the local Postgres `tripplanner` database — no manual migration needed for this stage, consistent with the project's existing convention.
- Email uniqueness now relies on application-level normalization (lowercase) plus the existing DB unique constraint; a case-insensitive DB index (e.g. Postgres `citext` or a functional index) would be a stronger guarantee once migrations (Flyway/Liquibase) are introduced later in the roadmap.

## Routing and authority

- Tier used and reason: balanced — bounded, deterministic CRUD/auth implementation with standard Spring Security patterns, no frontier-level ambiguity.
- Escalation/decomposition: none.
- Routing granted no additional permissions and removed no review or verification gate.

## Review request

- Independent review already run (forked `code-review` skill agent) before this handoff was written. It found 3 issues, all fixed in this same task (no second round needed):
  1. `JwtAuthenticationFilter` let `UsernameNotFoundException` escape the filter chain for a valid-but-stale token (deleted user) → would have 500'd instead of 401'd. Fixed: caught, falls through to unauthenticated → clean 401.
  2. `AuthService.register` had a TOCTOU race between `existsByEmail` and `save` → concurrent duplicate registrations could 500 instead of 409. Fixed: `DataIntegrityViolationException` from `save` is now mapped to `EmailAlreadyInUseException`.
  3. Email lookups/storage were case-sensitive → same address in different casing could register twice or fail to log in. Fixed: emails normalized (trim + lowercase) at the `AuthService` boundary for both register and login.
- Focus for any follow-up human review: the case-insensitivity fix is application-level only (see Discoveries and risks above); the concurrent-registration fix is now correct at the application layer.

## User-facing closeout

- Outcome: Backend of Módulo 1 (Autenticação & Usuários) is implemented, self-tested, and independently reviewed with all findings fixed.
- Stage: Módulo 1 in progress — backend done, frontend (TASK-002) next.
- Progress: TASK-001 completed; TASK-002 unblocked (was `blocked`, now `ready`).
- Material changes: see Changes above.
- Verification: `./mvnw -Dtest=AuthServiceTest,UserControllerSecurityTest test` → 8/8 passed; full `./mvnw test` and live Swagger check left for the user (needs local Postgres credentials this session doesn't have).
- Lifecycle state: completed.
- Blockers: None.
- Next action: dispatch TASK-002 (frontend auth UI), or the user may want to first run the full app locally to see the backend working end to end.
- Inspectable paths: `harness-state/TASK-001.md`, `harness-state/HANDOFF-TASK-001-01.md`, `harness-state/TASK-GRAPH.md`, `harness-state/PENDING.md`.
- Human action required: None to continue technically. Optional: run `./mvnw spring-boot:run` locally (with `DB_USERNAME`/`DB_PASSWORD`/`JWT_SECRET` set) and check `/swagger-ui.html` to see it live.
