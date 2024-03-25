# Stage 1: Build Maven project
FROM maven:3.8.4-openjdk-17 AS maven_build
WORKDIR /app
COPY . /app
RUN mvn clean package

# Stage 2: MySQL setup
FROM mysql:latest
ENV MYSQL_ROOT_PASSWORD=root
COPY --from=maven_build /app/database/setup.sql /docker-entrypoint-initdb.d/
COPY --from=maven_build /app/target/dropwizard-demo-1.0-SNAPSHOT.jar /app/target/
COPY --from=maven_build /app/src/main/resources/config.yml /app/src/main/resources/
COPY . /app
WORKDIR /app

EXPOSE 8080 3306

# Start MySQL service and Dropwizard application
CMD service mysql start && \
    # Initialize MySQL database (
    mysql -u root -p root -h localhost /docker-entrypoint-initdb.d/setup.sql && \
    # Start Dropwizard application 
	java -jar /app/target/dropwizard-demo-1.0-SNAPSHOT.jar server /app/src/main/resourcesconfig.yml"

