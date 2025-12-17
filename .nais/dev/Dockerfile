FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25-dev@sha256:3262d2059adfb51d46a7bb8aea8eb3c0f5584b283d9dbeb16501cd705b47c6cf

COPY build/install/*/lib /lib

ENV TZ="Europe/Oslo"
ENV JVM_OPTS="--enable-native-access=ALL-UNNAMED"
ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["java", "-cp", "/lib/*", "no.nav.sokos.prosjektnavn.ApplicationKt"]