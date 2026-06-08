# Spring Boot Clean Architecture - Agent Instructions

## Project Overview
Multi-module Maven project (Java 21, Spring Boot 3.4.1) implementing Clean Architecture with three layers:
- **domain** - Pure Java business logic, zero Spring dependencies
- **application** - Use cases & ports (interfaces), depends only on domain
- **infrastructure** - Spring Boot, JPA, REST, security, configs; depends on application + domain

**Dependency Rule**: `infrastructure → application → domain` (inner layers never depend on outer)

## Key Commands

| Task | Command |
|------|---------|
| Build all modules | `mvn clean install` |
| Build (skip tests) | `mvn clean install -DskipTests` or `npm run build` |
| Run application | `mvn spring-boot:run -pl infrastructure` or `npm run dev` |
| Run tests | `mvn test` (from root runs all modules) |
| Start ELK stack | `docker-compose up -d` or `npm run elk` |
| Stop ELK stack | `docker-compose down` or `npm run elk:down` |

## Architecture Constraints
- **domain**: No Spring, no framework annotations. Pure POJOs + Lombok.
- **application**: Only `spring-boot-starter-validation` + domain. Defines Input/Output ports.
- **infrastructure**: All Spring Boot starters, JPA, Security, MapStruct, Flyway, Bucket4j, ELK logging.

## Module Entry Points
- **App entry**: `infrastructure/src/main/java/com/example/infrastructure/Application.java`
- **Config**: `infrastructure/src/main/resources/application.yml` (base), `application-dev.yml`, `application-prod.yml`
- **DB Migrations**: `infrastructure/src/main/resources/db/migration/V*.sql` (Flyway)

## Environment Setup
- Copy `.env.example` → `.env` and configure:
  - `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` (PostgreSQL)
  - `JWT_SECRET` (32+ chars, required for prod)
  - `ELASTICSEARCH_URIS`, `LOGSTASH_HOST`, `LOGSTASH_PORT` (ELK)
- Dev profile active by default (`spring.profiles.active=dev`)

## Testing
- **Framework**: JUnit 5 + Mockito
- **Test location**: `infrastructure/src/test/...` (only infrastructure has tests currently)
- Run single test: `mvn test -Dtest=RateLimitingFilterTest -pl infrastructure`

## API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI spec: `http://localhost:8080/v3/api-docs`

## Health & Monitoring
- Actuator endpoints: `/actuator/health`, `/actuator/info`, `/actuator/metrics`, `/actuator/env`, `/actuator/flyway`
- Structured JSON logging to Logstash (prod), console (dev)

## Rate Limiting
- Bucket4j filter at `RateLimitingFilter.java`
- Config in `application.yml` under `app.rate-limit`
- Excludes `/actuator/**` endpoints

## JWT Auth
- Access token: 24h expiry (`app.jwt.expiration-ms`)
- Refresh token: 7 days (`app.jwt.refresh-expiration-ms`)
- Secret via `JWT_SECRET` env var (mandatory in prod)

## Common Gotchas
1. **Lombok + MapStruct**: Annotation processors configured in root `pom.xml` - run `mvn compile` after adding new mappers
2. **Flyway**: `baseline-on-migrate=true` - existing DBs need baseline version 0
3. **Profile-specific config**: Dev uses `ddl-auto: validate`, `show-sql: true`; Prod uses structured Logstash logging
4. **No CI/CD**: No GitHub Actions or CI config present
5. **Single test module**: Only infrastructure has tests; domain/application have test deps but no test sources