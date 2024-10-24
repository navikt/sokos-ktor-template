FROM gcr.io/distroless/java21-debian12

FROM debian:12 as BUILD
RUN apt-get update && apt-get install -y --no-install-recommends dumb-init

FROM gcr.io/distroless/java21-debian12
COPY --from=BUILD /usr/bin/dumb-init /usr/bin/dumb-init

ENV TZ="Europe/Oslo"
COPY build/libs/*.jar app.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["dumb-init", "--"]
CMD ["java","-jar", "app.jar"]
