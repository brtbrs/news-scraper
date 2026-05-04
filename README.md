# News Scraper Microservices

This repository is split into three services:

- `scraper-service`: CLI scraper job that fetches news and pushes results to API over HTTP.
- `api-service`: Spring Boot REST API for PostgreSQL persistence and article retrieval.
- `tagger-service`: CLI tagging job that tags content by source/date and updates content status.

## Project structure

- `/scraper-service`
- `/api-service`
- `/db`
- `/tagger-service`
- `/docker-compose.yml`

## 1) Run `api-service` + PostgreSQL with Docker

From repository root:

```bash
docker compose up --build -d
```

API base URL:

- `http://localhost:8080/api/articles`

> Note: this path is convenient for full-stack/local integration, but code changes in `api-service` require rebuilding the image.


Database security note for `api-service`:
- By default, `api-service` now uses a dedicated database login (`api_service_user`) with limited privileges (no role creation, no schema ownership changes).
- Configure credentials with `API_DB_USER` and `API_DB_PASSWORD` (or `DB_USER` / `DB_PASSWORD` for Spring override).
- The role is created from `db/01-api-service-user.sql` during first PostgreSQL initialization; if `postgres_data` already exists, recreate it to apply initialization scripts.

Stop services:

```bash
docker compose down
```

## 2) Run `api-service` with Maven (recommended for development speed)

Start only PostgreSQL with Docker:

```bash
docker compose up -d db
```

Then run API from source:

```bash
mvn -pl api-service spring-boot:run
```

> This mode is faster for development because Java code changes are applied without rebuilding Docker images.

## 3) Run `scraper-service` manually

From repository root:

```bash
mvn -pl scraper-service compile
API_BASE_URL=http://localhost:8080/api mvn -pl scraper-service exec:java -Dexec.args="--limit=0 --source=ALL --from=WEBSITE"
API_BASE_URL=http://localhost:8080/api mvn -pl scraper-service exec:java -Dexec.args="--limit=5 --source=STOCKWATCH --from=SITEMAP"
```

Environment variables:

- `API_BASE_URL` (default: `http://localhost:8080/api`)
- `TAGGER_TIMEZONE` / scraper publish-date handling uses `Asia/Jakarta` offset format (example: `2026-04-28T10:15:00+07:00`)

## 4) Run `tagger-service` manually

From repository root:

```bash
mvn -pl tagger-service compile
TAGGER_TIMEZONE=Asia/Jakarta API_BASE_URL=http://localhost:8080/api mvn -pl tagger-service exec:java -Dexec.mainClass=id.ihaesge.tagger.app.Main -Dexec.args="--source=BISNIS --from=2026-04-01 --to=2026-04-15"
```

## 5) Run scraper as cron job

Example cron entry (runs every hour):

```cron
0 * * * * cd /path/to/news-scraper && API_BASE_URL=http://localhost:8080/api /usr/bin/mvn -q -pl scraper-service exec:java -Dexec.args="--limit=0 --source=ALL --from=WEBSITE"
```

## Independent Maven builds

Each service can be built independently:

```bash
mvn -pl api-service clean package
mvn -pl scraper-service clean package
mvn -pl tagger-service clean package
```

## Content lifecycle

Content moves through these statuses:

`PENDING -> TAGGED / UNTAGGED -> DEDUPED -> TRANSLATED -> SUMMARIZED -> LABELED`

- `PENDING`: newly ingested from scraper.
- `TAGGED`: one or more tags were matched from `tag_alias.alias` and saved to `content_tag.tag`.
- `UNTAGGED`: no aliases matched.
- `DEDUPED`: duplicate check completed.
- `TRANSLATED`: translation completed.
- `SUMMARIZED`: summarization completed.
- `LABELED`: final labeling completed.

## Stored function for Summarize + Deduplicate input preparation

Use this SQL function to fetch TAGGED content rows for one stock ticker within a Jakarta-time window.

```sql
SELECT *
FROM get_tagged_content_by_ticker_and_window(
    'BBCA',
    '2026-04-28T00:00:00+07:00',
    '2026-04-28T23:59:59+07:00'
);
```

This replaces direct ad-hoc joins like:

```sql
-- old pattern
-- select c.url, c.original_publish_date, a.code ...
```
