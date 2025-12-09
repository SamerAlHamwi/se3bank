# Design Rationale

## Pattern Choices
- **Factory (`AccountFactory`)**: decouples account creation from controllers, ensures consistent defaults and validation.
- **Strategy (Interest)**: multiple interest calculations per account type, runtime switching via `InterestCalculator`.
- **Decorator (Account Features)**: adds behaviors (insurance, overdraft, premium) without changing core account class.
- **Composite (`AccountGroup`)**: groups accounts and aggregates balances; supports group-level ops.
- **Facade (`BankFacade`)**: single entry-point to orchestrate complex flows.
- **Observer (Notifications)**: async notification pipeline for account/transaction events.
- **Adapter (Payments)**: unified interface to external gateways; mocked Stripe/PayPal adapters.

## Architecture & Trade-offs
- Layered services keep controllers thin and reuse business logic.
- Stateless JWT + RBAC chosen for scalability and SPA/mobile support; trade-off: tokens must be refreshed client-side.
- JPA with PostgreSQL in prod, H2 for tests; trade-off: native queries may differ slightly, mitigated with PostgreSQL mode in H2.
- Mock payment adapters simplify development while preserving integration points.

## Fit to Requirements
- Banking domain needs auditable, isolated services; patterns improve clarity:
  - Factory/Strategy keep account and interest rules testable.
  - Composite/Decorator provide flexible account grouping/features.
  - Adapter prepares future real payment providers without core changes.
  - Facade centralizes orchestrations for higher-level flows.

## Testing Approach
- Unit tests with Mockito for services and utilities.
- Integration tests with MockMvc + H2 to exercise security and controllers.
- Validation tests ensure DTO constraints block bad input early.

## Security
- BCrypt for passwords; JWT with custom filter; RBAC via `@PreAuthorize`.
- Strictly open `/auth/**`, all other routes protected; Swagger secured but whitelisted for docs.

## Future Trade-offs
- Could introduce CQRS/event sourcing for audit-heavy flows.
- Add rate limiting and refresh tokens for stronger session control.

