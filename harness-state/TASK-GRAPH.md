---
schema: harness.task-graph/v1
id: graph-main
revision: 3
status: active
project_context: project-context@2
updated_at: 2026-08-24T02:00:00Z
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
    }
  ]
}
```

## Transition log

- r1: Draft graph created from approved project context (`project-context@2`). TASK-001 (backend auth) ready and dispatched to this session; TASK-002 (frontend auth) blocked on TASK-001. Módulos 2-9 not yet decomposed — will be graphed when reached.
- r2: TASK-001 completed. Independent review (forked `code-review` agent) found 3 issues (JWT filter letting `UsernameNotFoundException` escape → 500 instead of 401; register race condition on duplicate email → 500 instead of 409; case-sensitive email lookup allowing duplicate accounts); all 3 fixed and re-verified (8/8 tests passing). `assurance_status: passed`. TASK-002 unblocked → `ready`. See `HANDOFF-TASK-001-01.md`.
- r3: TASK-002 completed. Write_set expanded during execution to include `frontend/src/app/features/home/**` (minimal placeholder needed for the guard to protect, since Módulo 8 doesn't exist yet), `app.routes.ts`, `app.config.ts` (wiring), and `frontend/package.json` (added `@angular/animations`, which was missing). Independent review found 3 issues (`isAuthenticated`/`currentUserEmail` memoized via `computed()` didn't re-evaluate time-based JWT expiry; duplicate login/register scss; `atob()` Latin1 decoding corrupting non-ASCII JWT claims); all 3 fixed, 2 regression tests added, re-verified (19/19 tests passing, clean `ng build`). `assurance_status: passed`. Módulo 1 (backend + frontend) is now complete. Módulo 2 (Viagens/Trip) not yet decomposed. See `HANDOFF-TASK-002-01.md`. Also: per explicit user instruction, this session no longer commits/pushes on its own — it prepares changes and hands the user a commit message; the user runs `git add`/`git commit`/`git push`.
