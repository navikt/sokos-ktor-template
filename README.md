# sokos-ktor-template

Kan brukes som utgangspunkt for å opprette nye Ktor-apper for Team Motta og Beregne.

## Tilpass repo-et
1. Gi rettighet for å kjøre scriptet `chmod 755 setupTemplate.sh`
2. Kjør scriptet: 
   ```
   ./setupTemplate.sh
   ```
3. Fyll inn prosjektnavn og artifaktnavn (no.nav.sokos.xxx)

## Workflows

1. [Deploy alarmer](.github/workflows/alerts-dev.yaml) -> For å pushe alarmer for dev
   1. Denne workflow kjører inviduelt og trigges også hvis det gjøres endringer i [naiserator-dev.yaml](.nais/naiserator-dev.yaml) og [naiserator-prod.yaml](.nais/naiserator-prod.yaml)
   2. NB! Denne bør du bytte fra å deploye til `prod-gcp` istedet.
   3. Endre filnavn til `alerts-prod.yaml`
   4. Endre slack kanal i yaml filen til `channel: '#team-mob-alerts-prod'`
   5. Endre cluster som alert skal deployes til i workflow for alerts til `CLUSTER: prod-gcp`
2. [Bygg, test og deploy til dev/prod](.github/workflows/build-test-push-deploy.yaml) -> For å bygge/teste prosjektet, bygge/pushe Docker image og deploy til dev og prod
   1. Denne workflow er den aller første som kjøres når kode er i `master/main` branch
3. [Bygg og test PR](.github/workflows/build-pr.yaml) -> For å bygge og teste alle PR som blir opprettet
   1. Denne workflow kjøres kun når det opprettes pull requester
4. [Sikkerhet](.github/workflows/security.yaml) -> For å skanne kode og docker image for sårbarheter. Kjøres hver morgen kl 06:00
   1. Denne kjøres når [Bygg, test og deploy til dev/prod](.github/workflows/build-test-push-deploy.yaml) har kjørt ferdig
5. [Manuell deploy](./.github/workflows/manual-deploy.yaml) -> For å kjøre manuelle deploys til dev. Denne er ment for teste inviduelt
   1. Denne workflow er for å kunne gjøre manuelle deploy basert på hvilken branch du velger

## OpenApi Generator og Swagger
1. Endre [pets.json](https://github.com/navikt/sokos-ktor-template/blob/master/build.gradle.kts#L73) til hva spec filen skal hete som ligger i [specs](specs) mappa.
2. Når prosjektet bygges genereres det data klasser i `build` mappa. Disse sjekkes ikke inn i Git pga. datamodellen kan endres ganske mye så slipper du pushe inn hver endring i modellen. Dvs du følger kontrakten, altså api spec
3. Når du kjører applikasjonen genereres det en SwaggerUI som kan nås på [localhost:8080/api/v1/docs](localhost:8080/api/v1/docs)

## Bygge og kjøre prosjekt
1. Bygg `sokos-ktor-template` ved å kjøre `./gradlew buildFatJar`
2. Start appen lokalt ved å kjøre main metoden i [Bootstrap.kt](src/main/kotlin/no/nav/sokos/prosjektnavn/Bootstrap.kt)
3. Appen nås på `URL`
4. For å kjøre tester i IntelliJ IDEA trenger du [Kotest IntelliJ Plugin](https://plugins.jetbrains.com/plugin/14080-kotest)

# NB!! Kommer du på noe lurt vi bør ha med i template som default så opprett gjerne en PR 
  
## Henvendelser

- Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på github.
- Interne henvendelser kan sendes via Slack i kanalen [#po-utbetaling](https://nav-it.slack.com/archives/CKZADNFBP)

```
Alt under her skal beholdes som en standard dokumentasjon som må fylles ut av utviklere.
```

# Prosjektnavn

# Innholdsoversikt
* [1. Funksjonelle krav](#1-funksjonelle-krav)
* [2. Utviklingsmiljø](#2-utviklingsmiljø)
* [3. Programvarearkitektur](#3-programvarearkitektur)
* [4. Deployment](#4-deployment)
* [5. Autentisering](#5-autentisering)
* [6. Drift og støtte](#6-drift-og-støtte)
* [7. Swagger](#7-swagger)
* [8. Henvendelser](#7-henvendelser)
---

# 1. Funksjonelle Krav
Hva er oppgaven til denne applikasjonen

# 2. Utviklingsmiljø
### Forutsetninger
* Java 17
* Gradle 8

### Bygge prosjekt
Hvordan bygger jeg prosjektet.

### Lokal utvikling
Hvordan kan jeg kjøre lokalt og hva trenger jeg?

# 3. Programvarearkitektur
Legg ved skissediagram for hvordan arkitekturen er bygget

# 4. Deployment
Distribusjon av tjenesten er gjort med bruk av Github Actions.
[sokos-ktor-template CI / CD](https://github.com/navikt/sokos-ktor-template/actions)

Push/merge til master branche vil teste, bygge og deploye til produksjonsmiljø og testmiljø.
Det foreligger også mulighet for manuell deploy.

# 7. Autentisering
Applikasjonen bruker [AzureAD](https://docs.nais.io/security/auth/azure-ad/) autentisering

### Hente token
1. Installer `vault` kommandolinje verktøy
2. Gi rettighet for å kjøre scriptet `chmod 755 getToken.sh`
3. Kjør scriptet:
   ```
   ./getToken.sh
   ```
4. Skriv inn applikasjonsnavn du vil hente `client_id` og `client_secret` for

# 6. Drift og støtte

### Logging
Hvor finner jeg logger? Hvordan filtrerer jeg mellom dev og prod logger?

[sikker-utvikling/logging](https://sikkerhet.nav.no/docs/sikker-utvikling/logging) - Anbefales å lese

### Kubectl
For dev-gcp:
```shell script
kubectl config use-context dev-gcp
kubectl get pods -n okonomi | grep sokos-ktor-template
kubectl logs -f sokos-ktor-template-<POD-ID> --namespace okonomi -c sokos-ktor-template
```

For prod-gcp:
```shell script
kubectl config use-context prod-gcp
kubectl get pods -n okonomi | grep sokos-ktor-template
kubectl logs -f sokos-ktor-template-<POD-ID> --namespace okonomi -c sokos-ktor-template
```

### Alarmer
Vi bruker [nais-alerts](https://doc.nais.io/observability/alerts) for å sette opp alarmer. Disse finner man konfigurert i [.nais/alerterator.yaml](.nais/alerterator.yaml) filen.

### Grafana
- [appavn](url)
---

# 7. Swagger
Hva er url til Lokal, dev og prod?

