---
applyTo: "**/test/**/*.kt"
---

# Testing essentials

Framework: **Kotest** (never JUnit) + **MockK**. Tester i dette prosjektet bruker `FunSpec`. Bruk `BehaviorSpec` (Given/When/Then/And) med norsk scenariotekst for komplekse integrasjonstester med mange kontekster.

For full patterns, examples, and MockK/matchers cheat sheets, invoke the **`kotest` skill**.

## Hard rules

- Mock HTTP clients for external service calls — never make real HTTP calls in tests.
- For suspend functions use `coEvery` / `coVerify`; never `runBlocking` inside test blocks.
- Load `PropertiesConfig` in `beforeSpec` when tests need config: `PropertiesConfig.load(ApplicationConfig("application-test.conf"))`

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
- Last `PropertiesConfig` i `beforeSpec` for tester som trenger konfig
- Kotest matchers (`shouldBe`, `shouldHaveSize`, `shouldBeEmpty`, `with { ... }`)

### 🚫 Never
- JUnit
- Real HTTP calls to external services in tests
- `runBlocking` inside test blocks
- Leak mutable state between scenarios
