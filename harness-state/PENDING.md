---
schema: harness.pending/v1
id: pending-main
revision: 10
status: attention
updated_at: 2026-08-25T00:05:00Z
updated_by: role:orchestrator
---

# Pending work

This is not a technical progress log. Never record task dispatch, progress, blockers, dependencies, completion, leases, contexts, or readiness here instead of revising `harness-state/TASK-GRAPH.md`. Update this artifact only for human action/decision state or macro project outcomes, with the latest graph revision as technical source when applicable.

## Human action required

| ID | Area | Owner | Request or decision | Why / delivery effect | Needed by | Status | Source |
| --- | --- | --- | --- | --- | --- | --- | --- |
| H-003 | backend-trip, frontend-trip, frontend-shell | human:Gustavo | Commit and push TASK-003 + TASK-004 + TASK-005 (this session doesn't commit/push on its own) — commit messages provided in chat, 2026-08-25 | Work sits uncommitted in the working tree until pushed | before starting Módulo 3 | open | `harness-state/HANDOFF-TASK-003-01.md`, `harness-state/HANDOFF-TASK-004-01.md`, `harness-state/HANDOFF-TASK-005-01.md` |

## Project completion overview

| Area | Current state | What remains | Human dependency | Technical source |
| --- | --- | --- | --- | --- |
| Módulo 0 — Setup | complete | Nothing | none | prior commits |
| Módulo 1 — Autenticação & Usuários | complete | Nothing | none | `graph-main@3` |
| Módulo 2 — Viagens (Trip) | complete (pending commit) | Backend and frontend both implemented, tested, and manually verified by the user. See H-003 above | H-003 | `graph-main@6` |
| UX fix (out of band) — global logout/nav + landing page | complete (pending commit) | Not a roadmap módulo; user-directed fix (TASK-005), manually verified by the user. See H-003 above | H-003 | `graph-main@7` |
| Módulo 3 — Itinerário | not-started, paused | Everything. Decomposition explicitly paused by human:Gustavo (2026-08-25) until the TASK-005 UX fix landed | resume is the user's call | not yet decomposed |
| Módulo 4 — Integração Clima | not-started | Everything | none right now | not yet decomposed |
| Módulo 5 — Integração Localização/Rotas | not-started | Everything | none right now | not yet decomposed |
| Módulo 6 — Integração Câmbio | not-started | Everything | none right now | not yet decomposed |
| Módulo 7 — Orçamento/Despesas | not-started | Everything | none right now | not yet decomposed |
| Módulo 8 — Dashboard | not-started | Everything | none right now | not yet decomposed |
| Módulo 9 — Polimento final | not-started | Everything | none right now | not yet decomposed |

## Recently resolved

| ID | Resolution | Resolved by | Evidence |
| --- | --- | --- | --- |
| D-001..D-004 | Runtime mode, learning destination, next module, and out-of-scope items confirmed | human:Gustavo | `harness-state/PROJECT-CONTEXT.md@2` |
| H-001 | Commit/push of TASK-002 confirmed done outside this session | human:Gustavo | git log: `f0406e2`, `3d94f5e`, `b3fb33d`; working tree clean |
| H-002 | Manual browser verification of Módulo 1 auth UI confirmed done, working as expected | human:Gustavo | user confirmation, 2026-08-24 |
| H-004 | Manual browser verification of Módulo 2 (Trip CRUD) and TASK-005 (logout, landing page) confirmed done — "tudo testado e aprovado" | human:Gustavo | user confirmation, 2026-08-25 |
| TASK-001 | Backend auth (register/login, JWT, protected endpoint) implemented, reviewed, and fixed | agent:claude-code + agent:code-review-fork | `harness-state/HANDOFF-TASK-001-01.md` |
| TASK-002 | Frontend auth UI (login/register, interceptor, guard) implemented, reviewed, and fixed | agent:claude-code + agent:code-review-fork | `harness-state/HANDOFF-TASK-002-01.md` |
| TASK-003 | Backend Trip CRUD (owner-scoped create/list/get/update/delete) implemented, reviewed, and fixed | agent:claude-code + agent:code-review-fork | `harness-state/HANDOFF-TASK-003-01.md` |
| TASK-004 | Frontend Trip UI (list/create/edit/delete) implemented, reviewed, and fixed | agent:claude-code + agent:code-review-fork | `harness-state/HANDOFF-TASK-004-01.md` |
| TASK-005 | UX fix: global logout/nav in the app shell + public landing page at `/`, implemented, reviewed, and fixed | agent:claude-code + agent:code-review-fork | `harness-state/HANDOFF-TASK-005-01.md` |
