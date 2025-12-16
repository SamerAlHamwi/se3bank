# SE3 Bank – Backend
mvn spring-boot:run
ollama pull llama3.1:8bollama pull llama3.1:8b
ollama pull qwen2.5-coder:1.5b-base
ollama pull nomic-embed-text:latest  
## Overview
Spring Boot 3 (Java 17) backend for a banking platform featuring accounts, transactions, notifications, interest strategies, and secure JWT + RBAC authentication.

## Features
- JWT authentication, RBAC, password hashing (BCrypt)
- Accounts CRUD, balance, transfers, transactions with approval chain
- Design patterns: Factory, Strategy, Decorator, Composite, Facade, Observer, Adapter
- Mock payment adapters (Stripe/PayPal)
- Swagger/OpenAPI 3 docs

## Architecture
- Layered: controller → service → repository → JPA entities
- Security: stateless JWT filter chain; /auth/** open, others protected
- Patterns implemented across modules (see “Design Patterns”)

## Design Patterns (7)
- Factory: `AccountFactory`
- Strategy: interest strategies via `InterestCalculator`
- Decorator: account feature decorators
- Composite: `AccountGroup`
- Facade: `BankFacade`
- Observer: notifications module
- Adapter: payment gateways (Stripe/PayPal)

## Security
- `SecurityConfig` with JWT filter, RBAC via roles
- `JwtUtil`, `JwtAuthenticationFilter`, `CustomUserDetailsService`
- `/auth/login`, `/auth/register`, `/auth/me`

## API Documentation
- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/api-docs`
- JWT header: `Authorization: Bearer <token>`

## Testing Strategy
- Unit tests: services, patterns, JWT, validation
- Integration tests: MockMvc for auth, accounts, transactions, payments, security paths
- Profiles: `test` uses H2 in-memory DB

## Running Locally
```bash
mvn spring-boot:run
# or
mvn -DskipTests package && java -jar target/se3bank-0.0.1-SNAPSHOT.jar
```

## Docker
```bash
docker build -t se3bank .
docker run -p 9090:9090 se3bank
```

## Future Improvements
- Add migration tooling (Flyway)
- Add performance metrics and tracing
- Expand payment providers and real integrations
- Increase integration test coverage for approval chain and notifications

