---
applyTo: "**/test/**/*.kt"
---

# Testing essentials

Framework: **Kotest** (never JUnit) + **MockK**. Tester i dette prosjektet bruker `FunSpec`. Bruk `BehaviorSpec` (Given/When/Then/And) med norsk scenariotekst for komplekse integrasjonstester med mange kontekster.

For full patterns, examples, and MockK/matchers cheat sheets, invoke the **`kotest` skill**.

## Two API test patterns

This project uses two distinct test patterns:

| Pattern | Use for | Tool |
|---------|---------|------|
| `testApplication { }` | Security / JWT validation tests (`SecurityTest`) | ktor-server-test-host |
| `embeddedServer(Netty, PORT)` + RestAssured | API-logic tests with OpenAPI validation (`DummyApiTest`) | RestAssured + `OpenApiValidationFilter` |

For API tests: start/stop server in `beforeTest`/`afterTest`, deactivate auth hardcoded in `applicationTestModule()` with `authenticate(false, AUTHENTICATION_NAME)`.

## Hard rules

- Mock HTTP clients for external service calls — never make real HTTP calls in tests.
- For suspend functions use `coEvery` / `coVerify`; never `runBlocking` inside test blocks.
- `PropertiesConfig` is loaded globally once for the entire test suite via `ProjectConfig : AbstractProjectConfig` — no manual `beforeSpec` needed in individual test classes.

## Supplementary patterns (add when needed)

- **Database tests**: use a database test listener with TestContainers; clear state before loading fixtures in each `Given`.
- **SQL fixtures**: `src/test/resources/SQLscript/*.sql`

## File conventions

- Unit tests: `.../service/unit/*Test.kt`
- Integration tests: `.../service/integration/*IntegrationTest.kt`, `.../database/*Test.kt`

## Coverage audit — do this before drawing conclusions

Before claiming that an area lacks tests, **always run**:

```bash
find src/test -type f -name "*.kt" | sort
```

- Never use `| head` or limit output — test files live in subcategories (`unit/`, `integration/`, `database/`, `client/`, `config/`) and are easily truncated
- Evaluate actual file content, not just file names, before concluding on scenario coverage

## Boundaries

### ✅ Always
- `FunSpec` som standard; bruk `BehaviorSpec` for komplekse integrasjonstester
- `ProjectConfig : AbstractProjectConfig` laster `PropertiesConfig` globalt — ikke manuelt `beforeSpec` i testklasser
- Kotest matchers (`shouldBe`, `shouldHaveSize`, `shouldBeEmpty`, `with { ... }`)
- Bruk `OpenApiValidationFilter` i RestAssured-baserte API-tester for automatisk schema-validering

### 🚫 Never
- JUnit
- Real HTTP calls to external services in tests
- `runBlocking` inside test blocks
- Leak mutable state between scenarios
- `testApplication { }` for API-logikk-tester — bruk `embeddedServer(Netty)` + RestAssured
