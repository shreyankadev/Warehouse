# Warehouse

Repository name Warehouse
project name : dropwizard-demo

The project is a Java-based web application built using Dropwizard and MySQL. It provides RESTful APIs for managing inventory and warehouse locations.<b> The APIs allow users to perform operations such as adding, updating, and deleting inventory items, managing warehouse locations, and performing putaway operations to allocate inventory to specific locations.</b> The application also supports features like pagination, authentication, and caching for improved performance.

# Table of Contents
- Requirement
- Installation
- Configuration
- API documentation
# Requirements
- project requires Java 17 , dropwizard 4.0.7
- maven version 3.8.4 or greater
- mysql database version 8.0.17
# Installation

## Installation local setup

-Clone the repository
git clone https://github.com/shreyankadev/Warehouse.git

//Navigate to the project directory
cd dropwizard-demo

// Build the project (if applicable)
mvn clean package

// Run the project
java -jar target/dropwizard-demo-1.0-SNAPSHOT.jar

--create database table and data required using setup.sql file

## Containerize using docker

git clone https://github.com/shreyankadev/Warehouse.git

//Navigate to the project directory
cd dropwizard-demo

mvn clean package

//create image myapp , tag 1.0
docker build -t myapp:1.0 .

//Run command to create and start container
docker run -it --name mycontainer myapp:1.0 /bin/bash

//find out ip address virtual system, under "NetworkSettings": { "IPAddress" :""}
docker inspect mycontainer

# Configuration

- open postman or any Rest Client
- Refer API documentation to send any request
- Give Basic authorization admin/admin

# API Doumentation

Document of all available APIs are provided along with Postive and negative test-case/examples
click on drop-down on right-hand-side-top of each example to see other cases

## Link to documentation
https://documenter.getpostman.com/view/33816603/2sA35Bb42U




