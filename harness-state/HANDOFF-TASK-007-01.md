---
schema: harness.handoff/v1
id: HANDOFF-TASK-007-01
task: TASK-007@2
attempt: 1
status: completed
author: agent:claude-code
workstream: frontend-itinerary
agent_role: role:frontend-specialist
execution_context: shared
thread_ref: this-session
created_at: 2026-08-25T00:00:00Z
model_tier_used: balanced
model_route_changes: none
execution_budget: none
---

# Handoff — TASK-007

## Result

Angular UI for Itinerary is implemented: from the trip list, clicking a trip's name opens a detail page showing the trip's info and its itinerary items, with inline add/edit/delete, wired against the TASK-006 backend contract.

- Execution context: this Claude Code session (no isolated/parallel context available); no adapter needed.

## Changes

- `frontend/src/app/core/itinerary/{itinerary.models,itinerary.service}.ts`: DTOs + `ItineraryService` (list/get/create/update/delete against `/api/trips/{tripId}/itinerary-items`).
- `frontend/src/app/features/trips/trip-detail/`: trip detail page — trip header, itinerary list, and an inline toggleable form shared between create and edit.
- `frontend/src/app/features/trips/trips.routes.ts`: added `:id` (detail), placed after `new` and `:id/edit` so those static/nested paths keep matching first.
- `frontend/src/app/features/trips/trip-list/trip-list.component.html`: trip card title is now a link into `/trips/:id`.
- `frontend/src/app/features/trips/trip-list/trip-list.component.scss`: styling for the new title link (`.trip-title-link`) — scope expansion, see below.
- `frontend/src/app/features/trips/trip-date.util.ts` (new): extracted `formatDate()` shared by `trip-list` and `trip-detail` — scope expansion made during review remediation, see below.
- `frontend/src/app/features/trips/trip-list/trip-list.component.ts`: now imports the shared `formatDate` instead of its own copy — same remediation.

## Change unit and authority

- Coherent unit: frontend Itinerary UI (Módulo 3 frontend half); depends on TASK-004 (trip list/detail entry point) and TASK-006 (backend contract).
- Split boundaries: backend vs. frontend, matching `write_set` ownership; the `trip-list.component.{html,scss,ts}` touches are documented here and in `TASK-GRAPH.md`'s node `write_set`, following the same expansion-with-documentation pattern as TASK-002/004/005.
- Commit/integration/push/deploy/publication authority: not exercised — no commit was made; changes remain in the working tree pending the user's own review/commit.

## Acceptance evidence

| Criterion | Result | Evidence |
| --- | --- | --- |
| Trip detail page shows trip info + itinerary items ordered by date/time, empty state | pass (unit-level for the service; component logic thin, same testing depth as `trip-list`/`trip-form` in TASK-004) | `itinerary.service.spec.ts` covers the HTTP layer; items arrive pre-ordered from the backend (TASK-006's `findAllByTripIdOrderByDateAscStartTimeAsc`) |
| Create form: required title/date, date within trip range, `POST .../itinerary-items` | pass | `itinerary.service.spec.ts` create() case; explicit range check in `submit()` (`date! < trip.startDate \|\| date! > trip.endDate`), added during review remediation — see Review request |
| Edit form: pre-filled, `PUT .../itinerary-items/{id}` | pass | `itinerary.service.spec.ts` update() case; `openEditForm()` pre-fills from the clicked item |
| Delete with confirmation, `DELETE .../itinerary-items/{id}` | pass | `itinerary.service.spec.ts` delete() case; native `confirm()`, same pattern as `trip-list` |
| API errors (401/404/400) surface a user-visible message | pass | `HttpErrorResponse` status branching in `trip-detail.component.ts`, same pattern as `trip-form.component.ts` |
| Trip list links each trip into its detail page, edit/delete actions untouched | pass | `trip-list.component.html` — trip title wrapped in `[routerLink]="['/trips', trip.id]"`; the existing "Editar"/"Excluir" buttons are unchanged |

## Verification run

- Command: `npx ng test --watch=false --browsers=ChromeHeadless` — 29/29 passed (24 pre-existing + 5 new in `itinerary.service.spec.ts`), both before and after review remediation.
- Command: `npx ng build` — clean production build after remediation (one compile error surfaced and was fixed mid-implementation — see Discoveries and risks); `trip-detail-component` confirmed as its own lazy chunk (12.14 kB / 3.52 kB transfer).
- Not run: live in-browser walkthrough and a real end-to-end run against the live backend — `JWT_SECRET` still not present in `backend/.env`, same limitation as every prior frontend handoff.
- Environment: Angular CLI 19.2.27, Karma 6.4.4 + Chrome Headless 151, same local machine as prior tasks.

## Execution budget

- Goal lineage: TASK-007, attempt 1.
- Usage: 1 implementation pass (incl. one compile-error fix, not counted as a separate attempt since it was caught and fixed before verification completed) + 1 fix pass after independent review; 0 no-progress cycles; 0 context expansions.
- Decision: `continue` (task complete, no ceiling reached).
- Token/cost measurement: unavailable.

## Discoveries and risks

- Mid-implementation, `@else if (trip(); as t)` failed to compile (`NG9: Property 't' does not exist on type 'TripDetailComponent'`) for every usage of the aliased variable, including plain interpolations, not just property bindings. Root cause not fully diagnosed (may be a real limitation/bug of this Angular version's `as`-binding support specifically inside `@else if`, as opposed to a plain `@if`); worked around by dropping the alias and calling `trip()!` directly everywhere it's used instead. Worth knowing about if a future component reaches for the same `@else if (x(); as y)` pattern — plain `@if (x(); as y)` wasn't tested here and may or may not have the same issue.
- The itinerary create/edit form is a single inline toggle within the trip detail page (no separate routes), unlike Trip's create/edit which get their own routes (`/trips/new`, `/trips/:id/edit`). This was a deliberate scope choice for a lighter-weight, more numerous sub-resource — flagging in case the user expected route-based itinerary forms instead.
- Native HTML `<input type="date">`/`type="time">` are used (via `matInput`) instead of `MatDatepicker`, avoiding pulling in `MatDatepickerModule`/`MatNativeDateModule` for this simpler, higher-cardinality form. Dates stay plain `YYYY-MM-DD` strings end to end, same convention as Trip (TASK-004).

## Routing and authority

- Tier used and reason: balanced — bounded, deterministic Angular CRUD UI, standard Material/reactive-forms patterns, no frontier-level ambiguity.
- Escalation/decomposition: none.
- Routing granted no additional permissions and removed no review or verification gate.

## Review request

- Independent review already run (forked `code-review` skill agent) before this handoff was written. It found 5 issues, all fixed in this same task:
  1. **Correctness bug** — the same class of bug TASK-004 fixed in `TripFormComponent`, now found here too: navigating directly from one trip's detail page to another's (component instance reused across sibling `:id` activations) left the itinerary edit form open, pre-filled with the previous trip's item, and a save would silently 404 against the wrong trip. Fixed: the `paramMap` subscription now resets `trip`, `items`, and the open form (`closeForm()`) at the start of every emission, before loading the new trip's data.
  2. **Correctness bug** — `loadTrip()`'s error handler never cleared the `trip` signal, so a failed load for a newly-navigated (e.g. deleted, or another user's) trip kept rendering the previous trip's full content under an error banner. Fixed: covered by the same reset in fix 1, plus `trip.set(null)`/`items.set([])` added directly in the respective error handlers for defensiveness if either loader is ever called independently of that reset.
  3. **Correctness / acceptance-criterion violation** — the `date` field's HTML `min`/`max` attributes only constrain the native date-picker widget, not Angular's reactive-forms validity; nothing actually enforced "date within the trip's own date range" as the acceptance criteria require, so an out-of-range date (e.g. typed directly) would reach the API instead of being caught client-side. Fixed: added an explicit range check in `submit()` (string comparison on the `YYYY-MM-DD` values, same approach `TripFormComponent` uses for its start/end-date order check), surfaced via the existing `formError()` banner; corrected the paired `mat-error` copy, which had claimed to cover this but was tied to the (required-only) field validator.
  4. **Governance: undeclared write_set** — `trip-list.component.scss` was edited (added `.trip-title-link`) without being listed in TASK-007's `Owned paths`. Fixed: added to the task brief's owned paths, documented here and in `TASK-GRAPH.md`, same expansion-with-documentation pattern used by TASK-002/004/005 rather than reverting a change that was genuinely needed (an unstyled link would look broken).
  5. **Reuse/simplification** — trip and itinerary-item dates in the detail page were raw ISO strings (`2026-09-03`) instead of the `dd/mm/yyyy` format `trip-list` already uses. Fixed by extracting `trip-list.component.ts`'s private `formatDate()` into a shared `frontend/src/app/features/trips/trip-date.util.ts`, used by both components — avoids the exact kind of duplication already called out as a follow-up elsewhere in this project (e.g. `.trip-grid`/`.feature-grid` in TASK-005's handoff).
- Round 2 (focused remediation) re-verified: 29/29 frontend tests pass, `ng build` clean. No new findings introduced by the fixes.

## User-facing closeout

- Outcome: Frontend of Módulo 3 (Itinerário) is implemented, self-tested, and independently reviewed with all findings fixed. Módulo 3 (backend + frontend) is now complete.
- Stage: Módulo 3 complete. Módulo 4 (Integração Clima) not yet decomposed.
- Progress: TASK-007 completed.
- Material changes: see Changes above.
- Verification: `npx ng test --watch=false --browsers=ChromeHeadless` → 29/29 passed; `npx ng build` → clean. Live browser walkthrough and real backend integration left for the user (needs `JWT_SECRET` set locally, same gap as every prior backend-dependent task).
- Lifecycle state: completed.
- Blockers: None.
- Next action: user reviews/commits/pushes this work; then decide whether to decompose Módulo 4 (Integração Clima) or first manually verify Módulo 3 end to end.
- Inspectable paths: `harness-state/TASK-006.md`, `harness-state/HANDOFF-TASK-006-01.md`, `harness-state/TASK-007.md`, `harness-state/HANDOFF-TASK-007-01.md`, `harness-state/TASK-GRAPH.md`, `harness-state/PENDING.md`.
- Human action required: add `JWT_SECRET` to `backend/.env`, run the backend and frontend locally, and click through a trip's itinerary (add/edit/delete an item, including one dated outside the trip's range to confirm the validation message) at least once, since it wasn't visually verified this session.
