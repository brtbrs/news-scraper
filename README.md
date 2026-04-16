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
API_BASE_URL=http://localhost:8080/api FROM_SITE_MAP=FALSE mvn -pl scraper-service exec:java -Dexec.args="--limit=5"
```

Environment variables:

- `API_BASE_URL` (default: `http://localhost:8080/api`)

## 4) Run `tagger-service` manually

From repository root:

```bash
mvn -pl tagger-service compile
API_BASE_URL=http://localhost:8080/api mvn -pl tagger-service exec:java -Dexec.mainClass=id.ihaesge.tagger.app.Main -Dexec.args="--source=Bisnis --from=2026-04-01 --to=2026-04-15"
```

## 5) Run scraper as cron job

Example cron entry (runs every hour):

```cron
0 * * * * cd /path/to/news-scraper && API_BASE_URL=http://localhost:8080/api /usr/bin/mvn -q -pl scraper-service exec:java -Dexec.args="--limit=5"
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
