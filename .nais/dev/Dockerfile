FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-21-dev@sha256:5c17090a632c5b919d21077d1962e45ba7d5f7c884f00cb09e18e86ee56984d5

COPY build/install/*/lib /lib

ENV TZ="Europe/Oslo"
ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["java", "-cp", "/lib/*", "no.nav.sokos.prosjektnavn.ApplicationKt"]