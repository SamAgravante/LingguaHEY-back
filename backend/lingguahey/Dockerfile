# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
RUN apk update && apk upgrade
COPY --from=build /app/target/*.jar ./lingguahey-aws.jar
COPY service-account.json /app/service-account.json
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/service-account.json
EXPOSE 8080
CMD ["java", "-jar", "/lingguahey-aws.jar"]