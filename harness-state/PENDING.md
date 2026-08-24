---
schema: harness.pending/v1
id: pending-main
revision: 2
status: clear
updated_at: 2026-08-24T01:00:00Z
updated_by: role:orchestrator
---

# Pending work

This is not a technical progress log. Never record task dispatch, progress, blockers, dependencies, completion, leases, contexts, or readiness here instead of revising `harness-state/TASK-GRAPH.md`. Update this artifact only for human action/decision state or macro project outcomes, with the latest graph revision as technical source when applicable.

## Human action required

| ID | Area | Owner | Request or decision | Why / delivery effect | Needed by | Status | Source |
| --- | --- | --- | --- | --- | --- | --- | --- |
| None | all | none | No human action currently recorded | none | none | clear | none |

## Project completion overview

| Area | Current state | What remains | Human dependency | Technical source |
| --- | --- | --- | --- | --- |
| Módulo 0 — Setup | complete | Nothing | none | prior commits |
| Módulo 1 — Autenticação & Usuários | partial | Backend done (register/login, JWT, protected endpoint, tests). Remaining: frontend (login/register UI, auth service, JWT interceptor, route guard) | none right now | `graph-main@2` |
| Módulo 2 — Viagens (Trip) | not-started | Everything | none right now | `graph-main@1` (not yet decomposed) |
| Módulo 3 — Itinerário | not-started | Everything | none right now | not yet decomposed |
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
| TASK-001 | Backend auth (register/login, JWT, protected endpoint) implemented, reviewed, and fixed | agent:claude-code + agent:code-review-fork | `harness-state/HANDOFF-TASK-001-01.md` |
