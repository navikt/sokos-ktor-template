FROM bellsoft/liberica-openjdk-alpine:21@sha256:502484ec841aba2427f9194168949eaa087be994959da647ce5edbbb82caf720
COPY build/libs/*.jar app.jar
CMD ["dumb-init", "--"]
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java","-jar", "app.jar"]