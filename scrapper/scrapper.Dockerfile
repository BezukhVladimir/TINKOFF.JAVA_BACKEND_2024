FROM openjdk:21
WORKDIR /app
COPY . /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/scrapper.jar"]
