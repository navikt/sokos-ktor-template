FROM gradle:7-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM bellsoft/liberica-openjdk-alpine:17@sha256:e848e1c146aad6f939ae82ee07d4125b633d3f1020ced107e3a9bf0cb2c2cba2
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
CMD ["dumb-init", "--"]
ENTRYPOINT ["java","-jar","/app/app.jar"]

