---
schema: harness.handoff/v1
id: HANDOFF-TASK-005-01
task: TASK-005@1
attempt: 1
status: completed
author: agent:claude-code
workstream: frontend-shell
agent_role: role:frontend-specialist
execution_context: shared
thread_ref: this-session
created_at: 2026-08-25T00:00:00Z
model_tier_used: balanced
model_route_changes: none
execution_budget: none
---

# Handoff — TASK-005

## Result

The app now has a global toolbar (in `AppComponent`, so it's present on every route) that shows "Início / Minhas viagens / Sair" when authenticated, or "Entrar / Criar conta" when not — fixing the missing logout on `/trips`. The root path `/` now renders a public landing page with a hero image, tagline, and CTAs, instead of redirecting straight to `/login`.

- Execution context: this Claude Code session (no isolated/parallel context available); no adapter needed.

## Changes

- `frontend/src/app/app.component.ts/.html/.scss`: toolbar now reads `AuthService.isAuthenticated`, shows nav + logout or login/register CTAs accordingly; sticky positioning.
- `frontend/src/app/app.component.spec.ts`: added `provideHttpClient`/`provideHttpClientTesting`/`provideRouter` (now required since `AppComponent` injects `AuthService` and `Router`).
- `frontend/src/app/app.routes.ts`: `''` now lazy-loads `LandingComponent` instead of `redirectTo: 'login'`.
- `frontend/src/app/features/home/home.component.ts/.html`: removed the now-redundant in-page "Sair" button (logout lives in the global toolbar); changed the "Minhas viagens" control from `<button routerLink>` to `<a routerLink>` (review fix, see below).
- `frontend/src/app/features/landing/**` (new): `LandingComponent` — hero section (Unsplash background image, tagline, auth-aware CTA), a 3-card feature preview (itinerário/orçamento/clima-rotas-câmbio, each with an Unsplash photo), footer.

## Change unit and authority

- Coherent unit: frontend app-shell/landing fix, independent of the Módulo 1/2 feature work; touches shared shell files (`app.component.*`, `app.routes.ts`) plus one new feature folder.
- Split boundaries: explicitly excludes `features/trips/**`, `features/auth/**`, and all backend code, per the task brief.
- Commit/integration/push/deploy/publication authority: not exercised — no commit was made; changes remain in the working tree pending the user's own review/commit.

## Acceptance evidence

| Criterion | Result | Evidence |
| --- | --- | --- |
| Logout reachable from `/home` and `/trips` without detouring through `/home` | pass | Global toolbar in `app.component.html` renders on every route when `isAuthenticated()` is true; verified via `ng build` (toolbar is outside `<router-outlet>`, always rendered) — not visually walked through live (see Verification run) |
| `/` renders a public landing page instead of redirecting to `/login` | pass | `app.routes.ts` — `''` now `loadComponent`s `LandingComponent`; `/login` and `/register` remain reachable via the landing page's CTAs and are otherwise untouched |
| No existing route/guard/auth regression | pass | `npx ng test` 24/24 (unchanged auth/trips specs still pass); `npx ng build` clean, `login-component`/`register-component`/`home-component`/`trip-*`/`landing-component` all still separate lazy chunks |

## Verification run

- Command: `npx ng test --watch=false --browsers=ChromeHeadless` — 24/24 passed, both before and after review remediation.
- Command: `npx ng build` — clean production build both before and after remediation; new `landing-component` chunk confirmed lazy-loaded (5.77 kB / 2.08 kB transfer).
- Not run: live in-browser walkthrough — Claude in Chrome extension not connected this session (same limitation as TASK-002/004). Also not run: a real backend-connected check of the logout flow, since the backend can't boot without `JWT_SECRET` (same pre-existing gap as prior handoffs).
- Environment: Angular CLI 19.2.27, Karma 6.4.4 + Chrome Headless 151, same local machine as prior tasks.

## Execution budget

- Goal lineage: TASK-005, attempt 1.
- Usage: 1 implementation pass + 1 fix pass after independent review; 0 no-progress cycles; 0 context expansions.
- Decision: `continue` (task complete, no ceiling reached).
- Token/cost measurement: unavailable.

## Discoveries and risks

- The toolbar's `isAuthenticated()` check re-evaluates on Angular change-detection ticks, not a live wall-clock timer — same documented characteristic as `AuthService` itself (see `auth.service.ts`'s own comment on this). Flagged again by this round's review as now more visible (the toolbar persists across the whole session instead of being destroyed/recreated per page); not fixed here — would need a periodic re-check (e.g. an interval or `visibilitychange` listener) which is a bigger change than this bounded UX fix warrants. Recorded as a follow-up candidate below.
- The auth-state CTA branching (nav+logout vs. login/register) is now duplicated between `app.component.html` and `landing.component.html` with slightly different copy ("Minhas viagens" vs. "Ver minhas viagens"). Not unified in this task (would mean introducing a small shared component); recorded as a follow-up candidate.
- `.feature-grid` in `landing.component.scss` reimplements the same responsive card-grid pattern as `.trip-grid` in `trip-list.component.scss`. Not deduplicated across the two feature folders in this task (would mean promoting it to an app-level shared partial, out of this task's narrow write_set); recorded as a follow-up candidate.
- The landing page's hero and feature images are hotlinked from Unsplash's CDN (`images.unsplash.com`), verified reachable at review time via direct HTTP checks. This is a live third-party dependency on the app's default entry point; mitigated with a dark fallback `background-color` behind the hero (so a failed/slow image still renders a legible, intentional-looking page) and `loading="lazy"` real `<img>` tags for the below-the-fold feature images (added during review remediation, see below). Worth revisiting (e.g. self-hosting the images) if this becomes a real portfolio/demo deployment rather than local dev.

## Routing and authority

- Tier used and reason: balanced — bounded, deterministic Angular UI work (a global toolbar wire-up + a static-content landing page), no frontier-level ambiguity.
- Escalation/decomposition: none.
- Routing granted no additional permissions and removed no review or verification gate.

## Review request

- Independent review already run (forked `code-review` skill agent) before this handoff was written. It found 6 issues; 2 fixed in this round, 4 recorded as non-blocking follow-ups (style/optional-hardening per the bounded-review-rounds policy — none violate an acceptance criterion):
  1. **Fixed (correctness/consistency)** — `home.component.html`'s "Minhas viagens" control was a `<button mat-flat-button routerLink>` instead of an `<a>`, so `RouterLink` never set an `href` and ctrl/cmd/middle-click to open in a new tab silently did nothing, inconsistent with every other nav link added in this same change. Fixed: changed to `<a mat-flat-button routerLink>`.
  2. **Fixed (resilience/performance)** — the landing page's 3 feature images were CSS `background-image`s (un-lazy-loadable, no `alt` text) and the hero background had no fallback if the Unsplash image failed to load. Fixed: feature images are now real `<img loading="lazy" alt="...">` elements; the hero got a dark fallback `background-color` behind the image/gradient.
  3. **Non-blocking follow-up** — toolbar `isAuthenticated()` staleness across CD ticks (see Discoveries and risks).
  4. **Non-blocking follow-up** — same staleness duplicated in the landing hero CTA (see Discoveries and risks).
  5. **Non-blocking follow-up** — auth-state CTA branching duplicated between toolbar and landing hero (see Discoveries and risks).
  6. **Non-blocking follow-up** — `.feature-grid`/`.trip-grid` CSS duplication (see Discoveries and risks).
- Round 2 (focused remediation) re-verified: 24/24 frontend tests pass, `ng build` clean. No new findings introduced by the fixes.

## User-facing closeout

- Outcome: Logout is now reachable from every authenticated page, and `/` is a real landing page with travel imagery and clear CTAs, both implemented, self-tested, and independently reviewed with the 2 blocking-adjacent findings fixed.
- Stage: Ready to resume module progression — Módulo 3 (Itinerário) is still un-decomposed, paused per your explicit instruction.
- Progress: TASK-005 completed.
- Material changes: see Changes above.
- Verification: `npx ng test --watch=false --browsers=ChromeHeadless` → 24/24 passed; `npx ng build` → clean. Live browser walkthrough left for you (extension not connected this session).
- Lifecycle state: completed.
- Blockers: None.
- Follow-up candidates (non-blocking, not done): auth-CTA duplication between toolbar/landing, `isAuthenticated()` staleness while idle on a long-lived page, `.feature-grid`/`.trip-grid` CSS duplication.
- Next action: your call — resume by decomposing Módulo 3 (Itinerário), or first do a manual browser pass (needs `JWT_SECRET` added to `backend/.env` to actually log in and see logout/landing in action end to end).
- Inspectable paths: `harness-state/TASK-005.md`, `harness-state/HANDOFF-TASK-005-01.md`, `harness-state/TASK-GRAPH.md`, `harness-state/PENDING.md`.
- Human action required: none to continue technically. Optional: visually check the landing page and the new toolbar behavior once the backend can boot locally (`JWT_SECRET` set).
