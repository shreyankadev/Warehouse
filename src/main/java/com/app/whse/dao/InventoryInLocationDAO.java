package com.app.whse.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.whse.data.InventoryInLocation;
import com.app.whse.data.Result;

public class InventoryInLocationDAO {

	private final String url;
    private final String username;
    private final String password;
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryInLocationDAO.class);
    public InventoryInLocationDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
 
	public InventoryInLocation findByInventoryIdAndLocationId(String itemName, String locationName) {
		InventoryInLocation wmInv = null ;
		try(Connection connection = DriverManager.getConnection(url,username,password);
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM INVENTORYLOCATION WHERE INVENTORYNAME=? AND LOCATIONNAME=?");
		){
			preparedStatement.setString(1,itemName);
			preparedStatement.setString(2,locationName);
			
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
	
	
	 public List<InventoryInLocation> findAll() {
	        List<InventoryInLocation> wminventories = new ArrayList<>();
	        String sql = "SELECT * FROM INVENTORYLOCATION";

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

	 public Result search(String inventoryname, String locationname, int count,int inventoryId,int locationId ,int limit, int offset) {
	        List<InventoryInLocation> locations = new ArrayList<>();
	        Result result = new Result();
	        try (Connection conn = DriverManager.getConnection(url,username,password)) {
	            String sql = "SELECT * FROM INVENTORYLOCATION WHERE inventoryname LIKE ? AND locationname LIKE ? AND count LIKE ? AND inventoryid LIKE ? AND locationid LIKE ? LIMIT ? OFFSET ?";
	            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	                stmt.setString(1, "%" + inventoryname + "%");
	                stmt.setString(2, "%" + locationname + "%");
	                stmt.setString(3, "%" + count + "%");
	                stmt.setString(4, "%" + inventoryId + "%");
	                stmt.setString(5, "%" + locationId + "%");
	                stmt.setInt(6, limit);
	                stmt.setInt(7, offset);
	                try (ResultSet rs = stmt.executeQuery()) {
	                    while (rs.next()) {
	                    	InventoryInLocation inventoryInLocation = mapResultSetToInventoryInLocation(rs);
	                        result.setSuccess(true);
	                        locations.add(inventoryInLocation);
	                    }
	                    if(result.isSuccess()) {
	                    	result.setDataObject(locations);
	                    }
	                }
	            }
	        }catch(Exception e) {
	        	e.printStackTrace();
	        	result.setMessage("No data");
	        }
	        return result;
	    }

	public Result update(InventoryInLocation invInLoc) {

		String query ="UPDATE INVENTORYLOCATION SET INVENTORYID =?, INVENTORYNAME=?, LOCATIONID=?, LOCATIONNAME=?, COUNT =? WHERE ID=?";
		Result result = null;
		try(Connection connection = DriverManager.getConnection(url,username,password);
				PreparedStatement preparedStatement = connection.prepareStatement(query);){
			
			preparedStatement.setInt(1,invInLoc.getInventoryId());
			preparedStatement.setString(2,invInLoc.getInventoryName());
			preparedStatement.setInt(3,invInLoc.getLocationId());
			preparedStatement.setString(4,invInLoc.getLocationName());
			preparedStatement.setInt(5,invInLoc.getCount());
			preparedStatement.setInt(6,invInLoc.getId());
			
			 int affectedRows = preparedStatement.executeUpdate();
	            if (affectedRows > 0) {
	            	result = new Result(true,200,"Updated successfully",invInLoc);
	            } else {
	            	result = new Result(false,404,"location NOT FOUND",null);
	            }
	        } catch (SQLException e) {
	        	
	        	e.printStackTrace();
	            result = new Result(false,400,"Update failed "+e.getMessage(),null);
	        
	        }
	        
	        return result;
	}

	public Result create(InventoryInLocation invInLoc) {

		String query ="INSERT INTO INVENTORYLOCATION( INVENTORYID, INVENTORYNAME, LOCATIONID, LOCATIONNAME, COUNT ) VALUES( ?,?,?,?,?)";
		Result result = null;
		try(Connection connection = DriverManager.getConnection(url,username,password);
				PreparedStatement preparedStatement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);){
			
			preparedStatement.setInt(1,invInLoc.getInventoryId());
			preparedStatement.setString(2,invInLoc.getInventoryName());
			preparedStatement.setInt(3,invInLoc.getLocationId());
			preparedStatement.setString(4,invInLoc.getLocationName());
			preparedStatement.setInt(5,invInLoc.getCount());
			
			
			 int affectedRows = preparedStatement.executeUpdate();
			 if (affectedRows == 0) {
	            	result = new Result(false,200,"Creation failed",invInLoc);
	            }
			 if (affectedRows == 1) {
	            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
	                if (generatedKeys.next()) {
	                	invInLoc.setId(generatedKeys.getInt(1));

	                    result = new Result(true,200,"Created successfully",invInLoc);
	                } else {
	                	result = new Result(false,200,"Creation failed",invInLoc);
	                }
	            
			 }
	        } catch(NullPointerException e1) {
	        	LOGGER.error("Exception occured "+e1.getMessage());
	            e1.printStackTrace();           
	            result = new Result(false,500,"Please populate other fileds of InventoryLocation(inventoryname,inventoryid,locationname,locationId,count)"+e1.getMessage(),null);
	        } catch (SQLException e) {
	        	LOGGER.error("Exception occured "+e.getMessage());
	            e.printStackTrace();           
	            result = new Result(false,400,"Creation failed "+e.getMessage(),null);
	        }

	        return result;
	}
	
	 private InventoryInLocation mapResultSetToInventoryInLocation(ResultSet resultSet) throws SQLException {
	        int id = resultSet.getInt("id");
	        int inventoryId = resultSet.getInt("inventoryid");
	        String inventoryName = resultSet.getString("inventoryname");
	        int locationId = resultSet.getInt("locationid");
	        String locationName = resultSet.getString("locationname");
	        int count = resultSet.getInt("count");

	        InventoryInLocation wminv=  new InventoryInLocation( inventoryId, inventoryName, locationId, locationName, count);
	        wminv.setId(id);
	        return wminv;
	    }
	 
	
}
