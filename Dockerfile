FROM bellsoft/liberica-openjdk-alpine:17@sha256:a4fc8f6076c4321d40e69e635e24c776fc74edd5dcc4012c4a721a55c7664d62
EXPOSE 8080:8080
COPY build/libs/*.jar app.jar
CMD ["dumb-init", "--"]
ENTRYPOINT ["java","-jar","app.jar"]