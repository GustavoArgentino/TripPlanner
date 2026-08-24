---
schema: harness.learning-profile/v1
id: learning-profile
revision: 1
status: active
owner: human:Gustavo
consent_updated_at: 2026-08-24T00:00:00Z
retention: external-local-vault
publication: pre-approved-to-named-destination
source_references: none
---

# Learning profile

## Goals

- Understand the concepts behind each TripPlanner module while it's being built (Spring Security/JWT, Spring Data JPA, Angular standalone components/routing, etc.), continuing the practice already started in Módulo 0.

## Observation consent

- May read: this project's source (`backend/`, `frontend/`), task briefs/handoffs/reviews under `harness-state/`, and the existing study-vault notes for continuity of style.
- Must not read/export: `.env` values or any real credentials/secrets, and anything outside the `TripPlanner-Project` repo and the `TripPlanner` study-vault folder.
- Packaging profile selected: none / core-learning / full — not applicable (native harness install already includes project-learning).

## Evidence by skill

| Skill | Self-assessment | Demonstrated evidence | Confidence |
| --- | --- | --- | --- |
| Spring Security / JWT | developing | Módulo 1 backend built end to end (filter chain, stateless auth, JWT issue/validate, BCrypt); 3 real bugs found by independent review and understood/fixed (401 vs 403 entry point, TOCTOU race on duplicate email, case-sensitive email) | medium |
| Angular standalone auth flows | unknown | none yet — planned for TASK-002 | low |

## Learning queue

- See `LEARNING-QUEUE.md` (to be created once Módulo 1 work starts).

## Destination preferences

- Activation/write gate: satisfied — destination explicitly confirmed by the user on 2026-08-24.
- Destination type: obsidian (local filesystem vault, no connector/MCP needed).
- Exact location: `C:\Users\Riva\OneDrive\Área de Trabalho\Obsidian-Estudos\Estudos\TripPlanner\`.
- Format and organization: one Markdown note per module (`NN - Nome do módulo.md`), following the user's own `Template - Relatório de Módulo.md` (O que foi alterado / Por que foi feito / Como funciona / Conceitos importantes para estudar / Arquivos modificados), and updating the module status table in `TripPlanner.md`.
- Capability status: available — verified by directly reading files at this path in this session (plain local filesystem access, same as any other local path).
- Write policy: private destination writes within this approved scope are allowed (matches the pattern the user already established for Módulo 0).
- Public sharing: approval-required (not applicable — this vault is a private local folder).
- Credentials: none stored here.

## Latest debrief

- Módulo 1 (backend) note written at `Obsidian-Estudos/Estudos/TripPlanner/01 - Autenticação & Usuários.md`, 2026-08-24: covers JWT/stateless auth, BCrypt, filter ordering, 401-vs-403 entry point, and the TOCTOU race on duplicate registration — all surfaced as concrete concepts from real bugs the independent review found in this module's implementation.
