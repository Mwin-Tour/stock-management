FROM eclipse-temurin:21-jdk
LABEL authors="Yves"
VOLUME /tmp
ADD target/*.jar app.jar
CMD ["java", "-jar", "/app.jar"]
EXPOSE 8080