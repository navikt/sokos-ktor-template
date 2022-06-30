# ktor-template-utbetaling


Kan brukes som utgangspunkt for å opprette nye Ktor-apper for Team Økonomi.


## Tilpass repo-et
1. Søk etter og erstatt `ktor-template-utbetaling` med det som skal være navnet på den nye appen.
2. Søk etter og erstatt `prosjektnavn` med det som skal være navnet på den nye appen.
3. Endre navnet på mappen `src/main/kotlin/no/nav/sokos/prosjektnavn` til noe som passer for den nye appen.

## Workflows

#### - Endre navn på mappen `.github/workflow_files` til `.github/workflows` for at github actions skal plukke dem opp. Dette vil sørge for at du får fire github actions:
1. [Deploy alarmer](.github/workflows/alerts.yaml) -> For å pushe opp [alerterator.yaml](.nais/alerterator.yaml) for både prod og dev (Env variabler hentes fra [dev-gcp.json](.nais/dev-gcp.json) og [prod-gcp.json](.nais/prod-gcp.json))
2. [Bygg, test og deploy](.github/workflows/build-and-deploy.yaml) -> For å teste, bygge prosjeket og bygge Docker image. Den vil også pushe [naiserator.yaml](.nais/naiserator.yaml) for både prod og dev (Env variabler hentes fra [dev-gcp.json](.nais/dev-gcp.json) og [prod-gcp.json](.nais/prod-gcp.json))
3. [Bygg og test PR](.github/workflows/build-pr.yaml) -> For å bygge og teste alle PR som blir opprettet
4. [Sårbarhetsskanning av avhengigheter](.github/workflows/snyk.yaml) -> For å skanne sårbarhet av avhengigheter. Kjøres hver natt kl 03:00

## Bygge og kjøre prosjekt
1. Bygg ktor-template-utbetaling ved å kjøre `gradle build`
1. Start appen lokalt ved å kjøre main metoden i [Bootstrap.kt](src/main/kotlin/no/nav/sokos/prosjektnavn/Bootstrap.kt)
1. Appen nås på `URL`

## Ting som enhver utvikler må ta høyde for og fikse
1. [.dockerignore](.dockerignore) -> Legg inn det du ikke trenger å ha med når du bygger Docker image
2. [.nais](.nais) -> Mappen inneholder en `naiserator.yaml` fil og en `alerterator.yaml` for å unngå ha en fil for dev og prod for begge filene. Miljøvariabler legges i `dev-gcp.json` og `prod-gcp.json` hvor de populeres inn i `naiserator.yaml` og `alerterator.yaml`. 
   1. NB! Anbefales å gjøre dette slik med mindre du har behov for å opprette to filer for `naiserator.yaml` og `alerterator.yaml` for å fylle applikasjonens behov
      1. [.nais/alerterator.yaml](.nais/alerterator.yaml) -> Default er lagt inn. Legg inn det applikasjonen har behov for
      2. [.nais/naiserator.yaml](.nais/naiserator.yaml) -> Default er lagt inn. Legg inn det applikasjonen har behov for 
# NB!! Kommer du på noe lurt vi bør ha med i template som default så opprett gjerne en PR 
  
## Henvendelser

- Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på github.
- Interne henvendelser kan sendes via Slack i kanalen [#po-utbetaling](https://nav-it.slack.com/archives/CKZADNFBP)

```
Alt under her skal beholdes som en standard dokumentasjon som må fylles ut av utviklere.
```
---
[![Bygg, test og deploy](https://github.com/navikt/ktor-template-utbetaling/actions/workflows/build-and-deploy.yaml/badge.svg)](https://github.com/navikt/ktor-template-utbetaling/actions/workflows/build-and-deploy.yaml)
[![Deploy alarmer](https://github.com/navikt/ktor-template-utbetaling/actions/workflows/alerts.yaml/badge.svg)](https://github.com/navikt/ktor-template-utbetaling/actions/workflows/alerts.yaml)
[![Sårbarhetsskanning av avhengigheter](https://github.com/navikt/ktor-template-utbetaling/actions/workflows/snyk.yaml/badge.svg)](https://github.com/navikt/ktor-template-utbetaling/actions/workflows/snyk.yaml)

# Prosjektnavn
Kort beskrivelse om prosjektet, og hav målet til prosjektet er

---

## Oppsett av utviklermaskin
Hva trenges for å sette opp prosjektet

---

## Bygging
Hvordan bygger jeg prosjektet.

---

## Lokal utvikling
Hvordan kan jeg kjøre lokalt og hva trenger jeg?

---

## Docker
Hvis det finnes Dockerfile eller docker-compose fil, hva er kommando for å kjøre?

---

## Logging
Hvor finner jeg logger? Hvordan filtrerer jeg mellom dev og prod logger?

[sikker-utvikling/logging](https://sikkerhet.nav.no/docs/sikker-utvikling/logging) - Anbefales å lese

---

## Nyttig informasjon
Trenger jeg vite noe mer? Skriv her!

---

## Swagger URL
Hva er url til Lokal, dev og prod?
