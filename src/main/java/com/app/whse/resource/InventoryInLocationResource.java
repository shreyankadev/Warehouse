package com.app.whse.resource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.whse.data.InventoryInLocation;
import com.app.whse.service.InventoryInLocationService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
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
	
	private final InventoryInLocationService service;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryInLocationResource.class);
	
	public InventoryInLocationResource(InventoryInLocationService service) {
        this.service = service;
       
    }
	
	@GET
	public List<InventoryInLocation> findAll() {
		return service.findAll();
	}
	
	@POST
    @Path("/putaway/{itemId}/{locationId}")
    public Response putAway(@PathParam("itemId") int itemId, @PathParam("locationId") int locationId) {
		return service.putAway(itemId,locationId);
	}
	
 	@GET
    @Path("/search")
    public Response searchLocations(@QueryParam("inventoryname") String inventoryname,
                                    @QueryParam("locationname") String locationname,
                                    @QueryParam("count") int count,
                                    @QueryParam("inventoryid") int inventoryId,
                                    @QueryParam("locationid") int locationId,
                                    @QueryParam("limit") @DefaultValue("10") int limit,
                                    @QueryParam("offset") @DefaultValue("0") int offset) {
       
          return service.search(inventoryname, locationname, count,inventoryId,locationId, limit, offset);
            
        
    }
}
