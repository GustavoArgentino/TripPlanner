---
schema: harness.task-graph/v1
id: graph-main
revision: 7
status: active
project_context: project-context@2
updated_at: 2026-08-25T00:00:00Z
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
