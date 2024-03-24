package com.app.whse.resource;

import java.util.Map;

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
	
	
    public LocationResource(LocationService locationDao) {
        this.service = locationDao;
    }
    
    @GET
    public Response getALL(){
    	return service.getAllLocations();
    }
    
    @POST
    public Response creatLocaton(Location location){
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
    public void partialUpdateLocation(@PathParam("id") int id,Map<String, Object> updates) {
    	service.partialUpdateLocation(id,updates);
    }
}
