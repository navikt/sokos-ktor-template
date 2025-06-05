# Ktor config oppsett


## Må gjøres
1. Ha filer som heter: application.conf, application-dev.conf, application-prod.conf, application-test.conf i resources
2. defaults.conf i root
3.  ```       systemProperty("test.env", "true")``` i withType<Test> i build.gradle.kts

## For test
* Sjekke application-test.conf
* Override variabler i application-test.conf med det man trenger for test, eller manuelt lage MapApplicationConfig() config
  *  Se TestApplicationEksempler for hvordan override med testapplication 

# Ktor server ApplicationConfig
1. Lever i Ktor Application scope
2. Application.conf lastes automatisk
4. Config er avhengig av Application context for å laste inn variabler og lever derfor på et "høyt" nivå sammenlignet med i Konfig, hvor config var dataklasser som brukte System.getenv() for å hente ut miljøvariabler
5. Dataklassene kan fremdeles mockes og fakes i test
```kotlin

