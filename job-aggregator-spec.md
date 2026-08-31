# Job Aggregator — Project Definition & Requirements

## 1. Overview

A personal portfolio project: a web application that aggregates job listings
from multiple external sources into one unified, searchable interface —
similar in concept to Jooble, but built from scratch on legitimate,
publicly documented data sources.

**Purpose:** Portfolio project to demonstrate full-stack skills (API
integration, data normalization, backend, search, frontend) for job
applications as a web developer.

**Not a commercial product.** No scraping of sites that prohibit it in
their Terms of Service. All data comes from official or explicitly
public APIs.

---

## 2. Goals

- Aggregate job listings from 3 initial sources into one normalized dataset.
- Provide a searchable, filterable web interface (keyword, location, source).
- Deduplicate listings that appear in more than one source.
- Link every listing back to its original source (no hosting of full
  content beyond what's needed for search/display).
- Ship something demoable and deployable (portfolio-ready).

## 3. Non-Goals (out of scope for v1)

- No user accounts, login, or saved searches.
- No employer-facing features (posting jobs, managing applications).
- No email alerts / notifications.
- No scraping of sites whose ToS forbid it.
- No salary prediction or ML-based ranking (v1 uses simple relevance/date sort).

---

## 4. Data Sources (v1)

| Source | Type | Auth | Notes |
|---|---|---|---|
| Arbeitnow | Public JSON API | None required | `https://www.arbeitnow.com/api/job-board-api` |
| Adzuna | Official REST API | Free App ID + Key (register at developer.adzuna.com) | Rate-limited free tier |
| Bundesagentur für Arbeit | Public data via community-documented endpoint | Shared API key (`X-API-Key: jobboerse-jobsuche`) | Not an officially sanctioned third-party API — public government data, document this clearly in the README |

Each source is implemented as an independent **adapter module** that maps
raw API responses to one shared internal schema (see §6). New sources
should be addable by writing one new adapter, without touching the rest
of the system.

---

## 5. Functional Requirements

### FR-1 — Data Ingestion
- FR-1.1: System shall fetch job listings from Arbeitnow, Adzuna, and the
  Bundesagentur endpoint on a scheduled basis (e.g. every 6 hours).
- FR-1.2: Each source shall have its own adapter that converts raw API
  responses into the shared `NormalizedJob` schema.
- FR-1.3: Ingestion shall support pagination for sources that paginate
  results (Adzuna, Bundesagentur).
- FR-1.4: Ingestion failures for one source shall not block ingestion of
  other sources (isolated error handling per adapter).
- FR-1.5: Each ingestion run shall be logged (source, jobs fetched, jobs
  new/updated, errors).

### FR-2 — Data Normalization
- FR-2.1: All ingested jobs shall be mapped to a single schema: title,
  company, location, salary_min, salary_max, currency, source, external_id,
  url, published_at, description (optional).
- FR-2.2: Missing fields (e.g. no salary) shall be stored as null, not
  fabricated.
- FR-2.3: Location strings shall be normalized where feasible (e.g.
  consistent city naming) — best-effort, not a hard requirement for v1.

### FR-3 — Deduplication
- FR-3.1: System shall detect likely duplicate listings across sources
  using a similarity check on title + company + location.
- FR-3.2: Duplicates shall be merged into one record that references all
  contributing sources, or clearly flagged as related listings — pick one
  approach and document it.

### FR-4 — Storage
- FR-4.1: Normalized jobs shall be persisted in a relational database
  (PostgreSQL).
- FR-4.2: Schema shall support efficient lookup by source + external_id
  to avoid duplicate inserts on repeated ingestion runs.
- FR-4.3: Historical jobs older than a configurable threshold (e.g. 60
  days since last seen) may be archived or removed.

### FR-5 — Search & Filtering
- FR-5.1: Users shall be able to search jobs by free-text keyword
  (matches title, company, description).
- FR-5.2: Users shall be able to filter by location.
- FR-5.3: Users shall be able to filter by source.
- FR-5.4: Users shall be able to filter by salary range (where available).
- FR-5.5: Results shall be sortable by relevance and by publish date.
- FR-5.6: Results shall be paginated.

### FR-6 — API Layer
- FR-6.1: Backend shall expose a REST API for search/filter/list operations.
- FR-6.2: API responses shall include enough data for the frontend to
  render a result card and enough to link to the original listing.
- FR-6.3: API shall handle empty/invalid query parameters gracefully.

### FR-7 — Frontend
- FR-7.1: A search page with a keyword input and filter controls
  (location, source, salary range).
- FR-7.2: A results list showing title, company, location, salary (if
  known), source, and a link to the original posting.
- FR-7.3: Clicking a result opens the original source URL in a new tab —
  the app does not attempt to replicate the application/apply flow.
- FR-7.4: Basic responsive layout (desktop + mobile).

### FR-8 — Transparency / Compliance
- FR-8.1: Footer or About page shall disclose all data sources and link
  to their terms.
- FR-8.2: README shall clearly document that the Bundesagentur integration
  uses a community-documented, not officially sanctioned, endpoint.
- FR-8.3: No source's data shall be presented as if originating from this
  app — original source must always be visible per listing.

---

## 6. Data Model (shared schema)

```
NormalizedJob
- id                 (internal UUID)
- external_id         (string, source-specific ID)
- source              (enum: arbeitnow | adzuna | bundesagentur)
- title               (string)
- company             (string, nullable)
- location            (string, nullable)
- salary_min          (number, nullable)
- salary_max          (number, nullable)
- currency            (string, default "EUR")
- url                 (string)
- published_at        (timestamp, nullable)
- first_seen_at       (timestamp)
- last_seen_at        (timestamp)
- duplicate_group_id  (UUID, nullable — links merged duplicates)
```

---

## 7. Non-Functional Requirements

- NFR-1: Ingestion jobs must not exceed each source's documented/implied
  rate limits.
- NFR-2: Search response time should be under ~500ms for typical queries
  on a dataset of tens of thousands of jobs.
- NFR-3: System should run on a single small VM or free-tier
  cloud deployment (portfolio-scale, not high-traffic production).
- NFR-4: Codebase should be modular enough that a new source adapter can
  be added without modifying ingestion, storage, or API layers.
- NFR-5: Secrets (Adzuna App ID/Key) must be stored in environment
  variables, never committed to source control.

---

## 8. Tech Stack

- **Backend:** Java 17+ with Spring Boot
  - **Spring Web (MVC)** for the REST API layer
  - **Spring Data JPA** + **PostgreSQL** for persistence
  - **Spring WebClient** (or `RestTemplate`) for calling the three external
    job APIs
  - **Spring Scheduler** (`@Scheduled`) for periodic ingestion jobs
  - **Jackson** for JSON mapping; per-source DTO classes (or a tree-model
    fallback for messier/nested responses like Bundesagentur's `arbeitsort`)
  - Each source adapter implemented as a `@Service` behind a shared
    `JobSourceAdapter` interface (e.g. `fetchJobs()` returning
    `List<NormalizedJob>`), injected into a central `IngestionService`
  - **Bean Validation (`jakarta.validation`)** for request/DTO validation
- **Database:** PostgreSQL
- **Search:** Postgres full-text search for v1 (simpler); **Spring Data
  Elasticsearch** as a later upgrade path if search quality/performance
  requires it
- **Frontend:** React or Next.js (separate from the backend, calling the
  Spring Boot REST API)
- **Deployment:** Render / Railway / Fly.io (free/low-cost tiers) or similar
  — watch cold-start times on free tiers with a JVM app

### 8.1 — Learning goal note
This project is also being used to build hands-on Spring Boot experience.
Where reasonable, prefer idiomatic Spring patterns over the shortest path
(e.g. proper layering into Controller → Service → Repository, interface-based
adapters, constructor injection) even if a shortcut would be faster —
the practice is part of the point.

---

## 9. Suggested Build Phases

1. **Phase 0 — Project scaffold:** Spring Boot project (Spring Initializr)
   with Web, Data JPA, PostgreSQL driver, Validation; base package structure
   (`controller`, `service`, `repository`, `adapter`, `model`/`entity`, `dto`).
2. **Phase 1 — Ingestion core:** Define `JobSourceAdapter` interface;
   implement one `@Service` adapter per source (Arbeitnow, Adzuna,
   Bundesagentur) mapping to the shared `NormalizedJob` entity; persist via
   Spring Data JPA repository.
3. **Phase 2 — Deduplication:** Implement duplicate detection across sources.
4. **Phase 3 — API:** Build REST controllers for search/filter/list
   (`@RestController`, pageable results via Spring Data `Pageable`).
5. **Phase 4 — Frontend:** Build search UI (React/Next.js) consuming the
   Spring Boot API.
6. **Phase 5 — Scheduling & polish:** Add `@Scheduled` ingestion jobs,
   logging (SLF4J), README/documentation, deploy.

---

## 10. Open Questions (to resolve before/during build)

- Exact deduplication strategy: fuzzy string matching threshold, or
  simpler exact-match on normalized title+company+location?
- Should archived/expired jobs be deleted or just hidden from search?
- Any need for a minimal admin view (see ingestion logs, error status)?
