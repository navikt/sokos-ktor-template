---
name: backend-vulnerabilities
description: "Lukk backend-sårbarheter i Gradle-prosjekter på Nais. Bruk ved GitHub Security-varsler eller for å sjekke om resolutionStrategy-pakker kan fjernes. Aksepterer spørsmål på norsk og engelsk. (Sårbarheter, dependency-oppdatering, CVE, Dependabot, resolutionStrategy, Gradle)"
---

# Backend-sårbarheter

Målet er å sjekke om sårbarheter har blitt fikset transitivt, slik at vi ikke trenger å holde `resolutionStrategy` lenger enn nødvendig.

---

## Steg 1 — Sjekk GitHub Security

Gå til **Security → Dependabot**-fanen på repoet og se hvilke sårbarheter som er rapportert.

---

## Steg 2 — Sjekk én og én pakke i resolutionStrategy

For hver pakke i `resolutionStrategy`, kjør (bytt ut pakkenavnet):

```bash
./gradlew dependencies --configuration runtimeClasspath | grep jackson-core
```

| Resultat | Handling |
|----------|----------|
| Versjon i output ≥ Dependabots anbefaling | Fjern pakken fra `resolutionStrategy` |
| Sårbar versjon vises fortsatt | Behold — gå til steg 3 |

---

## Steg 3 — Legg tilbake resolutionStrategy for det som fortsatt er sårbart

Legg kun tilbake pakker som fremdeles er sårbare. `because`-feltet skal inneholde nøyaktig det Dependabot skriver, pluss hvilke versjoner som er berørt:

```kotlin
configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "com.fasterxml.jackson.core" && requested.name == "jackson-core") {
                useVersion("2.21.1")
                because("jackson-core: Number Length Constraint Bypass in Async Parser Leads to Potential DoS Condition. Affected version >= 2.19.0, < 2.21.1")
            }
        }
    }
}
```

Verifiser at riktig versjon brukes etter endringen:

```bash
./gradlew dependencies --configuration runtimeClasspath | grep jackson-core
```

---

## Ferdig?

Når ingen **high** eller høyere sårbarheter vises i GitHub Security → deploy og verifiser at backend kjører normalt.

> **Tips:** Hvis en sårbarhet fortsatt vises i GitHub Security, men du allerede bruker den anbefalte versjonen, kan du **dismiss** alerten direkte i Dependabot.
