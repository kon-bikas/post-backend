FROM eclipse-temurin:17.0.17_10-jdk-ubi9-minimal AS builder
WORKDIR /opt/app

COPY .mvn/ .mvn
COPY mvnw ./
COPY pom.xml ./
COPY ./src ./src

RUN ./mvnw  package -Dmaven.test.skip

FROM eclipse-temurin:17.0.17_10-jre-ubi9-minimal
WORKDIR /opt/app

RUN groupadd appgroup && useradd -g appgroup appuser

EXPOSE 8080

COPY --from=builder /opt/app/target/postr-0.0.1-SNAPSHOT.jar ./application.jar

RUN chown appuser /opt/app/application.jar
USER appuser

ENTRYPOINT ["java", "-jar", "application.jar"]




