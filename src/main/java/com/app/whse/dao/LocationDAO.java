package com.app.whse.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.app.whse.data.Inventory;
import com.app.whse.data.Location;

import jakarta.ws.rs.core.Response;

public class LocationDAO {

	private final String url;
    private final String username;
    private final String password;

    public LocationDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    public List<Location> getAllLocations() {
        List<Location> locations = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url,username,password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM LOCATION")) {

            while (resultSet.next()) {
                Location location = matchResultsetToLocation(resultSet);
                
                locations.add(location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locations;
    }

    private Location matchResultsetToLocation(ResultSet resultSet) throws SQLException{
    	Location location = new Location();
    	location.setLocationId(resultSet.getLong("location_id"));
        location.setLocationName(resultSet.getString("location_name"));
        location.setLocationType(resultSet.getString("location_type"));
        location.setDimension(resultSet.getString("dimension"));
        location.setOnhandQty(resultSet.getInt("onhand_qty"));
        location.setMaxQty(resultSet.getInt("max_qty"));
		return location;
	}

	public Location addLocation(Location location) {
		String query = "INSERT INTO LOCATION (LOCATION_NAME, LOCATION_TYPE, DIMENSION, ONHAND_QTY, MAX_QTY) " +
                       "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, location.getLocationName());
            preparedStatement.setString(2, location.getLocationType());
            preparedStatement.setString(3, location.getDimension());
            preparedStatement.setInt(4, location.getOnhandQty());
            preparedStatement.setInt(5, location.getMaxQty());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long locationId = generatedKeys.getLong(1);
                        location.setLocationId(locationId);
                        return location;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return location;
    }

	public Response updateLocation(int id, Location location) {

		String query ="UPDATE LOCATION SET LOCATION_NAME =?, LOCATION_TYPE=?, DIMENSION=?, ONHAND_QTY=?, MAX_QTY =? WHERE LOCATION_ID=?";
		Response response= null;
		try(Connection connection = DriverManager.getConnection(url,username,password);
				PreparedStatement preparedStatement = connection.prepareStatement(query);){
			
			preparedStatement.setString(1,location.getLocationName());
			preparedStatement.setString(2,location.getLocationType());
			preparedStatement.setString(3,location.getDimension());
			preparedStatement.setInt(4,location.getOnhandQty());
			preparedStatement.setInt(5,location.getMaxQty());
			preparedStatement.setInt(6,id);
			
			int affectedrows = preparedStatement.executeUpdate();
			if (affectedrows == 0) {
            	response = Response.ok("Updating inventory failed, no rows affected.").build();                
            }else {
            	location.setLocationId((long)id);
            	response = Response.ok(location).build();
            }
		}catch(Exception e) {
			response = Response.status(Response.Status.NOT_FOUND).entity("Couldn't Update "+e.getMessage()).build();
		}
		
		return response;
	}

	public Response deleteLocation(int id) {
		
		String query = "DELETE FROM LOCATION WHERE LOCATION_ID =?";
		Response response =null;
		try(Connection connection = DriverManager.getConnection(url,username,password);
		PreparedStatement statement = connection.prepareStatement(query);){
			
			statement.setInt(1,id);
			int affectedrows = statement.executeUpdate();
			
			if(affectedrows ==0 ) {
				response = Response.ok("Location with id "+id+" does not exists.").build();
			}else {
				response = Response.ok("Location with id "+id +" is Successfully deleted.").build();
			}
		}catch(Exception e) {
			return Response.status(Response.Status.NOT_FOUND).entity("Couldn't delete the Inventory , plase check logs "+e.getMessage()).build();
		}
		return response;
	}
	
	public Response getLocationById(int id) {
		
		String query ="SELECT * FROM LOCATION WHERE LOCATION_ID=?";
		Response response = null;
		Location location =null;
		try(Connection connection = DriverManager.getConnection(url,username,password);
				PreparedStatement preparedStatement = connection.prepareStatement(query);){
			
			preparedStatement.setInt(1,id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
			if(resultSet.next()) {
				location = matchResultsetToLocation(resultSet);
				Response.ok(location).build();
			}else {
				response = Response.ok("Location not found").build();
			}
			}
		}catch(Exception e) {
			response = Response.status(Response.Status.NOT_FOUND).entity("Exception during query execution"+e.getMessage()).build();
		}
		
		return response;
	}
	
	 public void partialUpdateInventory(long inventoryId, Map<String, Object> updates) {
		 StringBuilder sqlBuilder = new StringBuilder("UPDATE LOCATION SET ");
	        boolean first = true;

	        for (String column : updates.keySet()) {
	            if (!first) {
	                sqlBuilder.append(", ");
	            }
	            sqlBuilder.append(column).append(" = ?");
	            first = false;
	        }
	        sqlBuilder.append(" WHERE location_id = ?");
	        
	        try (Connection connection = DriverManager.getConnection(url,username,password);
	             PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
	            
	            int parameterIndex = 1;
	            for (Object value : updates.values()) {
	                preparedStatement.setObject(parameterIndex++, value);
	            }
	            preparedStatement.setLong(parameterIndex, inventoryId);
	            
	            preparedStatement.executeUpdate();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	public List<Location> getLocationByType(String type) {
		
		String query = "SELECT * FROM LOCATION where location_type =?";
        List<Location> locations = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url,username,password);
        	PreparedStatement preparedStatement = connection.prepareStatement(query);){
        	preparedStatement.setString(1,type);
             ResultSet resultSet = preparedStatement.executeQuery();
        	
            while (resultSet.next()) {
                Location location = matchResultsetToLocation(resultSet);
                
                locations.add(location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locations;
    
	}
}
