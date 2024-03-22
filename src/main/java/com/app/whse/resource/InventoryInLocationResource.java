package com.app.whse.resource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.whse.cache.CacheService;
import com.app.whse.dao.InventoryDAO;
import com.app.whse.dao.InventoryInLocationDAO;
import com.app.whse.dao.LocationDAO;
import com.app.whse.data.Inventory;
import com.app.whse.data.InventoryInLocation;
import com.app.whse.data.Location;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/inventory-location")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin") 
public class InventoryInLocationResource {
	
	private final InventoryInLocationDAO inventoryInLocationDao;
	private final InventoryDAO inventoryDao;
	private final LocationDAO locationDao;
	private final CacheService cache;
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryInLocationResource.class);
	
	public InventoryInLocationResource(InventoryInLocationDAO inventoryInLocationDao, InventoryDAO invDao,LocationDAO locationDao, CacheService cache) {
        this.inventoryInLocationDao = inventoryInLocationDao;
        this.inventoryDao = invDao;
        this.locationDao = locationDao;
        this.cache =cache;
    }
	
	@GET
	public List<InventoryInLocation> findAll() {
		return inventoryInLocationDao.findAll();
	}
	
	@POST
    @Path("/putaway/{itemId}/{locationName}")
    public Response putAway(@PathParam("itemId") Long itemId, @PathParam("locationName") String locationName) {
        // Get location based on the location name
		LoggerFactory.getLogger(InventoryInLocationResource.class).info("Putaway start");
		LOGGER.info(itemId+" "+locationName);
        Location location = inventoryInLocationDao.findLocationIdByName(locationName);
        //Get inventory with id
        Inventory inv = inventoryDao.findById(itemId.intValue());
        InventoryInLocation wminv = null;
        if(inv.getStatus().equals("PUTAWAY")) {
        	return Response.ok("Inventory is already in location, PUTAWAY is performed for this!").build();
        }
        if (location != null) {
            // Check if the item already exists in wminventory
        	if(location.getOnhandQty()>=location.getMaxQty()) {
            	return Response.ok("Location is full, can't perform the operation").build();
            }else if(Math.addExact(inv.getCount(),location.getOnhandQty())>location.getMaxQty()) {
            	return Response.ok("Location cannot hold the qty of"+inv.getCount()).build();
            }
            InventoryInLocation invInLoc = inventoryInLocationDao.findByInventoryIdAndLocationId(itemId, location.getLocationId());
            
            if (invInLoc != null) {
                // If item already exists, increment the count
                invInLoc.setCount(invInLoc.getCount() + inv.getCount());
                inventoryInLocationDao.update(invInLoc);
                wminv = invInLoc;
                
            } else {
                // If item doesn't exist, add it to inventoryInLocationDao with count of items
            	wminv = new InventoryInLocation();
            	wminv.setInventoryId(itemId);
            	wminv.setInventoryName(inv.getName());
            	wminv.setLocationId(location.getLocationId());
            	wminv.setLocationName(location.getLocationName());
            	wminv.setCount(inv.getCount());
            	inventoryInLocationDao.add(wminv);
                
            }
            //update inventory status
            inv.setStatus("PUTAWAY");
            Inventory inventory = inventoryDao.update(inv.getId(),inv);
            location.setOnhandQty(Math.addExact(inv.getCount(),location.getOnhandQty()));
            Response rl =locationDao.updateLocation(location.getLocationId().intValue(),location);

        } else {
            //throw exception when location is not found
            throw new NotFoundException("Location not found: " + locationName);
        }
        LOGGER.info("Putaway exit");
        return Response.ok(wminv).build();
    }
	
	@GET
    @Path("/search")
    public Response search(
            @QueryParam("query") String query,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("offset") @DefaultValue("0") int offset) {
        // search operation with pagination
		
		List<InventoryInLocation> result = (List<InventoryInLocation>) cache.get("InventoryInLocation"+query);
		if(result != null) {
			return Response.ok(result).build();
		}
        List<InventoryInLocation> searchResults = inventoryInLocationDao.search(query, limit, offset);
        cache.put("InventoryInLocation"+query,searchResults);
        return Response.ok(searchResults).build();
    }
}
