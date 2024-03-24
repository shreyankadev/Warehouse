package com.app.whse.service;

import java.util.List;

import org.slf4j.LoggerFactory;

import com.app.whse.cache.CacheService;
import com.app.whse.dao.InventoryDAO;
import com.app.whse.dao.InventoryInLocationDAO;
import com.app.whse.dao.LocationDAO;
import com.app.whse.data.Inventory;
import com.app.whse.data.InventoryInLocation;
import com.app.whse.data.Location;
import com.app.whse.data.Result;
import com.app.whse.resource.InventoryInLocationResource;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

public class InventoryInLocationService {
	
	private final InventoryInLocationDAO invLocDao;
	private final InventoryDAO inventoryDao;
	private final LocationDAO locationDao;
	private final CacheService cache;

	
	public InventoryInLocationService(InventoryInLocationDAO invLocDao, InventoryDAO inventoryDao,
			LocationDAO locationDao, CacheService cache) {
		super();
		this.invLocDao = invLocDao;
		this.inventoryDao = inventoryDao;
		this.locationDao = locationDao;
		this.cache = cache;
	}

	public Response putAway(int itemId, int locationId) {
        
		Result locationResult = locationDao.getLocationById(locationId);
        //Get inventory with id
        Result inventoryResult = inventoryDao.findById(itemId);
        Inventory inv;
        InventoryInLocation invInLoc;
        if(inventoryResult.isSuccess())
        	inv= (Inventory) inventoryResult.getDataObject();
        else {
        	return Response.status(inventoryResult.getCode()).entity(inventoryResult.getMessage()).build();
        }
        
        Location location;
        if(locationResult.isSuccess())
        	location= (Location) locationResult.getDataObject();
        else {
        	return Response.status(locationResult.getCode()).entity(locationResult.getMessage()).build();
        }
        
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
            
            // If item doesn't exist, add it to inventoryInLocationDao with count of items
        	invInLoc = new InventoryInLocation();
        	invInLoc.setInventoryId(itemId);
        	invInLoc.setInventoryName(inv.getName());
        	invInLoc.setLocationId(location.getLocationId());
        	invInLoc.setLocationName(location.getLocationName());
        	invInLoc.setCount(inv.getCount());
        	Result result = invLocDao.create(invInLoc);
        	if(result.isSuccess()) {
        		invInLoc = (InventoryInLocation) result.getDataObject();
        	}
            
            //update inventory status
            inv.setStatus("PUTAWAY");
            inventoryDao.updateStatus(inv.getId());
            location.setOnhandQty(Math.addExact(inv.getCount(),location.getOnhandQty()));
            locationDao.updateLocation(location.getLocationId(),location);

        } else {
            //throw exception when location is not found
        	return Response.status(locationResult.getCode()).entity(locationResult.getMessage()+locationResult.getDataObject()).build();
        }
       
        return Response.ok(invInLoc).build();
        
    }

	public List<InventoryInLocation> findAll() {
		return invLocDao.findAll();
	}

	public Response search(String inventoryName, String locationName, int count,int inventoryId,int locationId, int limit, int offset) {
		Result result = invLocDao.search(inventoryName,locationName,count,inventoryId,locationId,limit,offset);
		if(result.isSuccess()) {
			return Response.ok(result.getDataObject()).build();
		}
		return Response.status(404).entity(result.getMessage()).build();
	}

}
