FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build

# cache dependencies first
COPY pom.xml .
COPY api-service/pom.xml api-service/pom.xml
COPY scraper-service/pom.xml scraper-service/pom.xml
RUN mvn -pl api-service -am -DskipTests dependency:go-offline

# then copy source
COPY . .
RUN mvn -pl api-service -am -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /build/api-service/target/api-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
