package com.app.whse.resource;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.whse.cache.CacheService;
import com.app.whse.dao.LocationDAO;
import com.app.whse.data.Location;
import com.app.whse.service.LocationService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/location")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin") 
public class LocationResource {
	
	private final LocationService service;
	private static final Logger LOGGER = LoggerFactory.getLogger(LocationResource.class);
	
    public LocationResource(LocationService locationDao) {
        this.service = locationDao;
    }
    
    @GET
    public Response getALL(){
    	return service.getAllLocations();
    }
    
    @POST
    public Location creatLocaton(Location location){
    	return service.addLocation(location);
    }
    
    @GET
    @Path("/{id}")
    public Response getLocationById(@PathParam("id") int id) {
    	return service.getLocationById(id);
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteLocation(@PathParam("id") int id) {
    	return service.deleteLocation(id);
    }
    @PUT
    @Path("/{id}")
    public Response updateLocation(@PathParam("id") int id, Location location) {
    	return service.updateLocation(id, location);
    }
    
    @GET
    @Path("/type/{type}")
    public Response getLocationByType(@PathParam("type") String type) {
        return service.getLocationByType(type);
        
    }
    @PATCH
    @Path("/{id}")
    public void partialUpdateInventory(@PathParam("id") int id,Map<String, Object> updates) {
    	service.partialUpdateInventory(id,updates);
    }
}
