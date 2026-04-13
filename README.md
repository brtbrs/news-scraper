# News Scraper Microservices

This repository is split into two services:

- `scraper-service`: CLI scraper job that fetches news and pushes results to API over HTTP.
- `api-service`: Spring Boot REST API for PostgreSQL persistence and article retrieval.

## Project structure

- `/scraper-service`
- `/api-service`
- `/db`
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

## 4) Run scraper as cron job

Example cron entry (runs every hour):

```cron
0 * * * * cd /path/to/news-scraper && API_BASE_URL=http://localhost:8080/api /usr/bin/mvn -q -pl scraper-service exec:java -Dexec.args="--limit=5"
```

## Independent Maven builds

Each service can be built independently:

```bash
mvn -pl api-service clean package
mvn -pl scraper-service clean package
```
