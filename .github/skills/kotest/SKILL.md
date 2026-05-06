---
name: kotest
description: "Kotest BehaviorSpec and MockK patterns for sokos Ktor services: scenario structure, test listeners, HTTP mocking, circuit-breaker reset, and matchers. Accepts prompts in Norwegian and English. (Testmønstre, teststruktur, integrasjonstester, enhetstester, MockK)"
---

# Kotest patterns

## Coverage audit — ALWAYS do this first

Before concluding that an area lacks tests, **always list all test files**:

```bash
find src/test -type f -name "*.kt" | sort
```

Tests may be organized in subcategories — never use `| head` which truncates output.

Do not assume missing coverage without seeing the full file list first.

---

Default spec style: **`BehaviorSpec`** (Given/When/Then/And) with Norwegian scenario text. Use `FunSpec` only for trivial, non-scenario-based unit tests.

## Sub-files

- [behaviorspec-patterns.md](behaviorspec-patterns.md) — canonical BehaviorSpec structure, conventions, and Norwegian scenario text
- [integration-testing.md](integration-testing.md) — database test listeners, SFTP listeners, mock HTTP clients, and circuit breaker reset
- [mockk-assertions.md](mockk-assertions.md) — MockK cheat sheet, Kotest matchers, `with { ... }` grouping

## Boundaries

### ✅ Always
- `testApplication { }` exclusively for testing API endpoints (routes) — never for services, repositories, or other non-HTTP logic
- `BehaviorSpec` as default; Norwegian scenario text
- Reset circuit breaker in `beforeEach` when tests reach an external HTTP client
- Clear database state before loading fixtures in each `Given`
- Mock HTTP clients for external service calls — never make real HTTP calls in tests
- Kotest matchers (`shouldBe`, `shouldHaveSize`, …)
- `coEvery` / `coVerify` for suspend functions

### ⚠️ Ask first
- New global test listeners or base specs
- Tests requiring real network or real SFTP

### 🚫 Never
- `testApplication { }` for anything other than API endpoint tests (use direct class instantiation + mocks for services, repositories, etc.)
- JUnit
- `runBlocking { ... }` in test blocks
- Real HTTP calls to external services
- Share mutable state between scenarios without explicit reset
