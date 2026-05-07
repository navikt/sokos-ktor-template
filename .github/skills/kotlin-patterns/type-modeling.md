# Type Modeling & Extension Functions

## Sealed Classes and Interfaces

### Modeling Restricted Hierarchies

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

fun <T> Result<T>.getOrThrow(): T = when (this) {
    is Result.Success -> data
    is Result.Failure -> throw error.toException()
    is Result.Loading -> throw IllegalStateException("Still loading")
}
```

### Sealed Interfaces for API Responses

```kotlin
sealed interface ApiError {
    val message: String

    data class NotFound(override val message: String) : ApiError
    data class Unauthorized(override val message: String) : ApiError
    data class Validation(
        override val message: String,
        val field: String,
    ) : ApiError
    data class Internal(
        override val message: String,
        val cause: Throwable? = null,
    ) : ApiError
}

fun ApiError.toStatusCode(): Int = when (this) {
    is ApiError.NotFound -> 404
    is ApiError.Unauthorized -> 401
    is ApiError.Validation -> 422
    is ApiError.Internal -> 500
}
```

## Extension Functions

### Adding Functionality Without Inheritance

```kotlin
// Good: Domain-specific extensions
fun String.toSlug(): String =
    lowercase()
        .replace(Regex("[^a-z0-9\\s-]"), "")
        .replace(Regex("\\s+"), "-")
        .trim('-')

fun Instant.toLocalDate(zone: ZoneId = ZoneId.systemDefault()): LocalDate =
    atZone(zone).toLocalDate()

// Good: Collection extensions
fun <T> List<T>.second(): T = this[1]

fun <T> List<T>.secondOrNull(): T? = getOrNull(1)

// Good: Scoped extensions (not polluting global namespace)
class UserService {
    private fun User.isActive(): Boolean =
        status == Status.ACTIVE && lastLogin.isAfter(Instant.now().minus(30, ChronoUnit.DAYS))

    fun getActiveUsers(): List<User> = userRepository.findAll().filter { it.isActive() }
}
```

## Delegation

### Property Delegation

```kotlin
// Lazy initialization
val expensiveData: List<User> by lazy {
    userRepository.findAll()
}

// Observable property
var name: String by Delegates.observable("initial") { _, old, new ->
    logger.info("Name changed from '$old' to '$new'")
}

// Map-backed properties
class Config(private val map: Map<String, Any?>) {
    val host: String by map
    val port: Int by map
    val debug: Boolean by map
}

val config = Config(mapOf("host" to "localhost", "port" to 8080, "debug" to true))
```

### Interface Delegation

```kotlin
// Good: Delegate interface implementation
class LoggingUserRepository(
    private val delegate: UserRepository,
    private val logger: Logger,
) : UserRepository by delegate {
    // Only override what you need to add logging to
    override suspend fun findById(id: String): User? {
        logger.info("Finding user by id: $id")
        return delegate.findById(id).also {
            logger.info("Found user: ${it?.name ?: "null"}")
        }
    }
}
```
