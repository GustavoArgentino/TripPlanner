---
schema: harness.handoff/v1
id: HANDOFF-TASK-006-01
task: TASK-006@1
attempt: 1
status: completed
author: agent:claude-code
workstream: backend-itinerary
agent_role: role:backend-specialist
execution_context: shared
thread_ref: this-session
created_at: 2026-08-25T00:00:00Z
model_tier_used: balanced
model_route_changes: none
execution_budget: none
---

# Handoff — TASK-006

## Result

Backend itinerary CRUD (Módulo 3), nested under a trip, is implemented and passes its tests. An authenticated user can create, list, view, update, and delete itinerary items on their own trips; any other trip id (owned by another user, or nonexistent) resolves to 404 for every nested endpoint, same as the Trip module's convention.

- Execution context: this Claude Code session (no isolated/parallel context available); no adapter needed.

## Changes

- `backend/src/main/java/com/gustavo/tripplanner/itinerary/{ItineraryItem,ItineraryItemRepository,ItineraryItemService,ItineraryItemController,ItineraryItemNotFoundException,InvalidItineraryDateException}.java`: new entity/repository/service/controller nested under `/api/trips/{tripId}/itinerary-items`.
- `backend/src/main/java/com/gustavo/tripplanner/itinerary/dto/{ItineraryItemRequest,ItineraryItemResponse}.java`: request/response DTOs.
- `backend/src/main/java/com/gustavo/tripplanner/config/GlobalExceptionHandler.java`: added mappings for `ItineraryItemNotFoundException` → 404 and `InvalidItineraryDateException` → 400.
- `backend/src/test/java/com/gustavo/tripplanner/itinerary/{ItineraryItemServiceTest,ItineraryItemControllerSecurityTest}.java`: new tests.

## Change unit and authority

- Coherent unit: backend Itinerary module (Módulo 3 backend half), nested under and depending on the Trip module (TASK-003); frontend Itinerary UI (TASK-007) is a separate unit that depends on this one.
- Split boundaries: backend vs. frontend, matching `write_set` ownership.
- Commit/integration/push/deploy/publication authority: not exercised — no commit was made; changes remain in the working tree pending the user's own review/commit.

## Acceptance evidence

| Criterion | Result | Evidence |
| --- | --- | --- |
| `POST .../itinerary-items` creates on own trip; rejects missing title/date, out-of-range date, and another user's trip (404) | pass | `ItineraryItemServiceTest.createSavesItemOnOwnTrip`, `.createRejectsAnotherUsersTripWithNotFound`, `.createRejectsDateOutsideTripRange`; `ItineraryItemControllerSecurityTest.rejectsCreateWithMissingRequiredFields`, `.mapsInvalidItineraryDateToBadRequest` |
| `GET .../itinerary-items` returns only that trip's items, ordered by date/time; 404 if not own trip | pass | `ItineraryItemServiceTest.listReturnsOnlyTripsOwnItems`; `ItineraryItemControllerSecurityTest.mapsTripNotFoundToNotFound` |
| `GET .../itinerary-items/{id}` returns own item, 404 for another trip's item | pass | `ItineraryItemServiceTest.getReturnsOwnItem`, `.getRejectsItemFromAnotherUsersTripWithNotFound`; `ItineraryItemControllerSecurityTest.mapsItineraryItemNotFoundToNotFound` |
| `PUT .../itinerary-items/{id}` updates own item under the same rules, 404 otherwise | pass | `ItineraryItemServiceTest.updateModifiesOwnItem`, `.updateRejectsItemFromAnotherUsersTripWithNotFound` |
| `DELETE .../itinerary-items/{id}` deletes own item, 404 otherwise | pass | `ItineraryItemServiceTest.deleteRemovesOwnItem`, `.deleteRejectsItemFromAnotherUsersTripWithNotFound` |
| All endpoints require a valid JWT (401 without one) | pass | `ItineraryItemControllerSecurityTest.rejectsListWithoutToken`, `.rejectsCreateWithoutToken`, `.rejectsGetWithoutToken`, `.rejectsUpdateWithoutToken`, `.rejectsDeleteWithoutToken` |
| Endpoints appear in Swagger UI | not-run | Requires running app against local Postgres + `JWT_SECRET`; not independently re-verified this session |
| JUnit/Mockito coverage incl. validation failure (date out of range / missing fields) | pass | 19 new tests (10 `ItineraryItemServiceTest` + 9 `ItineraryItemControllerSecurityTest`) |

## Verification run

- Command: `./mvnw test -Dtest='!TripplannerApplicationTests'` (full backend suite except the known pre-existing context-load gap, with local `DB_USERNAME`/`DB_PASSWORD` sourced from `backend/.env`) — 44/44 passed, `BUILD SUCCESS`, both before and after review remediation.
- Not run: `TripplannerApplicationTests.contextLoads` — `JWT_SECRET` still not present in `backend/.env`; same pre-existing gap flagged in every prior backend handoff, not a regression from this task.
- Not run: live Swagger check and a real cross-trip walkthrough — both need the app actually running, which needs `JWT_SECRET`.
- Environment: local Maven (offline-capable, cached dependencies), Java 24 runtime, Spring Boot 4.1.1.

## Execution budget

- Goal lineage: TASK-006, attempt 1.
- Usage: 1 implementation pass + 1 fix pass after independent review; 0 no-progress cycles; 0 context expansions.
- Decision: `continue` (task complete, no ceiling reached).
- Token/cost measurement: unavailable.

- Itinerary items have no independent owner field — ownership is always resolved transitively through the parent `Trip`. Deleting a trip that has itinerary items would otherwise fail on the `trip_id` FK constraint (a real gap found while writing this handoff, not from the code review); fixed by adding `@OnDelete(action = OnDeleteAction.CASCADE)` on `ItineraryItem.trip`, so `ddl-auto: update` generates the FK with `ON DELETE CASCADE` — deleting a trip now cascades to its itinerary items at the DB level, without `TripService` needing to know this module exists. Not independently verified against a real Postgres instance this session (same `JWT_SECRET` gap); worth a quick manual check once the app can boot locally.
- 404-on-cross-user/cross-trip access (rather than 403) is intentional, matching the Trip module's non-leaking convention.
- The `ddl-auto: update` convention will create the `itinerary_items` table (with a `trip_id` FK to `trips`) automatically on next app boot.

## Routing and authority

- Tier used and reason: balanced — bounded, deterministic nested owner-scoped CRUD, no frontier-level ambiguity.
- Escalation/decomposition: none.
- Routing granted no additional permissions and removed no review or verification gate.

## Review request

- Independent review already run (forked `code-review` skill agent) before this handoff was written. It found 2 issues, both non-blocking (efficiency/duplication, no acceptance-criterion or correctness violation) per the bounded-review-rounds policy:
  1. **Fixed (cheap, in-scope)** — `update()` resolved trip ownership twice (once directly, once again inside `findOwnedItem`), doubling DB round trips per `PUT` request. Fixed: `update()` now calls `findOwnedItem` once and reads the trip off the returned item (`item.getTrip()`) for date validation, instead of a separate `findOwnedTrip` call.
  2. **Non-blocking follow-up, not fixed** — `ItineraryItemService.findOwnedTrip` duplicates `TripService`'s private ownership-resolution logic verbatim (same `findByEmail` + `findByIdAndOwnerId` + `TripNotFoundException` sequence). Extracting a shared resolver would mean touching `TripService`, outside this task's `write_set`; recorded as a follow-up candidate, same category as TASK-003's own accepted follow-up about `TripService.resolveOwner()`'s duplicate per-request lookup — both point at the same underlying opportunity (a shared trip/user-ownership resolution helper) once a third module needs the same pattern.
- Round 2 (focused remediation) re-verified: 44/44 backend tests pass. No new findings introduced by the fix.

## User-facing closeout

- Outcome: Backend of Módulo 3 (Itinerário) is implemented, self-tested, and independently reviewed with the in-scope finding fixed.
- Stage: Módulo 3 in progress — backend done, frontend (TASK-007) next.
- Progress: TASK-006 completed; TASK-007 unblocked (was `blocked`, now `ready`).
- Material changes: see Changes above.
- Verification: `./mvnw test -Dtest='!TripplannerApplicationTests'` → 44/44 passed; the app-context test and a live walkthrough are left for the user (needs `JWT_SECRET` set locally, same gap as every prior backend task).
- Lifecycle state: completed.
- Blockers: None.
- Follow-up candidates (non-blocking): a shared trip-ownership-resolution helper (TripService + ItineraryItemService both duplicate it).
- Next action: dispatch TASK-007 (frontend Itinerary UI).
- Inspectable paths: `harness-state/TASK-006.md`, `harness-state/HANDOFF-TASK-006-01.md`, `harness-state/TASK-GRAPH.md`, `harness-state/PENDING.md`.
- Human action required: None to continue technically. Optional: add `JWT_SECRET` to `backend/.env` and try deleting a trip that has itinerary items locally, to confirm the `ON DELETE CASCADE` fix behaves as expected.
