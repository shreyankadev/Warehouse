#FROM maven:3.8.4-openjdk-17 as maven-builder
#WORKDIR /app
#COPY src /app/src
#COPY pom.xml /app
#RUN mvn -f /app/pom.xml clean package -DskipTests

#FROM openjdk:17-alpine

#COPY --from=maven-builder app/target/*.jar /app.jar

#EXPOSE 8080
#CMD ["java","-jar","app.jar","server"]

FROM openjdk:17-alpine

COPY src /app/src
COPY target/dropwizard-demo-1.0-SNAPSHOT.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
CMD ["java","-jar","/app/app.jar"] CMD ["server"]