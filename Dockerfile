FROM bellsoft/liberica-openjdk-alpine:20@sha256:d167d993577dbbb40e21f4f7928df66196cd2cbf1e4d006bbcf94f0d610ab6ac as BUILDER
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle.kts settings.gradle.kts gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
RUN ./gradlew build -x test || return 0
COPY . .
RUN ./gradlew build -x test

FROM bellsoft/liberica-openjdk-alpine:20@sha256:d167d993577dbbb40e21f4f7928df66196cd2cbf1e4d006bbcf94f0d610ab6ac
RUN apk add --no-cache bash
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY --from=BUILDER $APP_HOME/build/libs/app.jar .
EXPOSE 8080:8080
CMD ["dumb-init", "--"]
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java","-jar", "app.jar"]