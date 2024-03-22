package com.app.whse.resource;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.whse.cache.CacheService;
import com.app.whse.dao.InventoryDAO;
import com.app.whse.data.Inventory;
import com.app.whse.service.InventoryService;

import jakarta.ws.rs.PATCH;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/inventory")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("admin") 
public class InventoryResource {

    private final InventoryService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryResource.class);
    public InventoryResource(InventoryService service) {
        this.service = service;
    }

    @GET
    public Response getAllItems() {
    	return service.findAll();
    }

    @POST
    public Inventory addItem(Inventory item) {
        return service.create(item);
    }

    @GET
    @Path("/{id}")
    public Response getItemById(@PathParam("id") int id) {
        return service.findById(id);
    }

    @PUT
    @Path("/{id}")
    public Response updateItem(@PathParam("id") int id, Inventory newItem) {
        return service.update(id, newItem);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteItem(@PathParam("id") int id) {
        return service.delete(id);
    }
    
    @GET
    @Path("/type/{type}")
    public Response getInvnetoryByType(@PathParam("type") String type) {
        return service.getInventoryByType(type);
        
    }
    
    @PATCH
    @Path("/{id}")
    public void partialUpdateInventory(@PathParam("id") int id,Map<String, Object> updates) {
        service.partialUpdateInventory(id,updates);
    }
}

