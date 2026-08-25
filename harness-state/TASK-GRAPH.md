---
schema: harness.task-graph/v1
id: graph-main
revision: 11
status: active
project_context: project-context@2
updated_at: 2026-08-25T01:35:00Z
updated_by: role:orchestrator
discovery_snapshot: discovery-001
source_references: none
---

# Task graph

The JSON block is the executable graph view. `write_set` contains repository-relative paths or directory globs ending in `/**`.
This artifact owns technical order, dependencies, readiness, leases, remediation, and execution. Human decisions/actions and the macro view of unfinished project areas belong in `harness-state/PENDING.md`, not here.

```json
{
  "nodes": [
    {
      "id": "TASK-001",
      "goal": "Módulo 1 (backend): register/login with Spring Security + JWT",
      "depends_on": [],
      "status": "completed",
      "assignee": "agent:claude-code",
      "reviewer": "agent:code-review-fork",
      "workstream": "backend-auth",
      "agent_role": "role:backend-specialist",
      "execution_context": "shared",
      "thread_policy": "serialize-in-session",
      "thread_ref": "this-session",
      "write_set": ["backend/src/main/java/com/gustavo/tripplanner/**", "backend/src/test/java/com/gustavo/tripplanner/**", "backend/src/main/resources/application.yml", "backend/pom.xml"],
      "checkpoint": null,
      "assurance_status": "passed",
      "assurance_requires": [],
      "task_brief": "harness-state/TASK-001.md"
    },
    {
      "id": "TASK-002",
      "goal": "Módulo 1 (frontend): login/register UI, auth service, JWT interceptor, route guard",
      "depends_on": ["TASK-001"],
      "status": "completed",
      "assignee": "agent:claude-code",
      "reviewer": "agent:code-review-fork",
      "workstream": "frontend-auth",
      "agent_role": "role:frontend-specialist",
      "execution_context": "shared",
      "thread_policy": "serialize-in-session",
      "thread_ref": "this-session",
      "write_set": ["frontend/src/app/features/auth/**", "frontend/src/app/core/**", "frontend/src/app/features/home/**", "frontend/src/app/app.routes.ts", "frontend/src/app/app.config.ts", "frontend/package.json"],
      "checkpoint": null,
      "assurance_status": "passed",
      "assurance_requires": [],
      "task_brief": "harness-state/TASK-002.md"
    },
    {
      "id": "TASK-003",
      "goal": "Módulo 2 (backend): Trip CRUD scoped to the authenticated owner",
      "depends_on": ["TASK-001"],
      "status": "completed",
      "assignee": "agent:claude-code",
      "reviewer": "agent:code-review-fork",
      "workstream": "backend-trip",
      "agent_role": "role:backend-specialist",
      "execution_context": "shared",
      "thread_policy": "serialize-in-session",
      "thread_ref": "this-session",
      "write_set": ["backend/src/main/java/com/gustavo/tripplanner/trip/**", "backend/src/test/java/com/gustavo/tripplanner/trip/**"],
      "checkpoint": null,
      "assurance_status": "passed",
      "assurance_requires": [],
      "task_brief": "harness-state/TASK-003.md"
    },
    {
      "id": "TASK-004",
      "goal": "Módulo 2 (frontend): Trip list/create/edit/delete UI",
      "depends_on": ["TASK-002", "TASK-003"],
      "status": "completed",
      "assignee": "agent:claude-code",
      "reviewer": "agent:code-review-fork",
      "workstream": "frontend-trip",
      "agent_role": "role:frontend-specialist",
      "execution_context": "shared",
      "thread_policy": "serialize-in-session",
      "thread_ref": "this-session",
      "write_set": ["frontend/src/app/features/trips/**", "frontend/src/app/core/trips/**", "frontend/src/app/app.routes.ts", "frontend/src/app/features/home/home.component.ts", "frontend/src/app/features/home/home.component.html", "frontend/src/app/app.config.ts"],
      "checkpoint": null,
      "assurance_status": "passed",
      "assurance_requires": [],
      "task_brief": "harness-state/TASK-004.md"
    },
    {
      "id": "TASK-005",
      "goal": "UX fix (user-directed, out of band): global logout/nav in the app shell + public landing page at '/'",
      "depends_on": ["TASK-002", "TASK-004"],
      "status": "completed",
      "assignee": "agent:claude-code",
      "reviewer": "agent:code-review-fork",
      "workstream": "frontend-shell",
      "agent_role": "role:frontend-specialist",
      "execution_context": "shared",
      "thread_policy": "serialize-in-session",
      "thread_ref": "this-session",
      "write_set": ["frontend/src/app/app.component.ts", "frontend/src/app/app.component.html", "frontend/src/app/app.component.scss", "frontend/src/app/app.component.spec.ts", "frontend/src/app/app.routes.ts", "frontend/src/app/features/home/home.component.ts", "frontend/src/app/features/home/home.component.html", "frontend/src/app/features/landing/**"],
      "checkpoint": null,
      "assurance_status": "passed",
      "assurance_requires": [],
      "task_brief": "harness-state/TASK-005.md"
    },
    {
      "id": "TASK-006",
      "goal": "Módulo 3 (backend): Itinerary items CRUD nested under a trip, scoped to the trip's owner",
      "depends_on": ["TASK-003"],
      "status": "completed",
      "assignee": "agent:claude-code",
      "reviewer": "agent:code-review-fork",
      "workstream": "backend-itinerary",
      "agent_role": "role:backend-specialist",
      "execution_context": "shared",
      "thread_policy": "serialize-in-session",
      "thread_ref": "this-session",
      "write_set": ["backend/src/main/java/com/gustavo/tripplanner/itinerary/**", "backend/src/test/java/com/gustavo/tripplanner/itinerary/**", "backend/src/main/java/com/gustavo/tripplanner/config/GlobalExceptionHandler.java"],
      "checkpoint": null,
      "assurance_status": "passed",
      "assurance_requires": [],
      "task_brief": "harness-state/TASK-006.md"
    },
    {
      "id": "TASK-007",
      "goal": "Módulo 3 (frontend): trip detail page with itinerary list/create/edit/delete UI",
      "depends_on": ["TASK-004", "TASK-006"],
      "status": "completed",
      "assignee": "agent:claude-code",
      "reviewer": "agent:code-review-fork",
      "workstream": "frontend-itinerary",
      "agent_role": "role:frontend-specialist",
      "execution_context": "shared",
      "thread_policy": "serialize-in-session",
      "thread_ref": "this-session",
      "write_set": ["frontend/src/app/features/trips/trip-detail/**", "frontend/src/app/core/itinerary/**", "frontend/src/app/features/trips/trips.routes.ts", "frontend/src/app/features/trips/trip-list/trip-list.component.html", "frontend/src/app/features/trips/trip-list/trip-list.component.scss", "frontend/src/app/features/trips/trip-list/trip-list.component.ts", "frontend/src/app/features/trips/trip-date.util.ts"],
      "checkpoint": null,
      "assurance_status": "passed",
      "assurance_requires": [],
      "task_brief": "harness-state/TASK-007.md"
    },
    {
      "id": "TASK-008",
      "goal": "Bug fix (found via live manual testing): trip date fields silently misparse typed dd/mm/yyyy input as US mm/dd/yyyy",
      "depends_on": ["TASK-004"],
      "status": "completed",
      "assignee": "agent:claude-code",
      "reviewer": "agent:code-review-fork",
      "workstream": "frontend-trip",
      "agent_role": "role:frontend-specialist",
      "execution_context": "shared",
      "thread_policy": "serialize-in-session",
      "thread_ref": "this-session",
      "write_set": ["frontend/src/app/core/date/br-date-adapter.ts", "frontend/src/app/core/date/br-date-adapter.spec.ts", "frontend/src/app/features/trips/trip-form/trip-form.component.ts"],
      "checkpoint": null,
      "assurance_status": "passed",
      "assurance_requires": [],
      "task_brief": "harness-state/TASK-008.md"
    }
  ]
}
```

## Transition log

- r1: Draft graph created from approved project context (`project-context@2`). TASK-001 (backend auth) ready and dispatched to this session; TASK-002 (frontend auth) blocked on TASK-001. Módulos 2-9 not yet decomposed — will be graphed when reached.
- r2: TASK-001 completed. Independent review (forked `code-review` agent) found 3 issues (JWT filter letting `UsernameNotFoundException` escape → 500 instead of 401; register race condition on duplicate email → 500 instead of 409; case-sensitive email lookup allowing duplicate accounts); all 3 fixed and re-verified (8/8 tests passing). `assurance_status: passed`. TASK-002 unblocked → `ready`. See `HANDOFF-TASK-001-01.md`.
- r3: TASK-002 completed. Write_set expanded during execution to include `frontend/src/app/features/home/**` (minimal placeholder needed for the guard to protect, since Módulo 8 doesn't exist yet), `app.routes.ts`, `app.config.ts` (wiring), and `frontend/package.json` (added `@angular/animations`, which was missing). Independent review found 3 issues (`isAuthenticated`/`currentUserEmail` memoized via `computed()` didn't re-evaluate time-based JWT expiry; duplicate login/register scss; `atob()` Latin1 decoding corrupting non-ASCII JWT claims); all 3 fixed, 2 regression tests added, re-verified (19/19 tests passing, clean `ng build`). `assurance_status: passed`. Módulo 1 (backend + frontend) is now complete. Módulo 2 (Viagens/Trip) not yet decomposed. See `HANDOFF-TASK-002-01.md`. Also: per explicit user instruction, this session no longer commits/pushes on its own — it prepares changes and hands the user a commit message; the user runs `git add`/`git commit`/`git push`.
- r4: H-001 (commit/push TASK-002) and H-002 (manual browser verification) confirmed resolved outside this session (git log shows `f0406e2`/`3d94f5e`/`b3fb33d` committed, working tree clean; user confirmed the browser walkthrough passed) — see `PENDING.md@5`. Módulo 1 fully closed. Módulo 2 (Viagens/Trip) decomposed into TASK-003 (backend: Trip CRUD scoped to the owning user, depends on TASK-001) and TASK-004 (frontend: trip list/create/edit/delete UI, depends on TASK-002 and TASK-003). TASK-003 dependencies satisfied → dispatched (`active`) to this session, `agent:claude-code`, `role:backend-specialist`, `model_tier: balanced` (bounded CRUD, deterministic acceptance). TASK-004 `blocked` on TASK-003. See `harness-state/TASK-003.md`, `harness-state/TASK-004.md`.
- r5: TASK-003 completed. Independent review (forked `code-review` agent, medium effort) found 2 issues: (1) blocking — missing test coverage for the required-field validation path (only bad-dates was covered, despite the brief explicitly requiring both); fixed by adding `TripControllerSecurityTest.rejectsCreateWithMissingRequiredFields`; (2) non-blocking follow-up candidate — `TripService.resolveOwner()` duplicates the per-request user lookup `AppUserDetailsService` already does during JWT auth (efficiency only, no correctness impact); recorded as a follow-up, not fixed (would touch the shared JWT auth path, out of this task's write_set). Re-verified: 25/25 backend tests passing (`./mvnw test -Dtest='!TripplannerApplicationTests'`, `DB_USERNAME`/`DB_PASSWORD` sourced from `backend/.env`); `TripplannerApplicationTests.contextLoads` still not runnable this session (`JWT_SECRET` not in `.env` — same pre-existing gap as TASK-001, not a regression). `assurance_status: passed`. Módulo 2 backend is now complete. TASK-004 unblocked → dispatched (`active`) to this session, `agent:claude-code`, `role:frontend-specialist`, `model_tier: balanced`. See `HANDOFF-TASK-003-01.md`.
- r6: TASK-004 completed. Write_set expanded during execution to include `frontend/src/app/features/home/{home.component.ts,home.component.html}` (added a "Minhas viagens" nav button — otherwise no in-app path to `/trips`, same pattern as TASK-002's `/home` placeholder) and `frontend/src/app/app.config.ts` (added `MAT_DATE_LOCALE: 'pt-BR'` during review remediation, an app-wide provider that couldn't be scoped narrower). Independent review (forked `code-review` agent, medium effort) found 3 issues: (1) correctness bug — `TripFormComponent.ngOnInit` used `route.snapshot.paramMap` instead of subscribing, so Angular's component-instance reuse across sibling `':id/edit'` activations could leave the form showing/saving over the wrong trip; fixed by subscribing to `route.paramMap`; (2) `MatDatepicker` had no `MAT_DATE_LOCALE` provider and would render in English inside an otherwise all-Portuguese UI; fixed via `app.config.ts`; (3) trip dates in the list view were raw ISO strings instead of a localized format; fixed with a small `formatDate()` helper. Re-verified: 24/24 frontend tests passing, `ng build` clean. `assurance_status: passed`. Módulo 2 (Viagens/Trip, backend + frontend) is now complete. Módulo 3 (Itinerário) not yet decomposed. See `HANDOFF-TASK-004-01.md`.
- r7: User-directed, out-of-band request (not part of the Módulo roadmap; user explicitly asked to pause continuing with tasks until this landed): (1) no logout control was reachable from `/trips` (only `/home` had one — the app shell's `mat-toolbar` in `app.component.html` had never been wired to auth state); (2) no public landing page existed (`/` redirected straight to `/login`). New node TASK-005 created and dispatched (`active` → `completed` same session): global toolbar in `AppComponent` now shows nav+logout (authenticated) or login/register CTAs (anonymous) on every route; new `LandingComponent` (hero + 3-card feature preview, Unsplash imagery) replaces the `/` redirect. Independent review (forked `code-review` agent, medium effort) found 6 issues: 2 fixed (a `<button routerLink>` with no `href`/middle-click support on `/home`'s "Minhas viagens" control, fixed to `<a>`; landing feature images were un-lazy-loadable CSS backgrounds with no fallback for the hero if the third-party Unsplash image fails, fixed with real lazy `<img>` tags + a fallback hero background-color), 4 recorded as non-blocking follow-ups (`isAuthenticated()` CD-tick staleness now more visible in a persistent toolbar; the same staleness duplicated in the landing CTA; auth-state CTA branching duplicated between toolbar/landing with no shared source of truth; `.feature-grid`/`.trip-grid` CSS pattern duplication) — none violate an acceptance criterion, so not blocking per the bounded-review-rounds policy. Re-verified: 24/24 frontend tests passing, `ng build` clean. `assurance_status: passed`. Módulo 3 (Itinerário) remains un-decomposed, paused per the user's explicit instruction — resume is the user's call. See `HANDOFF-TASK-005-01.md`.
- r8: User confirmed manual browser verification of Módulo 2 + TASK-005 passed ("tudo testado e aprovado") and confirmed commit/push done outside this session (git log: `d003301`, `a0240fc`, `9ac3713`; `master...origin/master` in sync) — see `PENDING.md@11`. Pause lifted; user asked to continue development. Módulo 3 (Itinerário) decomposed into TASK-006 (backend: itinerary items CRUD nested under `/api/trips/{tripId}/itinerary-items`, scoped through the trip's owner, depends on TASK-003) and TASK-007 (frontend: a new trip-detail page showing/managing itinerary items, depends on TASK-004 and TASK-006). TASK-006 dependencies satisfied → dispatched (`active`) to this session, `agent:claude-code`, `role:backend-specialist`, `model_tier: balanced`. TASK-007 `blocked` on TASK-006. See `harness-state/TASK-006.md`, `harness-state/TASK-007.md`.
- r9: TASK-006 completed. Independent review (forked `code-review` agent, medium effort) found 2 non-blocking issues (no acceptance-criterion or correctness violation): (1) `update()` resolved trip ownership twice per request; fixed by reusing the trip already loaded via `findOwnedItem`; (2) `ItineraryItemService.findOwnedTrip` duplicates `TripService`'s private ownership-resolution logic verbatim; recorded as a non-blocking follow-up (same category as TASK-003's accepted follow-up about the identical duplicate-lookup pattern), not fixed since it would touch `TripService`, outside this task's `write_set`. Separately, while writing the handoff, found and fixed a real gap not caught by the review: deleting a trip with itinerary items would have failed on the `trip_id` FK constraint (no cascade configured); fixed with `@OnDelete(action = OnDeleteAction.CASCADE)` on `ItineraryItem.trip`, generating `ON DELETE CASCADE` via `ddl-auto: update`. Re-verified: 44/44 backend tests passing (`./mvnw test -Dtest='!TripplannerApplicationTests'`). `assurance_status: passed`. Módulo 3 backend is now complete. TASK-007 unblocked → dispatched (`active`) to this session, `agent:claude-code`, `role:frontend-specialist`, `model_tier: balanced`. See `HANDOFF-TASK-006-01.md`.
- r10: TASK-007 completed. Write_set expanded during execution/remediation to include `trip-list.component.scss` (styling for the new title link) and, during review remediation, `trip-list.component.ts` plus a new `trip-date.util.ts` (shared date formatter extracted to remove duplication) — documented in `HANDOFF-TASK-007-01.md`. Independent review (forked `code-review` agent, medium effort) found 5 issues, all fixed: (1) correctness bug — the same route-reuse class of bug TASK-004 fixed, now in `TripDetailComponent`: navigating trip-A → trip-B detail pages left the itinerary edit form open with trip-A's item bound; fixed by resetting `trip`/`items`/the open form on every `paramMap` emission; (2) correctness bug — `loadTrip()`'s error handler never cleared the `trip` signal, so a failed navigation kept rendering the previous trip's content; fixed by the same reset plus explicit nulling in the error handlers; (3) acceptance-criterion violation — the date field's HTML `min`/`max` attributes don't enforce Angular form validity, so nothing actually validated "date within the trip's range" client-side; fixed with an explicit range check in `submit()`, mirroring `TripFormComponent`'s date-order check pattern; (4) governance — `trip-list.component.scss` was edited without being in TASK-007's declared `Owned paths`; fixed by amending the task brief and this write_set rather than reverting a genuinely-needed change; (5) reuse — itinerary/trip dates in the detail page were raw ISO strings inconsistent with `trip-list`'s `dd/mm/yyyy` format; fixed by extracting a shared `formatDate` util used by both components. Re-verified: 29/29 frontend tests passing, `ng build` clean. `assurance_status: passed`. Módulo 3 (Itinerário, backend + frontend) is now complete. Módulo 4 (Integração Clima) not yet decomposed. See `HANDOFF-TASK-007-01.md`.
- r11: User connected the Claude in Chrome extension and asked this session to test the already-running app (frontend :4200 + backend :8080, `JWT_SECRET` set by the user — the first time this session had live browser + working backend access). Live walkthrough found a real bug not caught by any prior code review: typing a date directly into the trip form's date fields (e.g. `01/09/2026`) was silently misparsed as US `mm/dd/yyyy` (became `09/01/2026`), because Angular Material's default `NativeDateAdapter.parse()` ignores `MAT_DATE_LOCALE` for typed-text parsing (only display formatting was locale-aware, from TASK-005). Calendar-picker selection was unaffected. New node TASK-008 created and dispatched (`active` → `completed` same session): added `BrDateAdapter` (parses `dd/mm/yyyy` explicitly), wired into `trip-form.component.ts` via component-level `DateAdapter`/`MAT_DATE_FORMATS` providers (replacing `MatNativeDateModule`). Independent review (forked `code-review` agent, medium effort) found 1 low-severity but genuine issue: the initial fix used `new Date(year, month-1, day)`, which has the well-known JS pitfall of converting two-digit years (0-99) to 19xx; fixed using the same `setFullYear`/`setHours` idiom `NativeDateAdapter` itself uses internally to avoid this, confirmed by reading Angular Material's own source. Re-verified: 35/35 frontend tests passing, `ng build` clean, plus a full live re-walkthrough (login → type trip dates → save → confirm correct → open itinerary → add/edit item → out-of-range date rejected → delete trip with itinerary item, no error → logout) — all passed, no console errors, test data cleaned up. `assurance_status: passed`. This live walkthrough also satisfies the manual-verification intent of `PENDING.md`'s H-006 for Módulo 3. See `HANDOFF-TASK-008-01.md`.
