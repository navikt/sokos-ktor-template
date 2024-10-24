FROM gcr.io/distroless/java21-debian12

RUN apk update && apk add --no-cache \
  dumb-init \
  && rm -rf /var/lib/apt/lists/* \

ENV TZ="Europe/Oslo"
COPY build/libs/*.jar app.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["dumb-init", "--"]
CMD ["java","-jar", "app.jar"]