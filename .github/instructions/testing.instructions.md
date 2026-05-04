---
applyTo: "**/test/**/*.kt"
---

# Testing essentials

Framework: **Kotest** (never JUnit) + **MockK**. Default spec style is `BehaviorSpec` (Given/When/Then/And) with Norwegian scenario text — both for unit and integration tests. Use `FunSpec` only for trivial, purely technical unit tests.

For full patterns, examples, and MockK/matchers cheat sheets, invoke the **`kotest` skill**.

## Hard rules

- Integration tests with DB → use a database test listener with TestContainers; clear state before loading fixtures in each `Given`.
- Integration tests with SFTP → use an SFTP test listener.
- Any test that reaches an external HTTP client with a circuit breaker → `beforeEach { CircuitBreakerManager.circuitBreaker.reset() }`.
- Mock HTTP clients for external service calls — never make real HTTP calls in tests.
- For suspend functions use `coEvery` / `coVerify`; never `runBlocking` inside test blocks.

## File conventions

- Unit tests: `.../service/unit/*Test.kt`
- Integration tests: `.../service/integration/*IntegrationTest.kt`, `.../database/*Test.kt`
- SQL fixtures: `src/test/resources/SQLscript/*.sql`

## Coverage audit — do this before drawing conclusions

Before claiming that an area lacks tests, **always run**:

```bash
find src/test -type f -name "*.kt" | sort
```

- Never use `| head` or limit output — test files live in subcategories (`unit/`, `integration/`, `database/`, `client/`, `config/`) and are easily truncated
- Evaluate actual file content, not just file names, before concluding on scenario coverage

## Boundaries

### ✅ Always
- `BehaviorSpec` as default; Norwegian Given/When/Then text
- Reset circuit breaker in `beforeEach` for tests reaching external services
- Kotest matchers (`shouldBe`, `shouldHaveSize`, `shouldBeEmpty`, `with { ... }`)

### 🚫 Never
- JUnit
- Real HTTP calls to external services in tests
- `runBlocking` inside test blocks
- Leak mutable state between scenarios
