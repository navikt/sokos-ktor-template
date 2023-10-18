FROM bellsoft/liberica-openjdk-alpine:17@sha256:30a2640e39818bbdefeb873c80f89fb67515be010193f51f63e5a5ec2af57f46
COPY build/libs/*.jar app.jar
CMD ["dumb-init", "--"]
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java","-jar", "app.jar"]