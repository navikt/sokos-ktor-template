FROM bellsoft/liberica-openjdk-alpine:21.0.6@sha256:5f23f8082baea518a1657b420dbe19c181483255209b70af836543d6068fed8c

RUN apk update && apk add --no-cache \
  dumb-init \
  && rm -rf /var/lib/apt/lists/*

COPY build/libs/*.jar app.jar

ENV TZ="Europe/Oslo"
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["dumb-init", "--"]
CMD ["java", "-jar", "app.jar"]