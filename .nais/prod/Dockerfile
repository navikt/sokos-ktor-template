FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25@sha256:6b58c392e44c5d94b31c01e82db6436fabb7a71a2d68da93f7ac14f67508ac0e

COPY build/install/*/lib /lib

ENV TZ="Europe/Oslo"
ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["java", "-cp", "/lib/*", "no.nav.sokos.prosjektnavn.ApplicationKt"]