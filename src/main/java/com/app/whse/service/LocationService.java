package com.app.whse.service;

import java.util.List;
import java.util.Map;

import com.app.whse.cache.CacheService;
import com.app.whse.dao.LocationDAO;
import com.app.whse.data.Location;

import jakarta.ws.rs.core.Response;

public class LocationService {

	private final LocationDAO dao;
	private final CacheService cache;
	
	public LocationService(LocationDAO dao, CacheService cache){
		this.cache= cache;
		this.dao =dao;
	}

	public Response getAllLocations() {
		List<Location> list = (List<Location>) cache.get("getAllLocations");
		if(list!= null) {
			return Response.ok(list).build();
		}
		list = dao.getAllLocations();
		cache.put("getAllLocations",list);
		return Response.ok(list).build();
	}

	public Location addLocation(Location location) {
		
		Location loc = dao.addLocation(location);
		if(loc!= null)
			cache.invalidate("getAllLocations");
		return loc;
	}

	public Response getLocationById(int id) {
		Location location = (Location) cache.get("getLocation"+id);
		if(location!= null) {
			return Response.ok(location).build();
		}
		Response response = dao.getLocationById(id);
		cache.put("getLocation"+id, response.readEntity(Location.class));
		return response;
	}

	public Response deleteLocation(int id) {
		cache.invalidate("getLocation"+id);
		cache.invalidate("getAllLocations");
		return dao.deleteLocation(id);
	}

	public Response updateLocation(int id, Location location) {
		cache.invalidate("getLocation"+id);
		cache.invalidate("getAllLocations");
		return dao.updateLocation(id,location);
	}

	public void partialUpdateInventory(int id, Map<String, Object> updates) {
		cache.invalidate("getLocation"+id);
		cache.invalidate("getAllLocations");
		dao.partialUpdateInventory(id,updates);
		
	}

	public Response getLocationByType(String type) {
        List<Location> list = dao.getLocationByType(type);

        return Response.ok(list).build();
       
	}
	
}
