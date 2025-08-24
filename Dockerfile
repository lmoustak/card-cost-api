FROM eclipse-temurin:21-ubi10-minimal
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} card-cost-api.jar
ENTRYPOINT ["java", "-jar", "card-cost-api.jar"]