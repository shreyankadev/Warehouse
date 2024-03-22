package com.app.whse.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.app.whse.data.Location;
import com.app.whse.data.InventoryInLocation;

public class InventoryInLocationDAO {

	private final String url;
    private final String username;
    private final String password;

    public InventoryInLocationDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    public Location findLocationIdByName(String locationName) {
        Location location = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM LOCATION WHERE LOCATION_NAME = ?");
        ) {
            preparedStatement.setString(1, locationName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    location = mapResultSetToLocation(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return location;
    }
	public InventoryInLocation findByInventoryIdAndLocationId(Long itemId, Long locationId) {
		InventoryInLocation wmInv = null ;
		try(Connection connection = DriverManager.getConnection(url,username,password);
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM WMINVENTORY WHERE INVENTORY_ID=? AND LOCATION_ID=?");
		){
			preparedStatement.setInt(1,itemId.intValue());
			preparedStatement.setInt(2,locationId.intValue());
			
			 try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                if (resultSet.next()) {
	                 
	                 wmInv= mapResultSetToInventoryInLocation(resultSet);
	                }
	            }
			 System.out.println(wmInv);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return wmInv;
	}
	public void add(InventoryInLocation newItem) {
		String sql = "INSERT INTO WMINVENTORY (INVENTORY_ID,INVENTORY_NAME,LOCATION_ID,LOCATION_NAME,COUNT) VALUES(?,?,?,?,?)";
		try (Connection connection = DriverManager.getConnection(url,username,password);
				PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
			//preparedStatement.setLong(1,newItem.getId());
			preparedStatement.setLong(1,newItem.getInventoryId());
			preparedStatement.setString(2,newItem.getInventoryName());
			preparedStatement.setLong(3,newItem.getLocationId());
			preparedStatement.setString(4,newItem.getLocationName());
			preparedStatement.setLong(5,newItem.getCount());
			
			int affectedrows= preparedStatement.executeUpdate();
			if(affectedrows==0) {
				throw new SQLException("Couldn't create InventoryInLocation,no rows affected.");
			}
			
			try(ResultSet generatedKeys = preparedStatement.getGeneratedKeys()){
				if(generatedKeys.next()) {
					newItem.setId(generatedKeys.getLong(1));
				}else {
					throw new SQLException("Couldn't create InventoryInLocation, no ID obtained.");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	public void update(InventoryInLocation existingItem) {
		String sql = "UPDATE WMINVENTORY SET INVENTORY_ID =? , INVENTORY_NAME =? ,LOCATION_ID =?, LOCATION_NAME =?, COUNT =? WHERE ID = ?";
		try(Connection connection = DriverManager.getConnection(url,username,password);
				PreparedStatement statement = connection.prepareStatement(sql)){
			
			statement.setLong(1,existingItem.getInventoryId());
			statement.setString(2,existingItem.getInventoryName());
			statement.setLong(3,existingItem.getLocationId());
			statement.setString(4,existingItem.getLocationName());
			statement.setLong(5,existingItem.getCount());
			statement.setLong(6,existingItem.getId());
			
			int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Couldn't update InventoryInLocation, no rows affected.");
            }
			
		}catch(Exception e) {
			
		}
	}
	 public List<InventoryInLocation> findAll() {
	        List<InventoryInLocation> wminventories = new ArrayList<>();
	        String sql = "SELECT * FROM WMINVENTORY";

	        try (Connection connection = DriverManager.getConnection(url, username, password);
	             Statement statement = connection.createStatement();
	             ResultSet resultSet = statement.executeQuery(sql)) {

	            while (resultSet.next()) {
	            	InventoryInLocation inventoryInLocation = mapResultSetToInventoryInLocation(resultSet);
	                wminventories.add(inventoryInLocation);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	        return wminventories;
	    }
	 private InventoryInLocation mapResultSetToInventoryInLocation(ResultSet resultSet) throws SQLException {
	        int id = resultSet.getInt("id");
	        Long inventoryId = resultSet.getLong("inventory_id");
	        String inventoryName = resultSet.getString("inventory_name");
	        Long locationId = resultSet.getLong("location_id");
	        String locationName = resultSet.getString("location_name");
	        int count = resultSet.getInt("count");

	        InventoryInLocation wminv=  new InventoryInLocation( inventoryId, inventoryName, locationId, locationName, count);
	        wminv.setId((long) id);
	        return wminv;
	    }
	 
	 
	 private Location mapResultSetToLocation(ResultSet resultSet) throws SQLException {
	        int id = resultSet.getInt("location_id");
	        String locationName = resultSet.getString("location_name");
	        String locationType = resultSet.getString("location_type");
	        String dimension = resultSet.getString("dimension");
	        int onHandQty = resultSet.getInt("onhand_qty");
	        int maxQty = resultSet.getInt("max_qty");

	        Location location=  new Location( locationName, locationType, dimension, onHandQty,maxQty);
	        location.setLocationId((long) id);
	        return location;
	    }
	 public List<InventoryInLocation> search(String query, int limit, int offset) {
	        List<InventoryInLocation> inventoryList = new ArrayList<>();
	        String sql = "SELECT * FROM WMINVENTORY WHERE LOCATION_NAME LIKE ? OR INVENTORY_NAME LIKE ? LIMIT ? OFFSET ?";
	        
	        try (Connection connection = DriverManager.getConnection(url,username,password);
	             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            
	            preparedStatement.setString(1, "%" + query + "%");
	            preparedStatement.setString(2, "%" + query + "%");
	            preparedStatement.setInt(3, limit);
	            preparedStatement.setInt(4, offset);
	            
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                while (resultSet.next()) {
	                	InventoryInLocation inventory = mapResultSetToInventoryInLocation(resultSet);
	                    
	                    inventoryList.add(inventory);
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        
	        return inventoryList;
	    }
}
