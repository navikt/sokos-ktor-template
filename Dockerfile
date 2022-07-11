FROM bellsoft/liberica-openjdk-alpine:17@sha256:5cb35778a344038a2b68d0e36cd3b4f3bd21c7dcad2843bc70b0ba548a8a3869
EXPOSE 8080:8080
COPY build/libs/*.jar app.jar
CMD ["dumb-init", "--"]
ENTRYPOINT ["java","-jar","app.jar"]