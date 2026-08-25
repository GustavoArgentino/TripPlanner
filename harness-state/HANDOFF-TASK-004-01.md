---
schema: harness.handoff/v1
id: HANDOFF-TASK-004-01
task: TASK-004@2
attempt: 1
status: completed
author: agent:claude-code
workstream: frontend-trip
agent_role: role:frontend-specialist
execution_context: shared
thread_ref: this-session
created_at: 2026-08-24T00:00:00Z
model_tier_used: balanced
model_route_changes: none
execution_budget: none
---

# Handoff — TASK-004

## Result

Angular UI for Trip CRUD is implemented: a trip list (create/edit/delete entry points), a shared create/edit form with client-side validation mirroring the backend, wired against the TASK-003 backend contract.

- Execution context: this Claude Code session (no isolated/parallel context available); no adapter needed.

## Changes

- `frontend/src/app/core/trips/{trip.models,trip.service}.ts`: DTOs + `TripService` (list/get/create/update/delete against `/api/trips`).
- `frontend/src/app/features/trips/trip-list/`: trip list, empty state, delete with a native `confirm()`.
- `frontend/src/app/features/trips/trip-form/`: shared create/edit form (route param decides mode), Material datepicker, client-side date-order validation.
- `frontend/src/app/features/trips/_trip-shared.scss`: shared `.error-banner` styling reusing the app's Material 3 tokens, mirroring `features/auth/_auth-form.scss` (avoids duplicating the pattern across list/form, same lesson as TASK-002's review finding).
- `frontend/src/app/features/trips/trips.routes.ts`: lazy routes for `''`, `'new'`, `':id/edit'`.
- `frontend/src/app/app.routes.ts`: added `/trips` (guarded, lazy-loaded) — additive only.
- `frontend/src/app/features/home/{home.component.ts,home.component.html}`: added a "Minhas viagens" nav button — scope addition beyond the original `write_set`, same pattern as TASK-002's `/home` placeholder addition; without it there was no in-app way to reach `/trips`.
- `frontend/src/app/app.config.ts`: added `{ provide: MAT_DATE_LOCALE, useValue: 'pt-BR' }` — scope addition made during review remediation (see Review request); needed because it's an app-wide provider, not something that can be scoped to the trip feature alone.

## Change unit and authority

- Coherent unit: frontend Trip UI (Módulo 2 frontend half); depends on TASK-002 (auth shell/guard) and TASK-003 (backend contract).
- Split boundaries: backend vs. frontend, matching `write_set` ownership; the two small `home.component.*`/`app.config.ts` additions are documented here and in `TASK-GRAPH.md`'s node `write_set`, same as the precedent set by TASK-002.
- Commit/integration/push/deploy/publication authority: not exercised — no commit was made; changes remain in the working tree pending the user's own review/commit, per the standing instruction from TASK-002.

## Acceptance evidence

| Criterion | Result | Evidence |
| --- | --- | --- |
| Trip list: shows own trips, empty state | pass (unit-level for the service; component not exercised by a live browser this session) | `trip.service.spec.ts` covers the HTTP layer; `TripListComponent` logic is thin (signals + service calls), same testing depth as `HomeComponent`/`LoginComponent` in TASK-002 |
| Create form: required fields, end date not before start date, `POST /api/trips` | pass (unit-level) | `trip.service.spec.ts` create() case; date-order check is a plain method (`startDate! > endDate!`), mirrors backend's `endDate.isBefore(startDate)` |
| Edit form: pre-filled, `PUT /api/trips/{id}` | pass (unit-level) | `trip.service.spec.ts` update() case; pre-fill logic (`loadTrip`) verified by build/type-check, not a dedicated spec (consistent with TASK-002's depth for component-level logic) |
| Delete with confirmation, `DELETE /api/trips/{id}` | pass (unit-level) | `trip.service.spec.ts` delete() case; confirmation uses native `confirm()` |
| API errors (401/404/400) surface a user-visible message | pass | `HttpErrorResponse` status branching in `trip-form.component.ts`/`trip-list.component.ts`, same pattern as `login.component.ts`; 401 is handled globally (guard + interceptor already tested in TASK-002) |

## Verification run

- Command: `npx ng test --watch=false --browsers=ChromeHeadless` — 24/24 passed (19 pre-existing + 5 new in `trip.service.spec.ts`), both before and after review remediation.
- Command: `npx ng build` — clean production build both before and after remediation; `trip-list-component`, `trip-form-component`, `trips-routes` all confirmed as separate lazy chunks.
- Not run: live in-browser walkthrough and a real end-to-end run against the live backend — `JWT_SECRET` is not present in `backend/.env` (only `DB_USERNAME`/`DB_PASSWORD` are), so the backend can't boot this session; same limitation flagged in `HANDOFF-TASK-003-01.md`, not new to this task.
- Environment: Angular CLI 19.2.27, Karma 6.4.4 + Chrome Headless 151, same local machine as TASK-001/002/003.

## Execution budget

- Goal lineage: TASK-004, attempt 1.
- Usage: 1 implementation pass + 1 fix pass after independent review; 0 no-progress cycles; 0 context expansions.
- Decision: `continue` (task complete, no ceiling reached).
- Token/cost measurement: unavailable.

## Discoveries and risks

- Trip dates are handled as plain `YYYY-MM-DD` strings end to end (no time/timezone component), converted to/from `Date` only for the Material datepicker via local-timezone getters (`getFullYear`/`getMonth`/`getDate`), deliberately avoiding `toISOString()` (which shifts by the local UTC offset and can roll the date). Worth keeping this convention if other modules add date fields later.
- The delete confirmation is a native `confirm()`, not a Material dialog — simplest option for a bounded CRUD UI; revisit if the app later gets a design-system-consistent modal pattern.
- Still no full `environments`/`fileReplacements` setup for `API_BASE_URL` (same gap noted in `HANDOFF-TASK-002-01.md`) — will need it before any real deployment.

## Routing and authority

- Tier used and reason: balanced — bounded, deterministic Angular CRUD UI, standard Material/reactive-forms patterns, no frontier-level ambiguity.
- Escalation/decomposition: none.
- Routing granted no additional permissions and removed no review or verification gate.

## Review request

- Independent review already run (forked `code-review` skill agent) before this handoff was written. It found 3 issues, all fixed in this same task:
  1. `TripFormComponent.ngOnInit` read the route id via `route.snapshot.paramMap` instead of subscribing to `route.paramMap`. Angular reuses the same component instance across two `':id/edit'` activations (same `routeConfig`), so a direct edit-A → edit-B navigation would not re-fire `ngOnInit`, leaving the form showing (or silently saving over) trip A's data while the URL says trip B. **Correctness bug — fixed:** now subscribes to `route.paramMap` and reloads on every emission; the subscription completes automatically when the component is destroyed (standard Angular router pattern, no manual `takeUntilDestroyed` needed since it's tied to route lifecycle, not an arbitrary long-lived stream).
  2. No `MAT_DATE_LOCALE` provider was configured anywhere in the app, so the newly-added `MatDatepicker` would render in English (month/day names, US ordering) while every other string in the app is Portuguese. Fixed: added `{ provide: MAT_DATE_LOCALE, useValue: 'pt-BR' }` to `app.config.ts` (an app-wide provider, so it couldn't be scoped to the trip feature files alone — noted as a `write_set` addition above).
  3. Trip dates in the list view were rendered as raw ISO strings (e.g. `2026-09-01`), inconsistent with the rest of the Portuguese-language UI. Fixed: added a small `formatDate()` method in `TripListComponent` that reformats to `DD/MM/YYYY` without going through Angular's locale-data-dependent `DatePipe` (unnecessary weight for a plain string reformat).
- Round 2 (focused remediation) re-verified: 24/24 frontend tests pass, `ng build` clean. No new findings introduced by the fixes.

## User-facing closeout

- Outcome: Frontend of Módulo 2 (Viagens/Trip) is implemented, self-tested, and independently reviewed with all findings fixed. Módulo 2 (backend + frontend) is now complete.
- Stage: Módulo 2 complete. Módulo 3 (Itinerário) not yet decomposed.
- Progress: TASK-004 completed.
- Material changes: see Changes above.
- Verification: `npx ng test --watch=false --browsers=ChromeHeadless` → 24/24 passed; `npx ng build` → clean. Live browser walkthrough and real backend integration left for the user (needs `JWT_SECRET` set locally, same gap as Módulos 1-2 backend).
- Lifecycle state: completed.
- Blockers: None.
- Next action: user reviews/commits/pushes this work; then decide whether to decompose Módulo 3 (Itinerário) or first manually verify Módulo 2 end to end.
- Inspectable paths: `harness-state/TASK-004.md`, `harness-state/HANDOFF-TASK-004-01.md`, `harness-state/TASK-GRAPH.md`, `harness-state/PENDING.md`.
- Human action required: add `JWT_SECRET` to `backend/.env`, run the backend (`./mvnw spring-boot:run`) and frontend (`ng serve`) locally, and click through create → edit → delete a trip at least once, since it wasn't visually verified this session.
