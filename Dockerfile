FROM bellsoft/liberica-openjdk-alpine:17@sha256:de83ab3dd1f3855105bc5050b7b3064bb54e36a60cf8440d392de4fb46891937
RUN apk add --no-cache bash
EXPOSE 8080:8080
COPY build/libs/*.jar app.jar
CMD ["dumb-init", "--"]
ENTRYPOINT ["java","-jar","app.jar"]