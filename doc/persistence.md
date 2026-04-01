# Persistence framework recommendation

Recommended framework: **Spring Data JPA (Hibernate as the JPA provider) with Spring Data REST**.

Why:
- Widely used and standard for Java + PostgreSQL projects.
- Easy to maintain (declarative entities/repositories, less boilerplate).
- Good performance baseline and mature optimization options.
- Fast CRUD delivery: repository interfaces automatically expose REST endpoints.

## Generated CRUD endpoints
With current configuration, repositories are exposed under `/api`:

- `/api/attribute`
- `/api/sectorindustry`
- `/api/source`
- `/api/stock`
- `/api/stockalias`
- `/api/corporateevent`
- `/api/news`
- `/api/newsstock`
- `/api/audio`
- `/api/appuser`
- `/api/userprofile`
- `/api/watchlist`
- `/api/activitylog`
- `/api/pipelinelog`

Each endpoint supports standard CRUD with Spring Data REST (`GET`, `POST`, `PUT`/`PATCH`, `DELETE`).
