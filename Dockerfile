FROM eclipse-temurin:25-jdk-alpine
COPY target/*.jar singlarr.jar
EXPOSE 3900
ENTRYPOINT ["java", "-jar", "/singlarr.jar"]