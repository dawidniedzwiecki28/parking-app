FROM openjdk:21-jdk-bookworm as builder
WORKDIR application
ARG JAR_PATH
COPY ${JAR_PATH} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:21-jdk-bookworm

RUN useradd --no-create-home --shell=/bin/false nonroot
USER nonroot

WORKDIR application
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

ENTRYPOINT ["java"]
