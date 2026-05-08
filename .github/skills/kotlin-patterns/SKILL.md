---
name: kotlin-patterns
description: "Idiomatic Kotlin patterns: null safety, immutability, sealed types, structured concurrency, extension functions, DSL builders, Gradle Kotlin DSL. Accepts prompts in Norwegian and English. (Kotlin-mønstre, idiomatisk Kotlin, null-sikkerhet, sealed class, koroutiner, Gradle)"
---

# Kotlin Development Patterns

Idiomatic Kotlin patterns and best practices for building robust, efficient, and maintainable applications.

## When to Use

- Writing new Kotlin code
- Reviewing Kotlin code
- Refactoring existing Kotlin code
- Designing Kotlin modules or libraries
- Configuring Gradle Kotlin DSL builds

## Sub-files

- [core-patterns.md](core-patterns.md) — null safety, immutability, expression bodies, scope functions
- [type-modeling.md](type-modeling.md) — sealed types, extensions, delegation
- [coroutines.md](coroutines.md) — structured concurrency, Flow, cancellation
- [gradle-build-configuration.md](gradle-build-configuration.md) — project Gradle setup (sokos-ktor-template)
- [error-handling.md](error-handling.md) — error patterns, collection operations, anti-patterns

## Core Principles

### 1. Null Safety

Kotlin's type system distinguishes nullable and non-nullable types. Leverage it fully.

```kotlin
// Good: Use non-nullable types by default
fun getUser(id: String): User {
    return userRepository.findById(id)
        ?: throw UserNotFoundException("User $id not found")
}

// Good: Safe calls and Elvis operator
fun getUserEmail(userId: String): String {
    val user = userRepository.findById(userId)
    return user?.email ?: "unknown@example.com"
}

// Bad: Force-unwrapping nullable types
fun getUserEmail(userId: String): String {
    val user = userRepository.findById(userId)
    return user!!.email // Throws NPE if null
}
```

### 2. Immutability by Default

Prefer `val` over `var`, immutable collections over mutable ones.

```kotlin
// Good: Immutable data
data class User(
    val id: String,
    val name: String,
    val email: String,
)

// Good: Transform with copy()
fun updateEmail(user: User, newEmail: String): User =
    user.copy(email = newEmail)

// Good: Immutable collections
val users: List<User> = listOf(user1, user2)
val filtered = users.filter { it.email.isNotBlank() }

// Bad: Mutable state
var currentUser: User? = null // Avoid mutable global state
```

### 3. Expression Bodies and Single-Expression Functions

```kotlin
// Good: Expression body
fun isAdult(age: Int): Boolean = age >= 18

fun User.displayName(): String =
    name.ifBlank { email.substringBefore('@') }

// Good: When as expression
fun statusMessage(code: Int): String = when (code) {
    200 -> "OK"
    404 -> "Not Found"
    500 -> "Internal Server Error"
    else -> "Unknown status: $code"
}
```

### 4. Data Classes and Value Classes

```kotlin
// Good: Data class with copy, equals, hashCode, toString
data class CreateUserRequest(
    val name: String,
    val email: String,
    val role: Role = Role.USER,
)

// Good: Value class for type safety (zero overhead at runtime)
@JvmInline
value class UserId(val value: String) {
    init {
        require(value.isNotBlank()) { "UserId cannot be blank" }
    }
}
```

## Sealed Classes and Interfaces

```kotlin
// Good: Sealed class for exhaustive when
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val error: AppError) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

fun <T> Result<T>.getOrNull(): T? = when (this) {
    is Result.Success -> data
    is Result.Failure -> null
    is Result.Loading -> null
}

// Good: Sealed interface for API errors
sealed interface ApiError {
    val message: String
    data class NotFound(override val message: String) : ApiError
    data class Unauthorized(override val message: String) : ApiError
    data class Validation(override val message: String, val field: String) : ApiError
    data class Internal(override val message: String, val cause: Throwable? = null) : ApiError
}

fun ApiError.toStatusCode(): Int = when (this) {
    is ApiError.NotFound -> 404
    is ApiError.Unauthorized -> 401
    is ApiError.Validation -> 422
    is ApiError.Internal -> 500
}
```

## Scope Functions

```kotlin
// let: Transform nullable or scoped result
val length: Int? = name?.let { it.trim().length }

// apply: Configure an object (returns the object)
val user = User().apply {
    name = "Alice"
    email = "alice@example.com"
}

// also: Side effects (returns the object)
val user = createUser(request).also { logger.info("Created user: ${it.id}") }

// run: Execute a block with receiver (returns result)
val result = connection.run { prepareStatement(sql); executeQuery() }

// Anti-pattern: avoid deep nesting
// Bad:
user?.let { u -> u.address?.let { a -> a.city?.let { c -> process(c) } } }
// Good:
user?.address?.city?.let { process(it) }
```

## Extension Functions

```kotlin
// Good: Domain-specific extensions
fun String.toSlug(): String =
    lowercase()
        .replace(Regex("[^a-z0-9\\s-]"), "")
        .replace(Regex("\\s+"), "-")
        .trim('-')

// Good: Scoped extensions (not polluting global namespace)
class UserService {
    private fun User.isActive(): Boolean =
        status == Status.ACTIVE && lastLogin.isAfter(Instant.now().minus(30, ChronoUnit.DAYS))

    fun getActiveUsers(): List<User> = userRepository.findAll().filter { it.isActive() }
}
```

## Coroutines

### Structured Concurrency

```kotlin
// Good: Structured concurrency with coroutineScope
suspend fun fetchUserWithPosts(userId: String): UserProfile =
    coroutineScope {
        val userDeferred = async { userService.getUser(userId) }
        val postsDeferred = async { postService.getUserPosts(userId) }
        UserProfile(user = userDeferred.await(), posts = postsDeferred.await())
    }

// Good: supervisorScope when children can fail independently
suspend fun fetchDashboard(userId: String): Dashboard =
    supervisorScope {
        val notifications = async { notificationService.getRecent(userId) }
        Dashboard(
            notifications = try {
                notifications.await()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                emptyList()
            },
        )
    }
```

### Flow for Reactive Streams

```kotlin
// Good: Cold flow with proper error handling
fun observeUsers(): Flow<List<User>> = flow {
    while (currentCoroutineContext().isActive) {
        emit(userRepository.findAll())
        delay(5.seconds)
    }
}.catch { e ->
    logger.error("Error observing users", e)
    emit(emptyList())
}

// Good: Flow operators
fun searchUsers(query: Flow<String>): Flow<List<User>> =
    query
        .debounce(300.milliseconds)
        .distinctUntilChanged()
        .filter { it.length >= 2 }
        .mapLatest { q -> userRepository.search(q) }
        .catch { emit(emptyList()) }
```

### Cancellation and Cleanup

```kotlin
// Good: Respect cancellation
suspend fun processItems(items: List<Item>) {
    items.forEach { item ->
        ensureActive()
        processItem(item)
    }
}

// Good: Cleanup with try/finally
suspend fun acquireAndProcess() {
    val resource = acquireResource()
    try {
        resource.process()
    } finally {
        withContext(NonCancellable) {
            resource.release()
        }
    }
}
```

## Delegation

```kotlin
// Property delegation
val expensiveData: List<User> by lazy { userRepository.findAll() }

// Interface delegation — add logging without reimplementing the whole interface
class LoggingUserRepository(
    private val delegate: UserRepository,
) : UserRepository by delegate {
    override suspend fun findById(id: String): User? {
        logger.info("Finding user by id: $id")
        return delegate.findById(id)
    }
}
```

## Error Handling

```kotlin
// Good: Result type for domain operations
suspend fun createUser(request: CreateUserRequest): Result<User> = runCatching {
    require(request.name.isNotBlank()) { "Name cannot be blank" }
    require('@' in request.email) { "Invalid email format" }
    userRepository.save(User(id = UUID.randomUUID().toString(), name = request.name, email = request.email))
}

// Good: Preconditions with clear messages
fun withdraw(account: Account, amount: Money): Account {
    require(amount.value > 0) { "Amount must be positive: $amount" }
    check(account.balance >= amount) { "Insufficient balance: ${account.balance} < $amount" }
    return account.copy(balance = account.balance - amount)
}
```

## Collection Operations

```kotlin
// Good: Chained operations
val activeAdminEmails: List<String> = users
    .filter { it.role == Role.ADMIN && it.isActive }
    .sortedBy { it.name }
    .map { it.email }

// Good: Grouping and aggregation
val usersByRole: Map<Role, List<User>> = users.groupBy { it.role }
val usersById: Map<String, User> = users.associateBy { it.id }
val (active, inactive) = users.partition { it.isActive }

// Good: Sequences for large collections with multiple operations
val result = users.asSequence()
    .filter { it.isActive }
    .map { it.email }
    .filter { it.endsWith("@nav.no") }
    .take(10)
    .toList()
```

## Gradle Kotlin DSL

Se [gradle-build-configuration.md](gradle-build-configuration.md) for det fullstendige oppsettet brukt i dette prosjektet.

Nøkkelpunkter for dette prosjektet:
- **Linting**: `ktlint` (kjøres automatisk før kompilering via `dependsOn("ktlintFormat")`)
- **Coverage**: `kover` med HTML-rapport
- **JVM**: Toolchain versjon 25
- **Kotlin**: 2.3.21 med serialization-plugin
- **Testing**: Kotest + MockK + mock-oauth2-server

## Quick Reference: Kotlin Idioms

| Idiom | Use for |
|-------|---------|
| `val` over `var` | Prefer immutable variables |
| `data class` | Value objects with equals/hashCode/copy |
| `sealed class/interface` | Restricted type hierarchies |
| `value class` | Type-safe wrappers with zero overhead |
| Expression `when` | Exhaustive pattern matching |
| `?.` / `?:` | Null-safe access and defaults |
| `let`/`apply`/`also`/`run`/`with` | Scope functions for clean code |
| Extension functions | Add behavior without inheritance |
| `copy()` | Immutable updates on data classes |
| `require`/`check` | Precondition assertions |
| `async`/`await` | Structured concurrent execution |
| `Flow` | Cold reactive streams |
| `sequence` | Lazy evaluation |
| Delegation `by` | Reuse implementation without inheritance |

## Boundaries

### ✅ Always
- Null safety via types (`?`, `?.`, `?:`)
- `sealed` for modeled state
- `suspend` + structured concurrency for async
- Run `ktlintFormat` before committing (happens automatically on build)
- [navikt/kotliquery](https://github.com/navikt/kotliquery) for database access

### 🚫 Never
- ORM frameworks (Exposed, Spring Data, Hibernate) — use navikt/kotliquery
- DI frameworks (Koin, Spring, Dagger) — use manual constructor injection
- `runBlocking` in production code or test blocks
- `GlobalScope.launch`
- `!!` without a preceding null check
- Ignore coroutine cancellation (`CancellationException` must always be rethrown)
