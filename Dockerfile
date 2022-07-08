FROM eclipse-temurin:17 AS base
WORKDIR /usr/src/app
COPY gradle gradle
COPY gradle.properties gradle.properties
COPY gradlew gradlew
COPY settings.gradle settings.gradle
RUN ./gradlew --version

FROM base AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew shadowJar --no-daemon

FROM bellsoft/liberica-openjdk-alpine:17@sha256:e848e1c146aad6f939ae82ee07d4125b633d3f1020ced107e3a9bf0cb2c2cba2
EXPOSE 8080:8080
COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar
CMD ["dumb-init", "--"]
ENTRYPOINT ["java","-jar","/app.jar"]

