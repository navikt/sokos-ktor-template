FROM bellsoft/liberica-openjdk-alpine:17@sha256:e848e1c146aad6f939ae82ee07d4125b633d3f1020ced107e3a9bf0cb2c2cba2
EXPOSE 8080:8080
COPY build/libs/*.jar app.jar
CMD ["dumb-init", "--"]
ENTRYPOINT ["java","-jar","app.jar"]