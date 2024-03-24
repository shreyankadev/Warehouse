package com.app.whse.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.app.whse.data.Inventory;
import com.app.whse.data.Result;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class InventoryDAO {

    private final String url;
    private final String username;
    private final String password;
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryDAO.class);
    public InventoryDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Result findAll() {
    	LOGGER.info("findAll start");
        List<Inventory> inventories = new ArrayList<>();
        Result result = new Result();
        String sql = "SELECT * FROM Inventory";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Inventory inventory = mapResultSetToInventory(resultSet);
                result.setSuccess(true);
                inventories.add(inventory);
            }
            if(result.isSuccess())
            	result.setDataObject(inventories);
            else {
				result.setMessage("NO DATA");
				result.setCode(404);
			}
        } catch (SQLException e) {
        	LOGGER.error("Exception occured "+e.getMessage());
            e.printStackTrace();
            result = new Result(false,404,"Getall Failure "+e.getMessage(),null);
        }
        LOGGER.info("findAll end");
        return result;
    }

    public Result findById(int id) {
        Inventory inventory = null;
        String sql = "SELECT * FROM Inventory WHERE id = ?";
        Result result = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    inventory = mapResultSetToInventory(resultSet);
                    result = new Result(true,200,"Retrieval successfully",inventory);
                }else {
                	result = new Result(false,404,"INVENTORY NOT FOUND ",null);
                }
            }
        }catch (SQLException e) {
        	LOGGER.error("Exception occured "+e.getMessage());
            e.printStackTrace();          
            result = new Result(false,400,"NOT FOUND "+e.getMessage(),null);
        }

        return result;
    }

    public Result create(Inventory inventory) {
        String sql = "INSERT INTO Inventory (name, dimensions, volume, type,count,status) VALUES (?, ?, ?, ?,?,?)";
        Result result = null;
        Float volume = inventory.getVolume();
        if(volume == null)
        	volume = 0.0f;
        	
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, inventory.getName());
            statement.setString(2, inventory.getDimensions());
            statement.setFloat(3, volume);
            statement.setString(4, inventory.getType());
            statement.setInt(5, inventory.getCount());
            statement.setString(6, "RECEIVED");

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
            	result = new Result(false,200,"Creation failed",inventory);
            }
            if (affectedRows == 1) {
            ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    inventory.setId(generatedKeys.getInt(1));
                    inventory.setStatus("RECEIVED");
                    result = new Result(true,200,"Created successfully",inventory);
                } else {
                	result = new Result(false,200,"Creation failed",inventory);
                }
            
            }
        } catch(NullPointerException e1) {
        	LOGGER.error("Exception occured "+e1.getMessage());
            e1.printStackTrace();           
            result = new Result(false,500,"Please populate other fileds of Inventory(name,dimensions,type,volume,count)"+getConstraintMessage(e1.getMessage()),null);
        } catch (SQLException e) {
        	LOGGER.error("Exception occured "+e.getMessage());
            e.printStackTrace();           
            result = new Result(false,400,"Creation failed "+getConstraintMessage(e.getMessage()),null);
        }

        return result;
    }
    
 

    public Result update(int id ,Inventory inventory) {
        String sql = "UPDATE Inventory SET name = ?, dimensions = ?, volume = ?, type = ? ,count =? WHERE id = ?";
        Result result = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, inventory.getName());
            statement.setString(2, inventory.getDimensions());
            statement.setFloat(3, inventory.getVolume());
            statement.setString(4, inventory.getType());
            statement.setInt(5,inventory.getCount());
            statement.setInt(6, id);
            

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
            	inventory.setId(id);
            	   result = new Result(true,200,"Updated successfully",inventory);
            } else {
            	result = new Result(false,404,"Inventory NOT FOUND",null);
            }
        } catch (SQLException e) {
        	LOGGER.info("Couldn't update Inventory , please check logs "+e.getMessage());
        	e.printStackTrace();
            result = new Result(false,400,"Update failed "+getConstraintMessage(e.getMessage()),null);
        
        }
        
        return result;
    }
    public Result updateStatus(int id ) {
        String sql = "UPDATE Inventory SET status =? WHERE id = ?";
        Result result = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "PUTAWAY");
            statement.setInt(2, id);
   
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
            	
            	   result = new Result(true,200,"Updated successfully",null);
            } else {
            	result = new Result(false,404,"Inventory NOT FOUND",null);
            }
        } catch (SQLException e) {
        	LOGGER.info("Couldn't update Inventory , please check logs "+e.getMessage());
        	e.printStackTrace();
            result = new Result(false,400,"Update failed "+getConstraintMessage(e.getMessage()),null);
        
        }
        
        return result;
    }
    public Result delete(int id) {
        String sql = "DELETE FROM Inventory WHERE id = ?";
        
        Result result = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
            	
            	 result = new Result(true,200,"deletion successful ",id);
            } else {
            	result = new Result(false,200,"deletion failed inventory NOT FOUND",id);
            }
        } catch (SQLException e) {
        	LOGGER.info("Couldn't update Inventory , please check logs "+e.getMessage());
        	e.printStackTrace();
            result = new Result(false,400,"Deletion failed "+getConstraintMessage(e.getMessage()),null);
        
        }
        
        return result;
    }

    public Result partialUpdateInventory(long inventoryId, Map<String, Object> updates) {
    	StringBuilder sqlBuilder = new StringBuilder("UPDATE inventory SET ");
        boolean first = true;
        Result result = null;
        for (String column : updates.keySet()) {
            if (!first) {
                sqlBuilder.append(", ");
            }
            sqlBuilder.append(column).append(" = ?");
            first = false;
        }
        sqlBuilder.append(" WHERE id = ?");
        
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
            
            int parameterIndex = 1;
            for (Object value : updates.values()) {
                preparedStatement.setObject(parameterIndex++, value);
            }
            preparedStatement.setLong(parameterIndex, inventoryId);
            
            int affectedrows = preparedStatement.executeUpdate();
            if (affectedrows > 0) {
            	result= new Result(true,200,"Update succesful ",inventoryId);
           } else {
        	   result = new Result(false,404,"update failed inventory NOT FOUND ",inventoryId);
           	
           }
            
        } catch (SQLException e) {
        	LOGGER.error("Exception occured "+e.getMessage());
            e.printStackTrace();
            result = new Result(false,404,"Update failed "+getConstraintMessage(e.getMessage()),null);
        }
        return result;
    }


	public Result getInventoryByType(String type) {

		Result result = new Result();
		String query = "SELECT * FROM INVENTORY where type =?";
        List<Inventory> inventories = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url,username,password);
        	PreparedStatement preparedStatement = connection.prepareStatement(query);){
        	preparedStatement.setString(1,type);
             ResultSet resultSet = preparedStatement.executeQuery();
        	
            while (resultSet.next()) {
                Inventory inventory = mapResultSetToInventory(resultSet);
                result.setSuccess(true);
                inventories.add(inventory);
            }
            result.setCode(200);
            if(result.isSuccess()) {
	            result.setDataObject(inventories);
	            
            }else {
            	result.setMessage("Inventory with type not found "+type);
            	
            }

        } catch (SQLException e) {
        	LOGGER.error("Exception occured "+e.getMessage());
            e.printStackTrace();
            result.setMessage("Failed to retrieve "+getConstraintMessage(e.getMessage()));
            result.setCode(404);
        }
        return result;
    
	
	}
	
	private String getConstraintMessage(String constraintname) {
		String errorMessage = "";
		if (constraintname.contains("Check constraint 'inventory_chk_1' is violated")) {
            errorMessage = "Name cannot be empty";
        } else if (constraintname.contains("Check constraint 'inventory_chk_2' is violated")) {
            errorMessage = "Dimensions cannot be empty";
        } else if (constraintname.contains("Check constraint 'inventory_chk_3' is violated")) {
            errorMessage = "Volume must be between 1 and 1000";
        } else if (constraintname.contains("Check constraint 'inventory_chk_4' is violated")) {
            errorMessage = "type cannot be empty";
        } else if (constraintname.contains("Check constraint 'inventory_chk_5' is violated")) {
            errorMessage = "Count must be between 1 and 100";
        }else {
        	return constraintname;
        }
		
		return errorMessage;
	}
	
    private Inventory mapResultSetToInventory(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String dimensions = resultSet.getString("dimensions");
        float volume = resultSet.getFloat("volume");
        String type = resultSet.getString("type");
        int count = resultSet.getInt("count");
        String status = resultSet.getString("status");
        Inventory inv=  new Inventory( name, dimensions, volume, type, count,status);
        inv.setId(id);
        return inv;
    }
}

