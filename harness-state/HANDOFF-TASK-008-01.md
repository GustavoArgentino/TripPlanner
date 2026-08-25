---
schema: harness.handoff/v1
id: HANDOFF-TASK-008-01
task: TASK-008@1
attempt: 1
status: completed
author: agent:claude-code
workstream: frontend-trip
agent_role: role:frontend-specialist
execution_context: shared
thread_ref: this-session
created_at: 2026-08-25T01:35:00Z
model_tier_used: balanced
model_route_changes: none
execution_budget: none
---

# Handoff — TASK-008

## Result

Trip date fields no longer silently swap day/month when typed by hand. Root cause: Angular Material's default `NativeDateAdapter.parse()` delegates to `Date.parse()` for slash-separated strings, which the JS runtime reads as US `MM/DD/YYYY` regardless of `MAT_DATE_LOCALE` — display formatting was already locale-correct (fixed in TASK-005), but parsing typed input wasn't. A custom `BrDateAdapter` now parses typed input as `DD/MM/YYYY` explicitly.

- Execution context: this Claude Code session, with live browser access via `mcp__claude-in-chrome__*` against the user's already-running local dev servers (frontend :4200, backend :8080, `JWT_SECRET` set by the user) — the first task in this project verified live in-browser rather than by code/tests alone.

## Changes

- `frontend/src/app/core/date/br-date-adapter.ts` (new): `BrDateAdapter extends NativeDateAdapter`, overrides `parse()` to read `DD/MM/YYYY` strings explicitly via regex + `setFullYear`/`setHours` (not the `Date` constructor — see Review request), rejecting calendar-invalid dates (e.g. `31/02`) and sub-100 years being misread as 19xx.
- `frontend/src/app/core/date/br-date-adapter.spec.ts` (new): unit tests for the parsing behavior.
- `frontend/src/app/features/trips/trip-form/trip-form.component.ts`: removed `MatNativeDateModule` from `imports`, added explicit `providers: [{ provide: DateAdapter, useClass: BrDateAdapter }, { provide: MAT_DATE_FORMATS, useValue: MAT_NATIVE_DATE_FORMATS }]` — component-scoped, since `MatNativeDateModule`'s own `DateAdapter` provider (imported into a standalone component) would otherwise sit at the same or closer injector level and could win over a root-level override.

## Change unit and authority

- Coherent unit: a targeted bug fix to the Trip module's date handling (TASK-004's `trip-form`), found via manual testing rather than through the normal task-graph module sequence.
- Split boundaries: frontend-only; does not touch the Itinerary module's native `<input type="date">` fields (unaffected — different input mechanism, no `MatDatepicker`/`DateAdapter` involved there).
- Commit/integration/push/deploy/publication authority: not exercised — no commit was made; changes remain in the working tree pending the user's own review/commit.

## Acceptance evidence

| Criterion | Result | Evidence |
| --- | --- | --- |
| Typing `01/09/2026` keeps the field showing `01/09/2026`, not `09/01/2026` | pass | Live browser repro: before the fix, typing `01/09/2026`/`10/09/2026` and blurring showed `09/01/2026`/`09/10/2026`; after the fix, typing the same values and saving created a trip showing `01/09/2026 — 10/09/2026` in both the detail page and the trip list card. `br-date-adapter.spec.ts` unit tests cover the same case. |
| Calendar-picker selection still works | pass | Live repro: used the calendar UI (confirmed Portuguese month names from TASK-005) to select Sep 1/Sep 10 before the fix was applied, confirming the picker path was never broken; unaffected by this change since picker selection doesn't go through `parse()`. |
| Invalid typed dates (e.g. `31/02/2026`) rejected, not coerced | pass | `br-date-adapter.spec.ts`: `returns null for an invalid calendar date (31 of February)` |
| No regression to itinerary or other date displays | pass | Live repro: created an itinerary item via the native date input (unaffected input type), confirmed the trip-detail/trip-list `formatDate()` display (from TASK-007) still showed `03/09/2026` correctly; `npx ng test` 35/35 (no existing spec touched this code path negatively). |

## Verification run

- Command: `npx ng test --watch=false --browsers=ChromeHeadless` — 35/35 passed (29 pre-existing + 6 new: 5 initial `br-date-adapter.spec.ts` cases + 1 regression test added after review remediation).
- Command: `npx ng build` — clean production build, both before and after the review-remediation fix.
- Live in-browser walkthrough (via `mcp__claude-in-chrome__*`, connected this session): registered a test user, logged in, created a trip by **typing** both dates (reproduced the bug pre-fix, confirmed the fix post-fix), opened the trip detail page, added/edited an itinerary item, submitted an out-of-range itinerary date (confirmed the TASK-007 client-side validation blocks it), deleted the trip while it still had an itinerary item (confirmed the TASK-006 `ON DELETE CASCADE` fix works against a real Postgres instance), and logged out (confirmed the TASK-005 global-toolbar logout works from `/trips`). No console errors observed. Test data cleaned up (trip deleted) before finishing.
- Environment: Angular CLI 19.2.27, Karma 6.4.4 + Chrome Headless 151 for unit tests; Chrome (via the Claude in Chrome extension, connected this session) against `http://localhost:4200` + `http://localhost:8080` for the live walkthrough.

## Execution budget

- Goal lineage: TASK-008, attempt 1.
- Usage: 1 implementation pass (incl. live-browser verification) + 1 fix pass after independent review; 0 no-progress cycles; 0 context expansions.
- Decision: `continue` (task complete, no ceiling reached).
- Token/cost measurement: unavailable.

## Discoveries and risks

- This is the first task in the project verified against a real, live, running instance (frontend + backend + Postgres) rather than unit tests and code review alone — it's exactly how this bug was caught, since no prior code review could have found a runtime-only JS `Date` parsing quirk. Worth keeping in mind: prior handoffs' "not run: live walkthrough" caveats were a real gap, not just a formality.
- The bug was isolated to `trip-form` (the only place `MatDatepicker`/`MatNativeDateModule` is used in this app). If a future module adds another `MatDatepicker` (e.g. a date-range filter), it needs the same `BrDateAdapter` provider — it's in `core/date/` specifically so it's reusable, not duplicated per-component.
- `MAT_DATE_LOCALE: 'pt-BR'` (from `app.config.ts`, TASK-005) still governs display formatting/calendar labels via `Intl`, inherited unchanged from `NativeDateAdapter`. Only `parse()` needed overriding.

## Routing and authority

- Tier used and reason: balanced — bounded, well-specified bug fix with a clear, testable root cause; no frontier-level ambiguity.
- Escalation/decomposition: none.
- Routing granted no additional permissions and removed no review or verification gate. Live browser access was newly available this session (user connected the Claude in Chrome extension) — used for verification, not to skip the independent code review.

## Review request

- Independent review already run (forked `code-review` skill agent) before this handoff was written. It found 1 issue, fixed in this same task:
  1. `BrDateAdapter.parse()` used `new Date(year, month - 1, day)` directly, which has the well-known JS `Date` constructor pitfall of converting two-digit years (0-99) to `19xx`. For a year like `0099`, the adapter's own overflow-rejection check (`date.getFullYear() === year`) would then fail (`1999 !== 99`) and incorrectly reject a syntactically valid date as invalid. Low real-world severity (nobody types a trip date in the year 0-99), but a genuine, demonstrable bug — the reviewer confirmed `NativeDateAdapter`'s own internal `_createDateWithOverflow` helper (in `node_modules/@angular/material/fesm2022/core.mjs`) exists specifically to avoid this exact pitfall via `setFullYear`/`setHours` instead of the constructor. Fixed by using the same `setFullYear`/`setHours` idiom; added a regression test (`parses a sub-100 year without the Date constructor rolling it into 19xx`).
  - The reviewer separately confirmed (not a finding, just verification) that the component-level provider override correctly takes precedence over `MatNativeDateModule` (which is no longer imported anywhere in the app), and checked single/double-digit day-month parsing, invalid-date rejection, whitespace handling, and `Date`-instance passthrough — all correct.
- Round 2 (focused remediation) re-verified: 35/35 frontend tests pass, `ng build` clean. No new findings introduced by the fix.

## User-facing closeout

- Outcome: The typed-date parsing bug is fixed, self-tested, live-verified in the browser, and independently reviewed with the one finding fixed.
- Stage: Ready to resume module progression — Módulo 4 (Integração Clima) still not decomposed.
- Progress: TASK-008 completed.
- Material changes: see Changes above.
- Verification: `npx ng test` → 35/35 passed; `npx ng build` → clean; live browser walkthrough performed and passed (see Verification run for the full path exercised).
- Lifecycle state: completed.
- Blockers: None.
- Next action: user reviews/commits/pushes this work (along with the still-uncommitted TASK-006/TASK-007 from before this fix); then decide whether to decompose Módulo 4.
- Inspectable paths: `harness-state/TASK-008.md`, `harness-state/HANDOFF-TASK-008-01.md`, `harness-state/TASK-GRAPH.md`, `harness-state/PENDING.md`.
- Human action required: None to continue technically. The live walkthrough already covered what H-006 (Módulo 3 manual verification) was asking for — see `PENDING.md` update.
