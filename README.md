# Job Aggregator

A portfolio project that aggregates job listings from multiple public/official
job-board APIs into one normalized dataset. See
[`job-aggregator-spec.md`](job-aggregator-spec.md) for the full requirements.

This repository currently covers **Phase 0-4**: ingestion from each source,
cross-source duplicate detection, a search/filter REST API, and a Next.js
frontend consuming that API. No scheduling yet (Phase 5).

## Data sources

| Source | Type | Auth |
|---|---|---|
| [Arbeitnow](https://www.arbeitnow.com/api/job-board-api) | Public JSON API | None |
| [Adzuna](https://developer.adzuna.com/) | Official REST API | Free App ID + Key |
| [Bundesagentur fuer Arbeit Jobsuche](https://rest.arbeitsagentur.de/jobboerse/jobsuche-service/pc/v6/jobs) | Community-documented endpoint | Shared key (`X-API-Key: jobboerse-jobsuche`) |
| [Jobicy](https://jobicy.com/api/v2/remote-jobs) | Public JSON API | None |

**Important:** the Bundesagentur integration uses a **community-documented,
not officially sanctioned** third-party endpoint for public government job
data. It is not a published/supported Bundesagentur API, and its shape or
availability can change without notice: during development, the originally
documented `pc/v4/jobs` path started returning `403 Forbidden` on every
request (key included) with no explanation. The fix was moving to
`pc/v6/jobs`, which is also 1-indexed for pagination and uses a different
response shape (`ergebnisliste` instead of `stellenangebote`, German field
names throughout) — a reminder that this integration can break again the
same way with no warning. A small number of individual listings also lack a
title in this API (private-employer postings, it seems); those are skipped
rather than inserted with a fabricated title, per FR-2.2.

Unlike the other three, Jobicy is a global remote-jobs feed, not
Germany-specific — its listings' `location` is typically a region
(`"USA"`, `"Europe"`) rather than a city. It's free with no API key and no
signup; its own usage notice just asks that results credit Jobicy and link
directly to the original job URL, which is exactly how every source here is
already displayed. It also isn't paginated the way the others are — it caps
at 200 results per request, so one ingestion request per run is all there
is.

Every listing returned by the app links back to its original source; this
project does not host or reproduce full third-party content.

## Requirements

- Java 17+
- Docker (for local Postgres)
- Node.js 20+ (for the frontend)

Maven itself does not need to be installed — use the bundled wrapper
(`./mvnw`).

## Running locally

1. Copy `.env.example` to `.env` and fill in credentials (Arbeitnow needs
   none; Adzuna requires a free App ID/Key from developer.adzuna.com).
2. Start Postgres:
   ```
   docker compose up -d
   ```
3. Export the env vars from `.env` into your shell (or use an env-file
   runner of your choice), then run the backend:
   ```
   export $(grep -v '^#' .env | xargs)
   ./mvnw spring-boot:run
   ```
4. In a second terminal, run the frontend:
   ```
   cd frontend
   npm install
   npm run dev
   ```
   Then open http://localhost:3000. It expects the backend on
   `http://localhost:8080` by default (see `frontend/.env.example` to
   override via `API_BASE_URL`).

Without Adzuna/Bundesagentur credentials, those adapters log a warning and
are skipped — Arbeitnow ingestion works with no configuration.

## Architecture notes

- Each source is an independent `JobSourceAdapter` implementation
  (`adapter/`), so adding a new source means writing one new adapter without
  touching ingestion, storage, or (future) API/search layers — confirmed in
  practice when Jobicy was added as a fourth source.
- **Gotcha when adding a new `JobSource` enum value to an existing database:**
  Hibernate auto-generates a `CHECK` constraint enumerating the enum's known
  values when it first creates the `normalized_job` table, and
  `ddl-auto: update` does not widen that constraint when the enum gains a
  new constant — inserts for the new source fail with a constraint
  violation until the constraint is dropped and recreated manually (or the
  dev database is reset). One more reminder that `ddl-auto: update` is a
  local-dev convenience, not a migration tool.
- `IngestionService` runs each adapter in isolation — one source failing
  (rate limit, bad credentials, API shape change) does not block the others.
- Jobs are upserted by `(source, external_id)`, so re-running ingestion
  updates existing rows instead of creating duplicates.

### Deduplication (FR-3)

`DuplicateDetectionService` runs after every ingestion. Chosen approach:
**flag, don't merge** — every source's row is kept (so it stays individually
attributable and linkable), and likely duplicates across *different* sources
are flagged by sharing a `duplicate_group_id`. Candidates are blocked by
normalized `(company, location)`, then compared pairwise within a block using
Jaro-Winkler similarity on the normalized title (threshold configurable via
`job-aggregator.dedup.title-similarity-threshold`, default `0.90`).

### Search API (FR-5, FR-6)

```
GET /api/jobs?q=backend&location=Berlin&source=arbeitnow&salaryMin=40000&salaryMax=70000&sortBy=relevance&page=0&size=20
```

All filter params are optional and parsed leniently — an unknown `source` or
unparseable salary is ignored rather than rejected (FR-6.3), so a typo just
falls back to unfiltered results. `sortBy` is `relevance` (default when `q`
is present) or `date` (default otherwise); it's deliberately not called
`sort` to avoid colliding with Spring Data's own `page`/`size`/`sort` binding
on the endpoint's `Pageable` parameter, which is used only for paging here.

Search uses Postgres full-text search (`to_tsvector`/`ts_rank`) over title,
company, and description, as called for in the spec's tech stack — no
Elasticsearch. Duplicate groups are collapsed to one representative row (the
most recently seen) before filtering/paging; each result's `sources` field
lists every source that contributed to it.

### Frontend (FR-7, FR-8)

`frontend/` is a Next.js (App Router, TypeScript, Tailwind) app. The search
page is a **Server Component only** — it reads the URL's `searchParams` and
fetches `GET /api/jobs` directly, server-to-server, on every request
(`cache: "no-store"`). The filter form and pagination/sort links are plain
HTML `<form>`/`<a>` elements (not `next/form`/`next/link`), which means:

- Every interaction is a real browser navigation — no client-side JS, no
  React state, and no CORS configuration needed on the backend, since the
  browser never calls port 8080 directly.
- This was a deliberate fix, not just a simplicity choice: `next/form`'s and
  `next/link`'s client-side "soft navigation" served a stale cached result
  when only search params changed (confirmed the *server* rendered correctly
  via direct `curl`, but the browser's router cache did not refetch) —
  switching to plain `<form>`/`<a>` sidesteps that class of bug entirely.

The footer (rendered on every page) discloses all four data sources with
links, and explicitly calls out the Bundesagentur integration as
community-documented/unofficial and Jobicy as global/remote-only
(FR-8.1, FR-8.2, FR-8.3).

## Tests

```
./mvnw test
```

Adapter tests run against fixture JSON in `src/test/resources/fixtures/` and
don't require network access or credentials. The full Spring context test
(`JobAggregatorApplicationTests`) and the search repository tests do require
a running Postgres (see "Running locally" above).

The frontend has no automated tests (thin presentation layer over an
already-tested backend endpoint, with no client-side logic); it was verified
manually in a browser — keyword/location/source/salary filtering, pagination
preserving active filters, mobile layout, and the backend-down error state.
