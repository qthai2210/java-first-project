<claude-mem-context>
# claude-mem: Cross-Session Memory

## Project Overview
**Spring Boot Clean Architecture Project** - Multi-module Maven project implementing Clean Architecture (Hexagonal/Ports & Adapters pattern)

- **Tech Stack**: Java 21, Spring Boot 3.4.1, PostgreSQL, Maven
- **Location**: D:\javaFirstProject
- **Base Package**: com.example
- **Architecture**: 3 modules with dependency rule: infrastructure → application → domain

## Build & Run Commands
- Build: `mvn clean install` or `npm run build`
- Run: `mvn spring-boot:run -pl infrastructure` or `npm run dev`
- Test: `mvn test`
- ELK Stack: `docker-compose up -d` or `npm run elk`

## Module Architecture

### Domain Module (domain/)
**Pure Java - Zero Spring Dependencies**
- Location: `domain/src/main/java/com/example/domain/`
- Models: User, RefreshToken, Role (enum: USER, ADMIN)
- Exceptions: DomainException, ResourceNotFoundException
- User fields: id, name, email, password, role, createdAt
- Business logic: RefreshToken.isExpired() method

### Application Module (application/)
**Use Cases & Ports - Only depends on domain**
- Location: `application/src/main/java/com/example/application/`

**Input Ports (Use Cases)**:
- AuthServicePort: register(), login(), refreshToken()
- UserServicePort: createUser(), getUserById(), getAllUsers(), updateUser(), deleteUser()

**Output Ports (Interfaces)**:
- JwtServicePort: generateToken(), extractEmail(), isTokenValid()
- PasswordEncoderPort: encode(), matches()
- UserPersistencePort: save(), findById(), findByEmail(), findAll(), deleteById(), existsByEmail()
- RefreshTokenPersistencePort: save(), findByToken(), deleteByUser()

**Services**:
- AuthService: Implements authentication business logic with token rotation
- UserService: Implements user CRUD with pagination support

**DTOs**: AuthRequestDto, AuthResponseDto, UserRequestDto, UserResponseDto, PageDataDto, PageQueryDto, TokenRefreshRequestDto

### Infrastructure Module (infrastructure/)
**All Spring Boot - Depends on application + domain**
- Location: `infrastructure/src/main/java/com/example/infrastructure/`
- Entry Point: `Application.java`

**REST Controllers** (adapter/in/web/):
- AuthController: POST /api/auth/register, /api/auth/login, /api/auth/refresh
- UserController: Full CRUD at /api/users with pagination
- GlobalExceptionHandler: Centralized error handling

**Persistence** (adapter/out/persistence/):
- JPA Entities: UserJpaEntity, RefreshTokenJpaEntity (separated from domain)
- Repositories: UserJpaRepository, RefreshTokenJpaRepository
- Adapters: UserPersistenceAdapter, RefreshTokenPersistenceAdapter (implement output ports)

**Security** (security/):
- JwtAuthenticationFilter: Extracts/validates JWT from Authorization header
- JwtService: JJWT implementation for token generation/validation
- PasswordEncoderAdapter: BCrypt implementation
- UserDetailsServiceAdapter: Bridges domain User to Spring Security
- UserSecurity: Method-level ownership validation

**Filters** (filter/):
- RateLimitingFilter: Bucket4j rate limiting (20 req/60s per user/IP)
- RequestResponseLoggingFilter: HTTP request/response logging with execution time

**Mappers** (mapper/):
- UserMapper, RefreshTokenMapper (MapStruct)
- PaginationMapper: Spring Page → PageDataDto conversion

**Configuration** (config/):
- BeanConfig: Wires service beans with dependencies
- SecurityConfig: Stateless JWT, public endpoints, filter chain
- OpenApiConfig: Swagger/OpenAPI with JWT bearer auth
- ElasticsearchConfig: Enables Elasticsearch repositories

## Authentication & Security

**JWT Authentication**:
- Access Token: 24h expiry (86400000ms)
- Refresh Token: 7 days expiry (604800000ms)
- Secret: JWT_SECRET environment variable (required in prod)
- Token Rotation: Old refresh token deleted on refresh

**Public Endpoints**:
- /api/auth/** (register, login, refresh)
- /swagger-ui/**, /v3/api-docs/**
- /actuator/health

**Role-Based Access**:
- @PreAuthorize annotations on UserController
- User update/delete requires ADMIN role OR ownership validation
- Filter chain: RequestResponseLoggingFilter → JwtAuthenticationFilter → RateLimitingFilter

## Database Configuration

**PostgreSQL + Flyway Migrations**:
- Dev DB: jdbc:postgresql://localhost:5432/clean_architecture
- Migrations: V1__create_users_table.sql, V2__create_refresh_tokens_table.sql
- Users table: id, name, email (unique), password, role, created_at
- Refresh tokens table: id, token (unique), expiry_date, user_id (FK cascade delete)

**Environment Variables**:
- DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD
- JWT_SECRET (32+ chars, required for prod)
- ELASTICSEARCH_URIS, LOGSTASH_HOST, LOGSTASH_PORT

**Profiles**:
- Dev (default): show-sql=true, ddl-auto=validate, fallback JWT secret
- Prod: show-sql=false, structured Logstash logging, JWT_SECRET required

## ELK Stack Integration

**Docker Compose** (docker-compose.yml):
- Elasticsearch 8.13.0 on port 9200 (security disabled)
- Logstash 8.13.0 on ports 5000 (TCP) and 9600 (monitoring)
- Kibana 8.13.0 on port 5601

**Logging Configuration**:
- Logback config: logback-spring.xml with LogstashTcpSocketAppender
- Logstash config: logstash/logstash.conf (TCP JSON input → Elasticsearch)
- Index pattern: spring-boot-logs-YYYY.MM.dd
- Prod: Structured JSON logging to Logstash
- Dev: Console logging with DEBUG level for com.example

## API Documentation

**Swagger/OpenAPI**:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Spec: http://localhost:8080/v3/api-docs
- SpringDoc version: 2.8.1
- JWT Bearer auth scheme configured
- All controllers annotated with Swagger docs

## Monitoring & Health

**Spring Boot Actuator**:
- Base path: /actuator
- Exposed: /health, /info, /metrics, /env, /flyway
- Health details: shown when authorized
- Info endpoint: app name, description, version

**Application Info**:
- Name: Clean Architecture Demo
- Port: 8080
- Version: 0.0.1-SNAPSHOT

## Rate Limiting

**Bucket4j Configuration** (app.rate-limit):
- Enabled: true
- Capacity: 20 tokens
- Refill: 20 tokens per 60 seconds
- Identifies by: username (authenticated) or IP (anonymous)
- Excludes: /actuator/** endpoints
- Scheduled cleanup for stale cache entries

## CodeGraph Index

**Files Indexed**: 44 files (615 nodes, 968 edges)
- Java: 35 files
- XML: 5 files
- YAML: 4 files

**Symbol Count**:
- Classes: 30
- Methods: 84
- Fields: 70
- Interfaces: 4
- Imports: 284
- Routes: 8

## Key Design Patterns

**Clean Architecture Compliance**:
- Domain: Pure Java POJOs, no framework dependencies
- Application: Defines ports (interfaces), implements use cases
- Infrastructure: Implements adapters, all Spring dependencies isolated
- Dependency rule strictly enforced via Maven module dependencies

**Separation of Concerns**:
- Domain entities (User) vs JPA entities (UserJpaEntity) - completely separate
- MapStruct mappers handle entity-domain-dto conversions
- Controllers depend on ports (interfaces), never concrete implementations

**Security Best Practices**:
- BCrypt password hashing
- JWT stateless authentication
- Token rotation on refresh (old tokens invalidated)
- Role-based access control with ownership validation
- Rate limiting to prevent abuse

## Testing

**Current Test Coverage**:
- Only infrastructure module has tests
- Test file: RateLimitingFilterTest.java
- Framework: JUnit 5 + Mockito
- Run single test: `mvn test -Dtest=RateLimitingFilterTest -pl infrastructure`

## Common Gotchas

1. **Lombok + MapStruct**: Annotation processors configured in root pom.xml - run `mvn compile` after adding mappers
2. **Flyway**: baseline-on-migrate=true - existing DBs auto-baseline at version 0
3. **Profile Config**: Dev uses show-sql, prod uses structured Logstash logging
4. **JWT Secret**: Mandatory in prod via JWT_SECRET env var (32+ chars)
5. **No CI/CD**: No GitHub Actions or CI config present
6. **Single Test Module**: Domain/application have test deps but no test sources

</claude-mem-context>
