# MockK & Assertions

## MockK cheat sheet

| Need | Pattern |
|---|---|
| Plain mock | `mockk<T>()` |
| Relaxed (auto-stub everything) | `mockk<T>(relaxed = true)` |
| Spy on real instance | `spyk(MyService(...), recordPrivateCalls = true)` |
| Suspend stub | `coEvery { ... } returns ...` |
| `Unit`-returning stub | `justRun { ... }` / `coJustRun { ... }` |
| Verify | `verify(exactly = n) { ... }` / `coVerify { ... }` |
| Private function stub | `every { spy["privateFun"](any<T>()) } returns ...` (needs `recordPrivateCalls = true`) |

## Assertions

Prefer Kotest matchers. Group related field checks with `with { ... }`:

```kotlin
result shouldBe expected
list.shouldHaveSize(2)
list.shouldBeEmpty()
list.shouldContainExactly(a, b)
exception.message shouldContain "missing field"

with(result.first()) {
    httpStatusCode shouldBe HttpStatusCode.OK
    status shouldBe Status.COMPLETED
    reference shouldBe "123"
}
```
