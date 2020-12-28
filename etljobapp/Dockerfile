FROM openjdk:8-jdk-alpine
RUN addgroup -S dusanov && adduser -S dusan -G dusanov
USER dusan:dusanov
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]