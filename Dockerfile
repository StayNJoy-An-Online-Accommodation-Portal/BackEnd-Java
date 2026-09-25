(
echo FROM eclipse-temurin:21-jre
echo WORKDIR /app
echo COPY target/*.jar app.jar
echo EXPOSE 8081
echo ENTRYPOINT ["java", "-jar", "app.jar"]
) > Dockerfile