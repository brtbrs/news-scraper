# Persistence architecture

This project uses **Spring Data JPA (Hibernate)** for ORM and **Spring Data REST** to auto-publish CRUD APIs.

## Schema update: `content_ai`

AI-enriched fields were moved out of `content` into a dedicated `content_ai` table.

- `content` now stores raw/source article fields (`source`, `type`, `url`, original title/content/language/date, `status`).
- `content_ai` stores derived fields (`title_id`, `title_en`, `content_id`, `content_en`, `sentiment`, `duplicate`, `publish_date`).
- Relation is **1:1** via `content_ai.content` (unique FK to `content.id`).

This separation keeps ingestion data independent from enrichment/AI pipeline output.

## API style

### 1) Ingestion API (custom controller)
- `POST /api/ingest/contents`
- `GET /api/ingest/contents`
- `GET /api/ingest/contents/{id}`

### 2) Auto CRUD API (Spring Data REST)
Base path: `/api`

- `/api/attributes`
- `/api/sources`
- `/api/contents`
- `/api/content-ai`
- `/api/sectors`
- `/api/industries`
- `/api/sub-industries`
- `/api/stocks`
- `/api/stock-aliases`
- `/api/corporate-events`
- `/api/content-tags`
- `/api/audios`
- `/api/app-users`
- `/api/user-profiles`
- `/api/watchlists`
- `/api/activity-logs`
- `/api/pipeline-logs`

Each Spring Data REST resource supports standard CRUD (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`) and pagination/filter conventions.
