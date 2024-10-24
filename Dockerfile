FROM gcr.io/distroless/java21-debian12
ENV TZ="Europe/Oslo"
COPY build/libs/*.jar app.jar
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["dumb-init", "--", "java", "-jar", "app.jar"]