---
schema: harness.task/v1
id: TASK-008
graph: graph-main@11
revision: 1
status: completed
assigned_to: agent:claude-code
reviewer: agent:code-review-fork
workstream: frontend-trip
agent_role: role:frontend-specialist
execution_context: shared
thread_policy: serialize-in-session
thread_ref: this-session
ownership_lease: lease:TASK-008
isolation: generic:exclusive-directory:frontend-trip-date
updated_at: 2026-08-25T01:35:00Z
capability_manifest: none
rules_map: none
model_tier: balanced
model_reason: Bounded, well-specified bug fix with deterministic acceptance (unit tests + live browser repro); no frontier-level ambiguity.
execution_budget: none
review_profile: standard
max_review_rounds: 2
assurance_gate: none
---

# TASK-008 — Bug fix: typed trip dates silently misparsed (found via live manual testing)

## Outcome

Found via manual browser walkthrough (user asked this session to connect Claude in Chrome and test the already-running app end to end): typing a date directly into the trip form's "Data de início"/"Data de término" fields (e.g. `01/09/2026`) silently produced the wrong date (`09/01/2026`, i.e. parsed as US MM/DD/YYYY) with no error shown. Selecting via the calendar picker was unaffected. Fixed so typed dates parse as DD/MM/YYYY.

## Context to load

- Live repro performed via `mcp__claude-in-chrome__*` tools against the already-running local dev servers (`ng serve` on :4200, backend on :8080, both started by the user with `JWT_SECRET` set).
- `frontend/src/app/features/trips/trip-form/trip-form.component.ts` (existing `MatNativeDateModule` usage from TASK-004).
- `frontend/src/app/app.config.ts` (existing `MAT_DATE_LOCALE: 'pt-BR'` from TASK-005 — display formatting was already correct, only typed-input parsing was broken).

## Owned paths

- `frontend/src/app/core/date/br-date-adapter.ts` (new)
- `frontend/src/app/core/date/br-date-adapter.spec.ts` (new)
- `frontend/src/app/features/trips/trip-form/trip-form.component.ts` (swap `MatNativeDateModule` for an explicit `DateAdapter`/`MAT_DATE_FORMATS` provider)

## Constraints

- Fix only the parsing bug; do not touch itinerary date handling (`trip-detail`), which uses native `<input type="date">`, not `MatDatepicker` — unaffected by this bug.
- Keep display/format behavior identical (still native-date, still locale-aware via `Intl`).

## Rules to load

- None formalized yet beyond this brief.

## Required capabilities

- Local Angular CLI build/test (`npx ng build|test`), Chrome (headless, for Karma). Live browser interaction via `mcp__claude-in-chrome__*` — connected and used this session (a first for this project — prior tasks all had it unavailable).

## Acceptance criteria

- Typing `01/09/2026` into a trip date field and moving focus away keeps the field showing `01/09/2026` (not `09/01/2026`).
- The calendar-picker selection flow (already correct) keeps working.
- Invalid typed dates (e.g. `31/02/2026`) are rejected (field becomes invalid), not silently coerced to a nearby valid date.
- No regression to any other date-consuming flow (itinerary dates, trip list/detail display).

## Verification

- `npx ng test --watch=false --browsers=ChromeHeadless`.
- `npx ng build`.
- Live in-browser walkthrough: performed this session via `mcp__claude-in-chrome__*` — typed both trip dates, saved, confirmed correct values persisted and displayed.

## Exit

Write a handoff with criterion-level evidence; do not self-accept. Independent review runs before the task is marked complete.
