# okonomi-ktor-template

Kan brukes som utgangspunkt for å opprette nye Ktor-apper for Team Økonomi.

## Tilpass repo-et
1. Søk etter og erstatt `okonomi-ktor-template` med det som skal være navnet på den nye appen.
2. Søk etter og erstatt `prosjektnavn` med det som skal være navnet på den nye appen.
3. Endre navnet på mappen `src/main/kotlin/no/nav/sokos/prosjektnavn` til noe som passer for den nye appen.

## Workflows

NB! Endre navn på mappen `.github/workflow_files` til `.github/workflows` for at github actions skal plukke dem opp. Dette vil sørge for at du får fire github actions:
1. [Deploy alarmer](.github/workflows/alerts.yaml) -> For å pushe opp [alerterator.yaml](.nais/alerterator.yaml) og pushe alarmer for både prod og dev
   1. Denne workflow kjører inviduelt og trigges også hvis det gjøres endringer i [naiserator.yaml](.nais/naiserator.yaml)
2. [Bygg og push Docker image](.github/workflows/build-and-push-docker-image.yaml) -> For å bygge/teste prosjektet og bygge/pushe Docker image
   1. Denne workflow er den aller første som kjøres når kode er i `master/main` branch
3. [Deploy til dev og prod](.github/workflows/deploy-dev-prod.yaml) -> For å pushe [naiserator.yaml](.nais/naiserator.yaml) og deploye applikasjonen til dev og prod
   1. Denne workflow tar seg KUN av deploy av applikasjonen til NAIS. Den er avhengig av at [Bygg og test](.github/workflows/build-and-push-docker-image.yaml) går gjennom
4. [Bygg og test PR](.github/workflows/build-pr.yaml) -> For å bygge og teste alle PR som blir opprettet
   1. Denne workflow kjøres kun når det opprettes pull requester
5. [Sikkerhet](.github/workflows/snyk.yaml) -> For å skanne sårbarhet av avhengigheter og docker image. Kjøres hver morgen kl 06:00
   1. Denne kjøres når [Deploy til dev og prod](.github/workflows/deploy-dev-prod.yaml) har kjørt ferdig

NB! Hvis du ønsker at [Sikkerhet](.github/workflows/snyk.yaml) kjøres først og [Deploy til dev og prod](.github/workflows/deploy-dev-prod.yaml) kjøres NÅR `Sikkerhet` er ferdig så gjør følgende:

i [snyk.yaml](.github/workflows/snyk.yaml) endrer du fra:
```
on:
  workflow_run:
    workflows: [ "Deploy til dev og prod" ]
```
til
```
on:
  workflow_run:
    workflows: [ "Bygg og push Docker image" ]
```

og i [deploy-dev-prod.yaml](.github/workflows/deploy-dev-prod.yaml) endrer du fra:
```
on:
  workflow_run:
    workflows: [ "Bygg og push Docker image" ]
```
til
```
on:
  workflow_run:
    workflows: [ "Sikkerhet" ]
```


## Bygge og kjøre prosjekt
1. Bygg `okonomi-ktor-template` ved å kjøre `./gradlew shadowJar`
1. Start appen lokalt ved å kjøre main metoden i [Bootstrap.kt](src/main/kotlin/no/nav/sokos/prosjektnavn/Bootstrap.kt)
1. Appen nås på `URL`

## Ting som enhver utvikler må ta høyde for og fikse
1. [.nais](.nais) -> Mappen inneholder en `naiserator.yaml` fil og en `alerterator.yaml` for å unngå ha en fil for dev og prod for begge filene. Miljøvariabler legges i `dev-gcp.json` og `prod-gcp.json` hvor de populeres inn i `naiserator.yaml` og `alerterator.yaml`. 
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
[![Bygg og push Docker image](https://github.com/navikt/okonomi-ktor-template/actions/workflows/build-and-push-docker-image.yaml/badge.svg)](https://github.com/navikt/okonomi-ktor-template/actions/workflows/build-and-push-docker-image.yaml)
[![Deploy til dev og prod](https://github.com/navikt/okonomi-ktor-template/actions/workflows/deploy-dev-prod.yaml/badge.svg)](https://github.com/navikt/okonomi-ktor-template/actions/workflows/deploy-dev-prod.yaml)
[![Sikkerhet](https://github.com/navikt/okonomi-ktor-template/actions/workflows/snyk.yaml/badge.svg)](https://github.com/navikt/okonomi-ktor-template/actions/workflows/snyk.yaml)

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

