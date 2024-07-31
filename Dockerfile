FROM bellsoft/liberica-openjdk-alpine:21@sha256:ee40d83d93023b804847568d847e6540799091bd1b61322f8272de2ef369aa8b
COPY build/libs/*.jar app.jar
CMD ["dumb-init", "--"]
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java","-jar", "app.jar"]