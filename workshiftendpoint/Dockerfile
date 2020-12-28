FROM openjdk:8-jdk-alpine
RUN addgroup -S dusanov && adduser -S dusan -G dusanov
USER dusan:dusanov
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","me.dusanov.etl.workshift.workshiftendpoint.WorkshiftendpointApplication"]