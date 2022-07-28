FROM bellsoft/liberica-openjdk-alpine:17
EXPOSE 8080:8080
COPY build/libs/*.jar app.jar
CMD ["dumb-init", "--"]
ENTRYPOINT ["java","-jar","app.jar"]