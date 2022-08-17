FROM bellsoft/liberica-openjdk-alpine:17@sha256:0f48d402c392229c9457dd1b6fc595157f50d7d56331db48a8af658be6603982
EXPOSE 8080:8080
COPY build/libs/*.jar app.jar
CMD ["dumb-init", "--"]
ENTRYPOINT ["java","-jar","app.jar"]