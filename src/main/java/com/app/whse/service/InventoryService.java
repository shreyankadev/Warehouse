package com.app.whse.service;

import java.util.Map;
import com.app.whse.cache.CacheService;
import com.app.whse.dao.InventoryDAO;
import com.app.whse.data.Inventory;
import com.app.whse.data.Result;
import jakarta.ws.rs.core.Response;

public class InventoryService {


	private final InventoryDAO dao;
	private final CacheService cache;
	
	public InventoryService(InventoryDAO inventoryDAO,CacheService cache) {

		this.dao = inventoryDAO;
		this.cache = new CacheService();

	}

	public Response findAll() {

		Object cachedData = cache.get("allInventory");
		if (cachedData != null) {
			return Response.ok(cachedData).build();
		}
		
		Result result =dao.findAll();
		if(result.isSuccess()) {
			cache.put("allInventory",result.getDataObject());
			return Response.status(result.getCode()).entity(result.getDataObject()).build();
		}
		
		return Response.status(result.getCode()).entity(result.getMessage()).build();
		

	}
	
	public Response create(Inventory item) {
		if(item == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No Request Body").build();
		}
		Result result = dao.create(item);
		if(result.isSuccess()) {
			cache.invalidate("allInventory");
			return Response.status(result.getCode()).entity(result.getDataObject()).build();
		}
		return Response.status(result.getCode()).entity(result.getMessage()).build();
	}
	
	public Response findById(int id) {
		Inventory inv = (Inventory) cache.get("inv"+id);
        if (inv != null) {
            return Response.ok(inv).build();
        }
		Result result = dao.findById(id);
		if(result.isSuccess()) {
			cache.put("inv"+id,result.getDataObject());
			return Response.status(result.getCode()).entity(result.getDataObject()).build();
		}
		return Response.status(result.getCode()).entity(result.getMessage()).build();
	}
	
	public Response update(int id, Inventory newItem) {
		if(newItem == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No Request Body").build();
		}
		Result result = dao.update(id,newItem);
		
		if(result.isSuccess()) {
			cache.invalidate("allInventory");
			cache.put("inv"+id, result.getDataObject());
			return Response.status(result.getCode()).entity(result.getDataObject()).build();
		}
		return Response.status(result.getCode()).entity(result.getMessage()).build();
			
	}
	
	public Response delete(int id) {
		Result result = dao.delete(id);
		if(result.isSuccess()) {
			cache.invalidate("allInventory");
			cache.invalidate("inv"+id);
			return Response.status(result.getCode()).entity(result.getMessage()+result.getDataObject()).build();
		}
        
    	return Response.status(result.getCode()).entity(result.getMessage()).build();	
	}
	
	public Response partialUpdateInventory(int id, Map<String, Object> updates) {
		if(updates == null || updates.size() == 0) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No Request Body").build();
		}
		Result result = dao.partialUpdateInventory(id,updates);
		if(result.isSuccess()) {
			cache.invalidate("inv"+id);
			cache.invalidate("allInventory");
		}	
		return Response.status(result.getCode()).entity(result.getMessage()+result.getDataObject()).build();
		
		
	}

	public Response getInventoryByType(String type) {
		Object cachedData = cache.get("typeInv"+type);
		if (cachedData != null) {
			return Response.ok(cachedData).build();
		}
		Result result = dao.getInventoryByType(type);
		if(result.isSuccess()) {
			cache.put("typeInv"+type,result.getDataObject());
			return Response.status(result.getCode()).entity(result.getDataObject()).build();
		}
		return Response.status(result.getCode()).entity(result.getMessage()).build();
	}

}
