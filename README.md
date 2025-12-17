# sokos-ktor-template

Kan brukes som utgangspunkt for å opprette nye Ktor-backend applikasjoner for Team Motta og Beregne

## Tilpass repo-et

1. Gi rettighet for å kjøre scriptet `chmod 755 setupTemplate.sh`
2. Kjør scriptet:
   ```
   ./setupTemplate.sh
   ```
3. Fyll inn prosjektnavn (sokos-foo-bar) og artifaktnavn (foo.bar)

## Workflows

1. [Deploy application](.github/workflows/deploy.yaml) -> Kjører tester og bygger (assembler) distribution, bygger og pusher Docker image og deploy til dev og prod
    1. Denne workflow trigges når kode pushes i `main` branch
2. [Build/test PR](.github/workflows/build-pr.yaml) -> Kjører tester og bygger (assembler) distribution alle PR som blir opprettet
    1. Denne workflow kjøres kun når det opprettes pull requester
3. [Security](.github/workflows/codeql-trivy-scan.yaml) -> For å skanne kode og docker image for sårbarheter. Kjøres hver morgen kl 06:00
    1. Denne kjøres hver mandag klokka 06.00
4. [Deploy application manual](.github/workflows/manual-deploy.yaml) -> For å deploye applikasjonen manuelt til ulike miljøer
    1. Denne workflow trigges manuelt basert på branch og miljø

## Bygge og kjøre prosjekt

1. Bygg prosjektet ved å kjøre `./gradlew build installDist`
2. Start appen lokalt ved å kjøre main metoden i ***Application.kt***
3. For å kjøre tester i IntelliJ IDEA trenger du [Kotest IntelliJ Plugin](https://plugins.jetbrains.com/plugin/14080-kotest)

# NB!! Kommer du på noe lurt vi bør ha med i template som default så opprett gjerne en PR

## Henvendelser

- Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på github.
- Interne henvendelser kan sendes via Slack i kanalen [#utbetaling](https://nav-it.slack.com/archives/CKZADNFBP)

```
‼️ Alt under her skal beholdes som en standard dokumentasjon som må fylles ut av utviklere. Vi prøver å ha standard mal for alle våre applikasjoner ‼️
```

# Prosjektnavn

* [1. Dokumentasjon](dokumentasjon/dokumentasjon.md)
* [2. Funksjonelle krav](#2-funksjonelle-krav)
* [3. Utviklingsmiljø](#3-utviklingsmiljø)
* [4. Programvarearkitektur](#4-programvarearkitektur)
* [5. Deployment](#5-deployment)
* [6. Autentisering](#6-autentisering)
* [7. Drift og støtte](#7-drift-og-støtte)
* [8. Swagger](#8-swagger)
* [9. Henvendelser](#9-henvendelser)

---

# 2. Funksjonelle Krav

Hva er oppgaven til denne applikasjonen

# 3. Utviklingsmiljø

### Forutsetninger

* Java 25
* [Gradle](https://gradle.org/)
* [Kotest IntelliJ Plugin](https://plugins.jetbrains.com/plugin/14080-kotest)

### Bygge prosjekt

`./gradlew build installDist`

### Lokal utvikling

Hvordan kan jeg kjøre lokalt og hva trenger jeg?

# 4. Programvarearkitektur

Legg ved skissediagram for hvordan arkitekturen er bygget

# 5. Deployment

Distribusjon av tjenesten er gjort med bruk av Github Actions.
[sokos-ktor-template CI / CD](https://github.com/navikt/sokos-ktor-template/actions)

Push/merge til main branche vil teste, bygge og deploye til produksjonsmiljø og testmiljø.

# 6. Autentisering

Applikasjonen bruker [AzureAD](https://docs.nais.io/security/auth/azure-ad/) autentisering.
- Bruk https://azure-token-generator.intern.dev.nav.no/api/obo?aud=cluster:namespace:app for å generere obo-token testing mot dev.
- Bruk https://azure-token-generator.intern.dev.nav.no/api/m2m?aud=cluster:namespace:app for å generere m2m-token testing mot dev.

# 7. Drift og støtte

### Logging

Feilmeldinger og infomeldinger som ikke innheholder sensitive data logges til [Grafana Loki](https://docs.nais.io/observability/logging/#grafana-loki).  
Sensitive meldinger logges til [Team Logs](https://doc.nais.io/observability/logging/how-to/team-logs/).

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

Applikasjonen bruker [Grafana Alerting](https://grafana.nav.cloud.nais.io/alerting/) for overvåkning og varsling.

Alarmene overvåker metrics som:

- HTTP-feilrater
- JVM-metrikker

Varsler blir sendt til følgende Slack-kanaler:

- Dev-miljø: [#team-mob-alerts-dev](https://nav-it.slack.com/archives/C042SF2FEQM)
- Prod-miljø: [#team-mob-alerts-prod](https://nav-it.slack.com/archives/C042ESY71GX)

### Grafana

- [appavn](url)

---

# 8. Swagger

- Url Lokal
- Url Dev 
- Url Prod

# 9. Henvendelser og tilgang

Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på Github.
Interne henvendelser kan sendes via Slack i kanalen [#utbetaling](https://nav-it.slack.com/archives/CKZADNFBP)

