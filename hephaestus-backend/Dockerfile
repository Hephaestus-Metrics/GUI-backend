FROM openjdk:16-jdk-alpine
COPY /target/hephaestus-backend-0.0.1-SNAPSHOT.jar app.jar
COPY . /opt
WORKDIR /opt
ENTRYPOINT ["java","-jar","/app.jar"]