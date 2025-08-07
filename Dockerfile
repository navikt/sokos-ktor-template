FROM bellsoft/liberica-openjdk-alpine:21-37@sha256:418df3c295b2bbbf129ef85c562a16b21472ab9ff7246f8ef35e53892dd3c1a5

RUN apk update && apk add --no-cache \
  dumb-init \
  && rm -rf /var/lib/apt/lists/*

COPY build/libs/*.jar app.jar

ENV TZ="Europe/Oslo"
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["dumb-init", "--"]
CMD ["java", "-jar", "app.jar"]