package com.app.whse.resource;


import com.app.whse.dao.DatabaseHealthCheck;
import com.codahale.metrics.health.HealthCheck.Result;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/healthcheck")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin") 
public class HealthCheckResource {

    private final DatabaseHealthCheck healthCheck;
    
    public HealthCheckResource(DatabaseHealthCheck healthCheck) {
        this.healthCheck = healthCheck;
    }

    @GET
    public Result check() {
        return healthCheck.execute();
    }
}

