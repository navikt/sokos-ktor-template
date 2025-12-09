FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-21@sha256:c00faac0bbc33e04d1e1785286535067971527d43f4dfd134428742afe12870b

COPY build/install/*/lib /lib

ENV TZ="Europe/Oslo"
ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["java", "-cp", "/lib/*", "no.nav.sokos.prosjektnavn.ApplicationKt"]