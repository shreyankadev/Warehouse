package com.app.whse.service;

import java.util.List;
import java.util.Map;
import com.app.whse.cache.CacheService;
import com.app.whse.dao.LocationDAO;
import com.app.whse.data.Location;
import com.app.whse.data.Result;

import jakarta.ws.rs.core.Response;

public class LocationService {

	private final LocationDAO dao;
	private final CacheService cache;
	
	public LocationService(LocationDAO dao, CacheService cache){
		this.cache= cache;
		this.dao =dao;
	}

	public Response getAllLocations() {
		Object cachedData = cache.get("getAllLocations");
		if (cachedData != null) {
			return Response.ok(cachedData).build();
		}
		Result result = dao.getAllLocations();
		
		if(result.isSuccess()) {
			cache.put("getAllLocations",result.getDataObject());
			return Response.status(result.getCode()).entity(result.getDataObject()).build();
		}
		
		return Response.status(result.getCode()).entity(result.getMessage()).build();
	}

	public Response addLocation(Location location) {
		if(location == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No Request Body").build();
		}
		Result result = dao.addLocation(location);
		
		if(result.isSuccess()) {
			cache.invalidate("getAllLocations");
			return Response.status(result.getCode()).entity(result.getDataObject()).build();
		}
		return Response.status(result.getCode()).entity(result.getMessage()).build();
		
	}

	public Response getLocationById(int id) {
		Location location = (Location) cache.get("getLocation"+id);
		if(location!= null) {
			return Response.ok(location).build();
		}
		Result result = dao.getLocationById(id);
			
		if(result.isSuccess()) {
			cache.put("getLocation"+id,result.getDataObject());
			return Response.status(result.getCode()).entity(result.getDataObject()).build();
		}
		return Response.status(result.getCode()).entity(result.getMessage()).build();
		
	}

	public Response deleteLocation(int id) {
		
		Result result =  dao.deleteLocation(id);
		
		if(result.isSuccess()) {
			cache.invalidate("getAllLocations");
			cache.invalidate("getLocation"+id);
			return Response.status(result.getCode()).entity(result.getMessage()+result.getDataObject()).build();
		}
        
    	return Response.status(result.getCode()).entity(result.getMessage()).build();
	}

	public Response updateLocation(int id, Location location) {
		if(location == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No Request Body").build();
		}
		
		Result result = dao.updateLocation(id,location);

		if(result.isSuccess()) {
			cache.invalidate("getAllLocations");
			cache.put("getLocation"+id, result.getDataObject());
			return Response.status(result.getCode()).entity(result.getDataObject()).build();
		}
		return Response.status(result.getCode()).entity(result.getMessage()).build();
	}

	public Response partialUpdateLocation(int id, Map<String, Object> updates) {
		if(updates == null || updates.size() == 0) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No Request Body").build();
		}
		
		Result result = dao.partialUpdateLocation(id,updates);
		
		if(result.isSuccess()) {
			cache.invalidate("getLocation"+id);
			cache.invalidate("getAllLocations");
		}	
		return Response.status(result.getCode()).entity(result.getMessage()+result.getDataObject()).build();
		
	}

	public Response getLocationByType(String type) {
		Object cachedData = cache.get("typeLoc"+type);
		if (cachedData != null) {
			return Response.ok(cachedData).build();
		}
        Result result = dao.getLocationByType(type);
        
        if(result.isSuccess()) {
			cache.put("typeLoc"+type,result.getDataObject());
			return Response.status(result.getCode()).entity(result.getDataObject()).build();
		}
		return Response.status(result.getCode()).entity(result.getMessage()).build();
       
	}
	
}
