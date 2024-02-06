FROM bellsoft/liberica-openjdk-alpine:21@sha256:e01c7bd638772e0883c84819e03c6ff19ee3735311300f1a28cdcffd98f5dee8
COPY build/libs/*.jar app.jar
CMD ["dumb-init", "--"]
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java","-jar", "app.jar"]