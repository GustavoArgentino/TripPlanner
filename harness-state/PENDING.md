---
schema: harness.pending/v1
id: pending-main
revision: 3
status: attention
updated_at: 2026-08-24T02:00:00Z
updated_by: role:orchestrator
---

# Pending work

This is not a technical progress log. Never record task dispatch, progress, blockers, dependencies, completion, leases, contexts, or readiness here instead of revising `harness-state/TASK-GRAPH.md`. Update this artifact only for human action/decision state or macro project outcomes, with the latest graph revision as technical source when applicable.

## Human action required

| ID | Area | Owner | Request or decision | Why / delivery effect | Needed by | Status | Source |
| --- | --- | --- | --- | --- | --- | --- | --- |
| H-001 | frontend-auth | human:Gustavo | Commit and push TASK-002 (this session no longer commits/pushes on its own, per explicit instruction) — commit message provided in chat | Work sits uncommitted in the working tree until pushed | before starting Módulo 2 | open | `harness-state/HANDOFF-TASK-002-01.md` |
| H-002 | frontend-auth | human:Gustavo | Manually verify the auth UI in a browser (`ng serve` + backend running) — not visually verified this session (Claude in Chrome extension not connected) | Unit tests cover logic, not the actual rendered/click-through experience | before considering Módulo 1 fully done | open | `harness-state/HANDOFF-TASK-002-01.md` |

## Project completion overview

| Area | Current state | What remains | Human dependency | Technical source |
| --- | --- | --- | --- | --- |
| Módulo 0 — Setup | complete | Nothing | none | prior commits |
| Módulo 1 — Autenticação & Usuários | complete (pending commit + manual browser check) | Backend and frontend both implemented and tested. See H-001/H-002 above | H-001, H-002 | `graph-main@3` |
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
| TASK-002 | Frontend auth UI (login/register, interceptor, guard) implemented, reviewed, and fixed | agent:claude-code + agent:code-review-fork | `harness-state/HANDOFF-TASK-002-01.md` |
