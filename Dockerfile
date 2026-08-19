FROM eclipse-temurin:25-jdk-alpine
COPY target/*.jar singlarr.jar
EXPOSE 3600
ENTRYPOINT ["java", "-jar", "/singlarr.jar"]