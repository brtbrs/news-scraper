# News Scraper Microservices

This repository is split into two services:

- `scraper-service`: CLI scraper job that fetches news and pushes results to API over HTTP.
- `api-service`: Spring Boot REST API for PostgreSQL persistence and article retrieval.

## Project structure

- `/scraper-service`
- `/api-service`
- `/docker-compose.yml`

## 1) Start API + PostgreSQL with Docker

From repository root:

```bash
docker compose up --build -d
```

API base URL will be available at:

- `http://localhost:8080/api/articles`

Stop services:

```bash
docker compose down
```

## 2) Run scraper service manually

From repository root:

```bash
cd scraper-service
mvn compile
API_BASE_URL=http://localhost:8080/api mvn exec:java -Dexec.args="--limit=5"
```

Environment variables:

- `API_BASE_URL` (default: `http://localhost:8080/api`)

## 3) Run scraper as cron job

Example cron entry (runs every hour):

```cron
0 * * * * cd /path/to/news-scraper/scraper-service && API_BASE_URL=http://localhost:8080/api /usr/bin/mvn -q exec:java -Dexec.args="--limit=5"
```

## Independent Maven builds

Each service can be built independently:

```bash
cd api-service && mvn clean package
cd scraper-service && mvn clean package
```
