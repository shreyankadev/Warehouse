package com.app.whse.service;

import java.util.List;
import java.util.Map;

import com.app.whse.cache.CacheService;
import com.app.whse.dao.InventoryDAO;
import com.app.whse.data.Inventory;
import com.app.whse.data.Location;

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
		} else {
			List<Inventory> list =dao.findAll();
			cache.put("allInventory",list);
			return Response.ok().build();
		}

	}
	public Inventory create(Inventory item) {
		Inventory inventory = dao.create(item);
		if(inventory!=null) {
			cache.invalidate("allInventory");
		}
		return inventory;
	}
	public Response findById(int id) {
		Inventory inv = (Inventory) cache.get("inv"+id);
        if (inv != null) {
            return Response.ok(inv).build();
        }
		inv = dao.findById(id);
		if(inv!=null) {
			cache.put("inv"+id,inv);
			return Response.ok(inv).build();
		}else {
			return Response.ok("Inventory not found").build();
		}
	}
	public Response update(int id, Inventory newItem) {
		
		Inventory inventory = dao.update(id,newItem);
		if(inventory!=null) {
			 cache.put("inv"+id, inventory);
			 cache.invalidate("allInventory");
		}
		return Response.ok(inventory).build();
	}
	public Response delete(int id) {
		cache.invalidate("inv"+id);
		cache.invalidate("allInventory");
		return dao.delete(id);
	}
	public void partialUpdateInventory(int id, Map<String, Object> updates) {
		int rowsupdated = dao.partialUpdateInventory(id,updates);
		if(rowsupdated>0) {
			cache.invalidate("inv"+id);
			cache.invalidate("allInventory");
		}
		
	}

	public Response getInventoryByType(String type) {
		List<Inventory> list = dao.getInventoryByType(type);
        return Response.ok(list).build();
	}

}
