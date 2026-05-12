# Project: mestemplate

## Purpose
`mestemplate` is a learning-first backend template for building a reusable MES platform.

This project is not a customer-specific SI codebase. It should be developed as a clean platform foundation that can later be adapted to different manufacturers with minimal source-code changes.

The developer is expected to write the code directly while using AI as a mentor, reviewer, and pair-programming assistant. Do not blindly paste large generated code. Prefer small, understandable steps.

## Development Mindset
- Build slowly and intentionally.
- Understand each layer before adding the next one.
- Keep the first version simple, but leave room for platform extension.
- Prefer a working vertical slice over a wide unfinished architecture.
- Ask AI to explain, review, compare alternatives, and generate small examples.
- After AI suggests code, read it line by line and adjust it to the project convention.
- Do not add abstractions only because they look professional.
- Add an abstraction when it protects a real boundary or removes repeated complexity.

## Target Baseline
- Java 17 or newer
- Spring Boot 3.x
- Gradle
- PostgreSQL
- Spring MVC REST API
- Spring Security (stateless JWT implemented; OAuth2/OIDC later if needed)
- JWT access token (implemented), refresh token not yet added
- Spring Data JPA / Hibernate
- QueryDSL for complex read queries
- Flyway or Liquibase for DB migration (not yet — must add before production)
- Lombok, used carefully
- Manual mappers only (MapStruct removed — see docs/ADR.md ADR-012)
- springdoc-openapi / Swagger
- Testcontainers for integration tests when possible
- Spring Boot Actuator for production readiness

## Architecture Direction
Start as a modular monolith.

Do not start with microservices. For an MES platform, module boundaries, data ownership, deployment needs, tenant isolation, and operational maturity must be clear before splitting services.

Recommended package flow for each feature:

```text
adapter/in/web
-> application
-> domain
-> application port
-> adapter/out/persistence
-> database
```

Recommended request flow:

```text
Controller
-> web dto (Request/Search)
-> application dto (Command/Query)
-> UseCase
-> Service
-> Domain
-> RepositoryPort
-> RepositoryAdapter
-> Mapper
-> Entity / Repository / QueryDSL
-> DB
```

Recommended response flow:

```text
DB
-> QueryDSL/JPA
-> application dto (Result)
-> web dto (Response)
-> ApiResponse
-> client
```

## Package Responsibilities
- `adapter/in/web`: HTTP API entry point. Routes, HTTP methods, request binding, response wrapping.
- `adapter/in/web/dto`: External API contract DTOs.
  - `Search`: GET query parameters.
  - `Request`: POST/PUT/PATCH request body.
  - `Response`: API response body.
- `application`: Use cases and business flow orchestration.
- `application/dto`: Internal use-case DTOs.
  - `Query`: read conditions.
  - `Command`: create/update/delete commands.
  - `Result`: internal read results.
- `domain`: Business objects, invariants, and state changes.
- `RepositoryPort`: Application-layer contract for persistence needs.
- `adapter/out/persistence`: Persistence adapter implementation.
- `mapper`: Domain/entity conversion.
- `entity`: JPA table mapping.
- `repository`: Spring Data JPA and QueryDSL repositories.

## First Implementation Goal
Build one complete vertical slice before creating many modules.

Recommended first slice:

```text
item master
```

Suggested flow:

```text
Create item
Update item
Get item by id
Search item list
Soft delete item
```

After the first slice is understood, repeat the same pattern for:

```text
material
inventory
work order
production result
quality inspection
equipment
lot tracking
```

## Platform Principles
- Do not hard-code customer-specific behavior into the core.
- Avoid customer branching such as `if (companyCode.equals("A"))`.
- Prefer configuration, feature flags, workflow definitions, strategy interfaces, or customer-specific adapters.
- Keep common MES concepts in the core:
  - tenant/company/factory/site
  - user/role/permission
  - item/material/BOM
  - inventory
  - production plan/work order/result
  - quality inspection
  - equipment downtime
  - mold management
  - lot tracking
  - file attachment
  - audit log
  - common code
  - i18n
- Treat customer-specific requirements as candidates for extension points, not immediate forks.
- Promote customization to platform capability only after the pattern appears repeatedly.

## Multi-Tenant Readiness
For an external MES platform, tenant isolation must be designed early.

Consider and document the chosen approach:

```text
shared DB with tenant_id
separate schema per tenant
separate DB per tenant
```

For the first learning version, prefer the simplest explicit model:

```text
tenant_id on business tables
```

Every external API and persistence query should eventually respect tenant boundaries. Object-level authorization and tenant data isolation are mandatory.

## Security Rules
- Never commit secrets, DB passwords, JWT secrets, or production credentials.
- Use environment variables, secret managers, or externalized configuration.
- Add Bean Validation to request DTOs.
- Prefer enum/value objects for status fields instead of raw strings when the state model stabilizes.
- Check object-level authorization for APIs that access data by ID.
- Keep CORS, cookie, JWT, and token expiration policies environment-specific.
- Follow OWASP API Security Top 10 as a baseline.

## API Rules
- Keep API versioning explicit, e.g. `/api/v1/...`.
- Use consistent response wrappers and error codes.
- Keep pagination and sorting rules consistent across modules.
- Do not expose JPA entities directly through controllers.
- Keep request DTOs separate from application commands, even if fields are currently similar.
- Document APIs with Swagger annotations when useful.
- Do not rely on Swagger as the only API contract.

## Persistence Rules
- Do not let application services depend directly on JPA repositories.
- Services should use repository ports.
- Persistence adapters should convert domain objects to JPA entities.
- Use QueryDSL for complex read queries.
- Use Flyway or Liquibase before treating this as a production platform.
- Design indexes deliberately for list/search APIs.
- Keep soft delete, audit fields, and tenant fields consistent.

## Testing Direction
Add tests in this order:

```text
domain tests
service tests
repository integration tests
API integration tests
authorization tests
tenant isolation tests
```

Use Testcontainers for PostgreSQL integration tests when possible.

For learning, write at least one test per layer for the first vertical slice.

## Observability And Operations
Before external service launch, add:

```text
Spring Boot Actuator health checks
structured JSON logs
request ID / trace ID propagation
metrics for HTTP, DB, JVM, and business events
error tracking
slow query monitoring
deployment profiles for local, staging, and production
CI/CD pipeline
Docker image build
DB migration step
rollback strategy
```

## AI Pair-Programming Rules
- For Codex sub-agent orchestration, use `docs/AI_ORCHESTRATION.md` as the operating guide.
- Ask AI for a plan before implementing unfamiliar layers.
- Ask AI to explain generated code in plain language.
- Ask AI to compare two approaches when unsure.
- Ask AI to review code for bugs, missing validation, security risks, and architectural drift.
- Keep prompts specific:
  - "Create only the domain object and tests."
  - "Review this controller for API design issues."
  - "Explain why RepositoryPort exists here."
- Avoid prompts that generate too much at once:
  - "Build the whole MES platform."
  - "Generate all modules."
- After AI changes code, run tests or at least compile.
- Never accept code that you cannot explain.

## Development Process
- Build one vertical slice end to end.
- Keep changes small and easy to review.
- Do not create many empty modules before the first feature works.
- Do not refactor unrelated code while implementing one feature.
- Prefer meaningful tests over broad mechanical coverage.
- Use conventional commits when committing:
  - `feat:`
  - `fix:`
  - `docs:`
  - `refactor:`
  - `test:`
  - `chore:`

## Suggested Learning Path
1. Project setup:
   - Gradle project structure.
   - Spring Boot application entry point.
   - `application.yml` and profiles.
   - PostgreSQL connection.
2. First vertical slice:
   - Controller.
   - web DTOs.
   - application DTOs.
   - UseCase.
   - Service.
   - Domain.
   - RepositoryPort.
   - RepositoryAdapter.
   - Mapper.
   - Entity.
   - Repository / QueryDSL.
3. Cross-cutting basics:
   - Common response format.
   - Exception handling.
   - Bean Validation.
   - Base entity fields.
   - Soft delete.
4. Platform basics:
   - Tenant model.
   - User/role/permission model.
   - Audit logging.
   - Common code.
   - Configurable fields.
5. Production readiness:
   - DB migration.
   - Tests.
   - Actuator.
   - Logging.
   - Docker.
   - CI/CD.

## Common Commands
Use the Gradle wrapper.

Windows:

```powershell
.\gradlew.bat clean build
.\gradlew.bat test
.\gradlew.bat bootRun
```

Unix-like shells:

```bash
./gradlew clean build
./gradlew test
./gradlew bootRun
```
