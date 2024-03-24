
FROM openjdk:17
RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY . /app
RUN mvn clean package

FROM mysql:latest
ENV MYSQL_ROOT_PASSWORD=root
COPY /database/setup.sql /docker-entrypoint-initdb.d/
EXPOSE 8080 3306

# Start MySQL service
CMD service mysql start && \
    # Initialize MySQL database (
    mysql -u root -p root -h localhost /app/database/setup.sql && \
    # Start Dropwizard application
    java -jar target/dropwizard-demo-1.0-SNAPSHOT.jar server /app/config.yml