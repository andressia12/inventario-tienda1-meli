FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/tienda1-0.0.1-SNAPSHOT.jar tienda1-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "tienda1-0.0.1-SNAPSHOT.jar"]