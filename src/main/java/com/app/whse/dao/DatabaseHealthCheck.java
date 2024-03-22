package com.app.whse.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;


public class DatabaseHealthCheck extends HealthCheck {
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHealthCheck.class);
    public DatabaseHealthCheck(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    protected Result check() throws Exception {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            // Check if the connection is valid
        	LOGGER.info("connection "+connection);
            if (connection.isValid(1)) {
                return Result.healthy("The service is running smoothly");
            } else {
                return Result.unhealthy("Database connection is not valid");
            }
        } catch (Exception e) {
            return Result.unhealthy("Unable to connect to database: " + e.getMessage());
        }
    }
}

