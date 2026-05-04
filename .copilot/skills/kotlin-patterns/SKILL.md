---
name: kotlin-patterns
description: "Idiomatic Kotlin patterns: null safety, immutability, sealed types, structured concurrency, extension functions, DSL builders, Gradle Kotlin DSL. Accepts prompts in Norwegian and English. (Kotlin-mønstre, idiomatisk Kotlin, null-sikkerhet, sealed class, koroutiner, Gradle)"
---

# Kotlin patterns

Idiomatic conventions for this codebase. Detailed examples live in the sub-files — load them on demand.

## Quick reference

| Idiom | Use for |
|---|---|
| `val` over `var` | Default; prefer immutability |
| `data class` / `copy()` | Value objects with equals/hashCode and non-destructive updates |
| `sealed class/interface` | Restricted type hierarchies (state, results) |
| `value class` | Zero-overhead type-safe wrappers |
| Expression `when` | Exhaustive pattern matching |
| `?.` / `?:` / `requireNotNull` | Null-safe access; avoid `!!` without a justified check |
| `let` / `run` / `apply` / `also` / `with` | Scope functions for locality and readability |
| Extension functions | Add behaviour without inheritance |
| `require` / `check` | Preconditions for public API |
| `suspend` + `coroutineScope` / `async` | Structured concurrency |
| `Flow` | Cold reactive streams |
| `sequence` | Lazy evaluation over collections |
| Delegation `by` | Reuse without inheritance |

## Sub-files

- [core-patterns.md](core-patterns.md) — null safety, immutability, expression bodies, scope functions
- [type-modeling.md](type-modeling.md) — sealed types, extensions, delegation
- [coroutines.md](coroutines.md) — structured concurrency, Flow, cancellation
- [dsl-and-gradle.md](dsl-and-gradle.md) — DSL builders (`@DslMarker`), Gradle Kotlin DSL
- [error-handling.md](error-handling.md) — error patterns, collection operations, anti-patterns

## Boundaries

### ✅ Always
- Null safety via types (`?`, `?.`, `?:`)
- `sealed` for modeled state
- `suspend` + structured concurrency for async
- Run `ktlintFormat` before committing

### 🚫 Never
- `runBlocking` in production code
- `GlobalScope.launch`
- `!!` without a preceding null check
- Ignore coroutine cancellation
