FROM gradle:latest@sha256:58f56046624ac0b6173c2ccd954f18ca4a3aab6033efd606abef10e1f5b98ac2 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM navikt/java:17
EXPOSE 8080:8080
COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar
CMD ["dumb-init", "--"]
ENTRYPOINT ["java","-jar","/app.jar"]

