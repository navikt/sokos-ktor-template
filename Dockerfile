FROM gradle:7-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM navikt/java:17
EXPOSE 8080:8080
COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar
CMD ["dumb-init", "--"]
ENTRYPOINT ["java","-jar","/app.jar"]

