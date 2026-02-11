# Solo TTRPG Journal — Project Plan (revised)

## 1. Problem statement & goals
Problem: Solo tabletop RPG players need a lightweight, offline-first journal app to record campaigns, characters, and session notes; existing tools are often web-first, heavyweight, or not tailored to campaign-linked journaling.

Project goal: Ship an MVP Desktop app (Windows/macOS/Linux) built with Kotlin Multiplatform (KMP) that provides campaign & character management, linked journal entries, and an editor. Architect the codebase so core business logic and persistence are reusable for Web and Mobile later.

Success criteria:
- Users can create/list/view campaigns, characters, and journal entries.
- Data is persisted locally and survives restarts.
- Shared business logic and persistence live in a `shared` module and are testable.
- Desktop UI implemented with Compose Multiplatform and connected to shared use-cases.

Constraints:
- Solo developer; prioritize low-complexity choices.
- Offline-first local storage only for MVP.


## 2. Tech stack (choices + rationale + alternatives)
Primary choices:
- Kotlin Multiplatform — share domain & data logic across Desktop/Web/Mobile.
- Compose Multiplatform (Desktop) — single Kotlin UI approach; fast iteration and native-feeling desktop apps.
- SQLDelight + SQLite — type-safe SQL, multiplatform support, simple local persistence and migrations.
- Gradle (Kotlin DSL) — standard for KMP builds.
- Koin (or manual DI for MVP) — lightweight cross-platform DI; manual wiring is ok initially.
- Kotest + MockK — testing stack for unit tests and mocking.

Rationale:
- KMP + SQLDelight minimizes duplicated business logic and gives stable local DB on all targets.
- Compose Multiplatform allows fastest Desktop UI iteration without writing platform-specific UI code.

Optional alternatives:
- Replace SQLDelight with Realm (if advanced sync needed later) or Room (Android-only) — not recommended for KMP portability.
- Use Kodein or Dagger if DI complexity grows.
- Use Exposed (JVM-only) if Desktop-only and prefer Kotlin DSL SQL.


## 3. Updated project structure (recommended)
- `shared/`
    - `src/commonMain/kotlin/`
        - `domain/` (models, use-cases, validation)
        - `ports/` (repository interfaces)
        - `utils/` (date, serialization helpers)
    - `src/commonTest/`
    - `src/jvmMain/` (platform DB adapters for desktop)
    - `sqldelight/` (schema files shared)
    - `build.gradle.kts`
- `desktopApp/`
    - `src/main/kotlin/`
        - `ui/` (Compose screens)
        - `navigation/`
        - `App.kt` (launcher)
    - `resources/` (icons, seed data)
- `build.gradle.kts` (root)
- `settings.gradle.kts`

Notes:
- Keep SQLDelight schema under `shared/sqldelight/` so generated models are shared.
- Add `samples/` or `seeddata/` under `desktopApp/resources` for testing and onboarding.


## 4. MVP features (prioritized)
Priority (P1 highest):
P1 (Core)
1. Campaign management: create, edit, list, delete campaigns; metadata (name, tags, notes).
2. Character management: create, edit, list, delete characters; link character → campaign.
3. Journal entries: create/edit/save text entries linked to a campaign (optional character tag).
4. Persistent local storage (SQLDelight + SQLite) and simple migrations.
5. Simple navigation & UI: sidebar with campaigns, list/detail panes, entry editor.

P2 (Usability polish)
6. Search/filtering (campaign, character, date, tags).
7. Export/import JSON for backup.
8. Sample seed data and onboarding.

P3 (Future)
9. Web/Mobile ports reusing shared logic.
10. SRD/RuleSystem import & parser.

MVP scope: implement all P1 items; add 1-2 P2 items only if time allows.


## 5. Workplan — Phases & granular steps
Each step includes: goal, inputs, outputs, acceptance criteria, estimated effort (hours), dependencies.

Phase A — Project setup (Goal: scaffolding, build, DB tooling)

A1. Create project modules & Gradle skeleton
- Goal: repo modules created and Gradle structure in place.
- Inputs: repository, Gradle wrapper.
- Outputs: `shared/`, `desktopApp/`, root `build.gradle.kts` skeleton.
- Acceptance: `./gradlew projects` or `./gradlew :desktopApp:assemble` runs without unresolved plugin errors.
- Effort: 4–6h
- Dependencies: none

A2. Add Compose Multiplatform desktop setup
- Goal: minimal Compose desktop app runs.
- Inputs: Compose plugin samples.
- Outputs: `desktopApp` build config + `App.kt` launching an empty window.
- Acceptance: Desktop window launches via `./gradlew :desktopApp:run`.
- Effort: 3–4h
- Dependencies: A1

A3. Configure SQLDelight in `shared`
- Goal: SQLDelight plugin configured; initial schema created.
- Inputs: SQLDelight docs, schema definition for campaigns/characters/entries.
- Outputs: `.sq` files, generated Kotlin interfaces.
- Acceptance: Build succeeds and generated classes are present in build output.
- Effort: 3–5h
- Dependencies: A1


Phase B — Domain & data (Goal: shareable models + persistence)

B1. Define domain models & validation
- Goal: canonical data models.
- Inputs: feature list, simple field choices.
- Outputs: `Campaign`, `Character`, `JournalEntry` Kotlin data classes and validation rules.
- Acceptance: Models compile and are used by unit tests.
- Effort: 3–4h
- Dependencies: A3

B2. Define repository interfaces (ports)
- Goal: clear API boundary for data access.
- Inputs: use-case requirements.
- Outputs: interfaces: `CampaignRepository`, `CharacterRepository`, `EntryRepository`.
- Acceptance: Interfaces compile and are documented.
- Effort: 2–3h
- Dependencies: B1

B3. Implement SQLDelight schema & mappers
- Goal: storage schema maps to domain models.
- Inputs: models from B1.
- Outputs: `.sq` schema files, mapper functions between SQLDelight types and domain models.
- Acceptance: unit/integration tests read & write roundtrip sample records.
- Effort: 6–8h
- Dependencies: A3, B1

B4. Implement local repository adapters
- Goal: repository implementations wired to SQLDelight.
- Inputs: generated SQLDelight classes.
- Outputs: concrete repository classes (in `jvmMain` or `shared` as appropriate).
- Acceptance: repository integration tests pass (read/write/list/delete).
- Effort: 4–6h
- Dependencies: B3


Phase C — Use cases & tests (Goal: business operations + unit tests)

C1. Implement core use cases (CRUD)
- Goal: encapsulated business operations.
- Inputs: repository interfaces.
- Outputs: use-case classes (CreateCampaign, ListCampaigns, CreateEntry, etc.).
- Acceptance: unit tests cover happy paths and validation errors.
- Effort: 6–8h
- Dependencies: B2, B4

C2. Unit tests for domain & use-cases
- Goal: ensure domain correctness.
- Inputs: use-case implementations, test framework.
- Outputs: Kotest suites, mocks where needed.
- Acceptance: tests pass locally; aim for >=80% coverage in domain/use-cases.
- Effort: 6–8h
- Dependencies: C1


Phase D — Desktop UI (Goal: usable desktop app wired to shared logic)

D1. Design UI flows & wireframes
- Goal: minimal, clear navigation.
- Inputs: MVP feature list.
- Outputs: screen list and navigation map.
- Acceptance: agreed skeleton before coding.
- Effort: 2–3h
- Dependencies: C1

D2. Implement navigation skeleton & sidebar
- Goal: navigation between Campaigns, Characters, Journal.
- Inputs: wireframes.
- Outputs: Compose navigation and placeholder screens.
- Acceptance: can navigate between screens; placeholders visible.
- Effort: 4–6h
- Dependencies: D1

D3. Campaign screens (list/create/edit)
- Goal: full CRUD for campaigns via UI.
- Inputs: use-cases.
- Outputs: screens wired to use-cases.
- Acceptance: create/list/edit/delete persists and reflects in UI.
- Effort: 6–8h
- Dependencies: D2, C1

D4. Character screens (list/create/edit)
- Goal: CRUD for characters and linking to campaigns.
- Inputs: use-cases.
- Outputs: character UI screens.
- Acceptance: characters persist and display in campaign context.
- Effort: 6–8h
- Dependencies: D3

D5. Journal entry list & editor
- Goal: lightweight editor (plain text or markdown) for entries.
- Inputs: use-cases and UI patterns.
- Outputs: editor screen, save flow, list view.
- Acceptance: create/view/edit entries persisted and retrievable.
- Effort: 8–10h
- Dependencies: D3, D4


Phase E — Testing, polish & release prep

E1. Integration tests for repositories
- Goal: ensure DB adapters behave across lifecycle.
- Inputs: repository implementations.
- Outputs: integration test suite using temp DB files.
- Acceptance: integration tests pass.
- Effort: 4–6h
- Dependencies: B4

E2. Manual UI smoke tests & checklist
- Goal: basic app reliability.
- Inputs: running app, smoke checklist.
- Outputs: bug fixes and smoke test document.
- Acceptance: all smoke scenarios pass.
- Effort: 3–4h
- Dependencies: D5

E3. Seed data & export/import JSON
- Goal: onboarding and backups.
- Inputs: sample data JSON.
- Outputs: seed loader + export/import CLI or UI.
- Acceptance: user can import sample data and export their data.
- Effort: 4–6h
- Dependencies: storage stability

E4. Packaging & README
- Goal: runnable artifact and documentation.
- Inputs: packaging docs (compose desktop packaging or jlink alternatives).
- Outputs: packaged desktop artifact and README with run/backup steps.
- Acceptance: artifact runs on a target OS, README sufficient to get started.
- Effort: 3–5h
- Dependencies: all prior


Estimated MVP effort: ~70–100 hours (approx. 2–3 developer weeks full-time). Adjust for part-time.


## 6. Short milestone timeline (weeks)
- Week 1: Phase A + start Phase B (project setup, SQLDelight, models)
- Week 2: Finish Phase B + Phase C (repositories, use-cases, unit tests)
- Week 3: Phase D (UI skeleton, Campaign + Character screens)
- Week 4: Finish UI (Journal editor), Phase E start (integration tests, polish)
- Week 5: Polish, seed data, packaging, and release prep

Adjust targets based on availability; consider a slower cadence for part-time.


## 7. Quality gates checklist (what 'done' means)
- Build: root and `desktopApp` build succeed (`./gradlew build`) — PASS.
- Lint / Typecheck: no blocking compiler errors; integrate ktlint optionally — PASS.
- Unit tests: domain & use-case tests green; target >=80% logic coverage — PASS.
- Integration tests: storage read/write tests passing against temp DB — PASS.
- Smoke test: manual scenarios (launch, create campaign, create character, create entry, restart, verify persistence) — PASS.
- Release readiness: artifact runs on the target OS and README documents run & backup steps — PASS.


## 8. Risks & mitigations
- SQLDelight multiplatform quirks or plugin issues
    - Mitigation: start with JVM target for desktop; keep schema minimal and pin plugin versions.
- Compose desktop layout/platform differences
    - Mitigation: use responsive Compose patterns and keep UI components simple for MVP.
- Over-architecting DI/abstractions slows progress
    - Mitigation: manual wiring initially; extract DI when multiple implementations exist.
- Time underestimation
    - Mitigation: cut P2 features and focus on P1; maintain a buffer.


## 9. Low-risk adjacent improvements (implement now)
1. Add `pre-commit` formatting and lint checks (ktlint) — small value, low-risk.
2. Create a minimal README with run instructions and project overview — improves onboarding.
3. Add a `seeddata` JSON and a debug flag to load it on first run — speeds manual testing.


## 10. Recommended next action & 2-week actionable todo
Recommended next action: initialize modules and configure Compose + SQLDelight so builds are reproducible.

Two-week todo (concrete):
Week 1:
- Create modules and Gradle scaffolding for `shared` and `desktopApp` (update `settings.gradle.kts`) — 6h
- Add Compose Multiplatform desktop sample app and verify run — 4h
- Configure SQLDelight and add initial schema for `campaigns`, `characters`, `entries` — 4h
- Define domain models and repository interfaces — 6–8h

Week 2:
- Implement SQLDelight mappers and local repository implementations — 8h
- Implement core use-cases (CRUD) and write unit tests — 8h
- Start UI skeleton: navigation and campaign list placeholder — 6–8h

Acceptance after 2 weeks:
- Project builds, Compose app launches, DB schema generates, domain models + use-cases implemented with unit tests passing, and a basic campaign list screen wired to a stubbed or real data source.


---

If you want, I can now apply this updated `plan.md` into the repository (already done), open it for review, and/or create the initial Gradle/module scaffolding and Compose + SQLDelight config next. Which would you like me to do now?
