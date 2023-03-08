FROM bellsoft/liberica-openjdk-alpine:17@sha256:e6c542a217e5b97fb5f6a216bb5bffc8d453526546d0d8a4f6cc8607c90b267b
RUN apk add --no-cache bash
EXPOSE 8080:8080
COPY build/libs/*.jar app.jar
CMD ["dumb-init", "--"]
ENTRYPOINT ["java","-jar","app.jar"]