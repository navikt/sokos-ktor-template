---
name: azure-rbac-ktor
description: "Role-based access control (RBAC) with Azure AD in Ktor services on NAIS. Use when adding endpoints with access control, defining new roles/scopes, or configuring accessPolicy in NAIS manifests. Accepts prompts in Norwegian and English. (Rollebasert tilgangsstyring, tilgangskontroll, endepunkter, roller, scopes, Nais-manifest)"
---

# Azure RBAC in Ktor

## Overview

Access control distinguishes between two token types:

| Token type | Source | JWT claim | Use case |
|-----------|-------|-----------|----------|
| **OBO** (On-Behalf-Of) | User via frontend | `scp` | Case worker calls API |
| **M2M** (Machine-to-Machine) | System without user | `roles` | Another service calls directly |

## 1. Define scopes and roles

Add new values in `AccessPolicy.kt`:

```kotlin
enum class Scope(val value: String) {
    READ_DATA_SCOPE("read-data-scope"),
    WRITE_DATA_SCOPE("write-data-scope"),
    // add new scopes here
}

enum class Role(val value: String) {
    READ_DATA_ROLE("read-data-role"),
    WRITE_DATA_ROLE("write-data-role"),
    // add new roles here
}
```

String values **must** exactly match what is configured in the NAIS manifest.

## 2. Protect endpoints in Ktor

Use extension functions from `AuthorizationGuard` inside routes:

```kotlin
// OBO OR M2M — most common pattern
get("/data/{id}") {
    call.requirePermission(
        requiredScope = Scope.READ_DATA_SCOPE,
        requiredRole  = Role.READ_DATA_ROLE,
    )
    // business logic
}

// OBO only (user context required)
post("/submit") {
    call.requireScope(Scope.WRITE_DATA_SCOPE)
    val navIdent = call.getNavIdentOrNull()   // log who performed the action
}

// M2M only (system call)
post("/ingest") {
    call.requireRole(Role.WRITE_DATA_ROLE)
    val system = call.getCallingSystem()      // log which app called
}
```

## 3. Configure NAIS manifest

Add each calling system under `accessPolicy.inbound.rules` with **only** the scopes/roles it actually needs:

```yaml
azure:
  application:
    enabled: true
    allowAllUsers: true
    claims:
      extra:
        - NAVident          # required for OBO tokens

accessPolicy:
  inbound:
    rules:
      - application: my-frontend-app
        permissions:
          scopes:
            - "read-data-scope"

      - application: my-batch-service
        permissions:
          roles:
            - "read-data-role"

      - application: external-app
        namespace: other-team
        cluster: prod-gcp    # only for cross-cluster
        permissions:
          roles:
            - "write-data-role"
```

## 4. Logging and audit

`getCallingSystem()` extracts the app name from `azp_name`/`client_id` and strips the cluster/namespace prefix:

```
"dev-gcp:okonomi:my-calling-app" → "my-calling-app"
```

Always log `navIdent` or `callingSystem` — never fnr/PII.

## Common errors

| Error | Cause | Fix |
|-------|-------|-----|
| 403 despite valid token | Scope/role missing in NAIS manifest | Add under `permissions` for the correct app |
| 403 despite NAIS access | Scope string doesn't match enum | Verify `Scope.value == permissions.scope` exactly |
| `NAVident` missing | `claims.extra` not set | Add `- NAVident` under `claims.extra` |
| M2M token gets OBO error | Wrong `requireScope` instead of `requirePermission` | Use `requirePermission` to support both |

## See also

- [REFERENCE.md](REFERENCE.md) — detailed flow description and code structure
