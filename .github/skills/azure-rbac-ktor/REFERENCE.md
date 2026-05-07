# Reference: Azure RBAC in Ktor

## Code structure

```
security/
├── AccessPolicy.kt        # Scope and Role enums + validation
├── AuthorizationGuard.kt  # Extension functions on ApplicationCall
└── TokenUtils.kt          # Helper: extract app name from JWT
```

## Authorization flow

```
Incoming request
       │
       ▼
  Ktor auth plugin (JWT validation against Azure JWKS)
       │
       ▼
  requirePermission() / requireScope() / requireRole()
       │
       ├─── OBO token? ──► check `scp` claim against Scope.ALLOWED_SCOPES
       │                         │
       │                    ✅ match → allow
       │                    ❌ no match → check M2M
       │
       └─── M2M token? ──► check `roles` claim against Role.ALLOWED_ROLES
                                 │
                            ✅ match → allow
                            ❌ no match → throw AuthorizationException (→ 403)
```

## NAIS double-check

The NAIS platform enforces `accessPolicy` at network level (mTLS). The code check in `AuthorizationGuard` is an **additional layer** at application level — both must pass for the call to go through.

Troubleshooting order for 403:
1. Is the calling app's `application` name correct in `accessPolicy.inbound.rules`?
2. Is the desired scope/role listed under `permissions` for that app?
3. Does the route use the correct `requirePermission`/`requireScope`/`requireRole`?
4. Does the string value in the `Scope`/`Role` enum match what is configured in NAIS?

## API overview

```kotlin
// Check OBO scope OR M2M role (recommended for most endpoints)
call.requirePermission(requiredScope: Scope, requiredRole: Role)

// Check OBO scope only
call.requireScope(requiredScope: Scope)

// Check M2M role only
call.requireRole(requiredRole: Role)

// Get NAVident from OBO token (null for M2M)
call.getNavIdentOrNull(): String?

// Get app name from azp_name/client_id
call.getCallingSystem(): String
```

All functions throw `AuthorizationException` (→ HTTP 403) on missing access.
