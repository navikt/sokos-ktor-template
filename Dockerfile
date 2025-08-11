FROM bellsoft/liberica-openjdk-alpine:21.0.8@sha256:c4052811bba52c7a06ebde235c839108bf723dfab3c65066f61145a252480b16

RUN apk update && apk add --no-cache \
  dumb-init \
  && rm -rf /var/lib/apt/lists/*

COPY build/libs/*.jar app.jar

ENV TZ="Europe/Oslo"
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["dumb-init", "--"]
CMD ["java", "-jar", "app.jar"]