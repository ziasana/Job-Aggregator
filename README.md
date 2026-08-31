# Job Aggregator

A portfolio project that aggregates job listings from multiple public/official
job-board APIs into one normalized dataset. See
[`job-aggregator-spec.md`](job-aggregator-spec.md) for the full requirements.

This repository currently covers **Phase 0 (project scaffold)** and
**Phase 1 (ingestion core)**: pulling jobs from each source, normalizing them
into a shared schema, and persisting them to Postgres. There is no search API
or frontend yet.

## Data sources

| Source | Type | Auth |
|---|---|---|
| [Arbeitnow](https://www.arbeitnow.com/api/job-board-api) | Public JSON API | None |
| [Adzuna](https://developer.adzuna.com/) | Official REST API | Free App ID + Key |
| [Bundesagentur fuer Arbeit Jobsuche](https://rest.arbeitsagentur.de/jobboerse/jobsuche-service/pc/v4/jobs) | Community-documented endpoint | Shared key (`X-API-Key: jobboerse-jobsuche`) |

**Important:** the Bundesagentur integration uses a **community-documented,
not officially sanctioned** third-party endpoint for public government job
data. It is not a published/supported Bundesagentur API, and its shape or
availability may change without notice — as observed during development,
where it can return `403 Forbidden` for reasons undocumented by the community
source. Treat it as best-effort.

Every listing returned by the app links back to its original source; this
project does not host or reproduce full third-party content.

## Requirements

- Java 17+
- Docker (for local Postgres)

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
   runner of your choice), then run the app:
   ```
   export $(grep -v '^#' .env | xargs)
   ./mvnw spring-boot:run
   ```

Without Adzuna/Bundesagentur credentials, those adapters log a warning and
are skipped — Arbeitnow ingestion works with no configuration.

## Architecture notes

- Each source is an independent `JobSourceAdapter` implementation
  (`adapter/`), so adding a new source means writing one new adapter without
  touching ingestion, storage, or (future) API/search layers.
- `IngestionService` runs each adapter in isolation — one source failing
  (rate limit, bad credentials, API shape change) does not block the others.
- Jobs are upserted by `(source, external_id)`, so re-running ingestion
  updates existing rows instead of creating duplicates.
- Duplicate detection *across* sources (same job posted on multiple boards)
  is not yet implemented — that's Phase 2.

## Tests

```
./mvnw test
```

Adapter tests run against fixture JSON in `src/test/resources/fixtures/` and
don't require network access or credentials. The full Spring context test
(`JobAggregatorApplicationTests`) does require a running Postgres (see
"Running locally" above).
