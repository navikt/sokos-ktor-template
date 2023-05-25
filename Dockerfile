FROM bellsoft/liberica-openjdk-alpine:17@sha256:de83ab3dd1f3855105bc5050b7b3064bb54e36a60cf8440d392de4fb46891937 as BUILDER
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle.kts settings.gradle.kts gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
RUN ./gradlew build -x test || return 0
COPY . .
RUN ./gradlew build -x test

FROM bellsoft/liberica-openjdk-alpine:17@sha256:de83ab3dd1f3855105bc5050b7b3064bb54e36a60cf8440d392de4fb46891937
RUN apk add --no-cache bash
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=BUILDER $APP_HOME/build/libs/app.jar .
EXPOSE 8080:8080
CMD ["dumb-init", "--"]
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java","-jar", "app.jar"]