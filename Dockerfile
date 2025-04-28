FROM bellsoft/liberica-openjdk-alpine:21.0.7@sha256:0994b3c88c95db93771f4bc6fc23db9833ac62843f642c3b83e2af2173d139c5

RUN apk update && apk add --no-cache \
  dumb-init \
  && rm -rf /var/lib/apt/lists/*

COPY build/libs/*.jar app.jar

ENV TZ="Europe/Oslo"
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["dumb-init", "--"]
CMD ["java", "-jar", "app.jar"]