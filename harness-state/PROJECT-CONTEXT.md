---
schema: harness.project-context/v1
id: project-context
revision: 2
status: approved
mode: delivery+learning
updated_at: 2026-08-24T00:00:00Z
approved_by: human:Gustavo
supersedes: project-context@1
discovery_snapshot: discovery-001
source_references: "Obsidian-Estudos/Estudos/TripPlanner/TripPlanner.md; Obsidian-Estudos/Estudos/TripPlanner/00 - Setup.md; Obsidian-Estudos/Estudos/TripPlanner/Template - Relatório de Módulo.md"
capability_manifest: capability-manifest@1
rules_map: rules-map@1
pending_authority: harness-state/PENDING.md
learning_profile: learning-profile@1
---

# Project context

## Project state

- Kind: existing (technical scaffold committed, zero domain features implemented yet).
- Evidence:
  - Repo has 3 commits: `Initial commit`, `chore: backend/frontend, Swagger e Angular Material`, `Adiciona .env.example e ignora arquivos .env`.
  - `backend/`: Spring Boot 4.1.1 (Java 17) with spring-boot-starter-data-jpa, -security, -validation, -webmvc, postgresql driver, springdoc-openapi-starter-webmvc-ui 3.0.3. Only `TripplannerApplication.java` bootstrap class exists — no entities, controllers, repositories.
  - `frontend/`: Angular 19 + Angular Material, standalone components. `core/`, `shared/`, `features/` folders exist but are empty (`.gitkeep` only). App shell has a `mat-toolbar` and `router-outlet`.
  - External study vault (`Obsidian-Estudos/Estudos/TripPlanner/`) documents a 9-module roadmap and module 0 (Setup) in detail, confirming this is a personal full-stack study/portfolio project.

## Intent

- Problem: Build a full-stack trip-planning application end to end, module by module, both as a working product and as a guided learning exercise in Java/Spring Boot and Angular.
- Users: Primary — the developer (portfolio/learning). Secondary — travelers who would plan trips (itinerary, budget, weather, routes, currency) in the finished app.
- Outcome: Each roadmap module ships working, documented, and verifiable; end state is a functioning trip planner covering auth, trips, itinerary, weather, location/routes, currency, budget, and a dashboard.

## Scope

- In (per user's own roadmap in the study vault):
  1. Setup — done.
  2. Autenticação & Usuários (Spring Security + JWT) — **current target**.
  3. Viagens (Trip).
  4. Itinerário.
  5. Integração Clima.
  6. Integração Localização/Rotas.
  7. Integração Câmbio.
  8. Orçamento/Despesas.
  9. Dashboard.
  10. Polimento final.
- Out (confirmed by user 2026-08-24): no real third-party booking/reservation integrations; no multi-user/collaborative trips for now (single-user trips only).

## Success measures

- Each module's backend endpoints documented in Swagger and covered by JUnit/Mockito tests.
- Each module's frontend feature implemented under `frontend/src/app/features/` per the core/shared/features convention already established.

## Constraints

- PostgreSQL runs native on the developer's machine (no Docker); database `tripplanner` must exist locally.
- Auth/DB credentials only via environment variables (`DB_USERNAME`, `DB_PASSWORD`, no default password) — never committed.
- `ddl-auto: update` acceptable only in dev; versioned migrations (Flyway/Liquibase) expected before production.
- Angular: standalone components, lazy-loaded feature routes.

## Rules and capabilities

- Durable rules: `rules-map@1` (not yet built).
- Detected/required capabilities: `capability-manifest@1` (not yet built).

## Assumptions and unknowns

- All prior open unknowns (runtime mode, learning destination, out-of-scope items, next module) were resolved by explicit user confirmation on 2026-08-24; see decisions below.
- A-001 (assumption, owner: human:Gustavo): Backend module 1 (auth) ships before frontend module 1 (auth UI), since the UI needs real endpoints to integrate against. Revisit if the user prefers building the UI against a stub first.

## Verification environment

- Backend: `./mvnw test` (JUnit/Mockito), local Postgres reachable at `localhost:5432` with `DB_USERNAME`/`DB_PASSWORD` set.
- Frontend: `ng test` (Karma/Jasmine).

## References

- Decisions (confirmed by human:Gustavo, 2026-08-24):
  - D-001: Runtime mode = `delivery+learning`.
  - D-002: Learning-note destination = existing vault `Obsidian-Estudos/Estudos/TripPlanner/`, following `Template - Relatório de Módulo.md`.
  - D-003: Next target = Módulo 1 — Autenticação & Usuários.
  - D-004: Out of scope for now = real booking/reservation integrations, multi-user/collaborative trips.
- Learning profile: `learning-profile@1` (`harness-state/LEARNING-PROFILE.md`).
- Pending authority: `harness-state/PENDING.md` for human actions and macro project completion only.
- Provenance: repository state as of commit `a6083d4`; study vault at `Obsidian-Estudos/Estudos/TripPlanner/` (`TripPlanner.md`, `00 - Setup.md`, `Template - Relatório de Módulo.md`).
