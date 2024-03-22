package com.app.whse.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.app.whse.data.Inventory;


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

    public List<Inventory> findAll() {
    	LOGGER.info("findAll start");
        List<Inventory> inventories = new ArrayList<>();
        String sql = "SELECT * FROM Inventory";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Inventory inventory = mapResultSetToInventory(resultSet);
                inventories.add(inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LOGGER.info("findAll end");
        return inventories;
    }

    public Inventory findById(int id) {
        Inventory inventory = null;
        String sql = "SELECT * FROM Inventory WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    inventory = mapResultSetToInventory(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventory;
    }

    public Inventory create(Inventory inventory) {
        String sql = "INSERT INTO Inventory (name, dimensions, volume, type,count,status) VALUES (?, ?, ?, ?,?,?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        	int count = inventory.getCount();
        	if(count<=0) {
        		count = 1;
        	}
            statement.setString(1, inventory.getName());
            statement.setString(2, inventory.getDimensions());
            statement.setFloat(3, inventory.getVolume());
            statement.setString(4, inventory.getType());
            statement.setInt(5, count);
            statement.setString(6, "RECEIVED");

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Couldn't create Inventory, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    inventory.setId(generatedKeys.getInt(1));
                    inventory.setStatus("RECEIVED");
                } else {
                    throw new SQLException("Couldn't create Inventory, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventory;
    }
    
 

    public Inventory update(int id ,Inventory inventory) {
        String sql = "UPDATE Inventory SET name = ?, dimensions = ?, volume = ?, type = ? ,count =?, status =? WHERE id = ?";
        Response response = null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, inventory.getName());
            statement.setString(2, inventory.getDimensions());
            statement.setFloat(3, inventory.getVolume());
            statement.setString(4, inventory.getType());
            statement.setInt(5,inventory.getCount());
            statement.setString(6, inventory.getStatus());
            statement.setInt(7, id);
            

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
            	LOGGER.info("Couldn't update Inventory, no rows affected.");                
            }else {
            	inventory.setId(id);
            	return inventory;
            }
        } catch (SQLException e) {
        	LOGGER.info("Couldn't update Inventory , please check logs "+e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public Response delete(int id) {
        String sql = "DELETE FROM Inventory WHERE id = ?";
        Response response= null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            int affectedrows = statement.executeUpdate();
            if(affectedrows ==0) {
            	response= Response.ok("Inventory with id "+id+" is not found").build();
            }else {
            	response= Response.ok("Delete of item with id "+ id+" is Successful!").build();
            }
            
            return response;
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.NOT_FOUND).entity("Couldn't delete the Inventory , please check logs "+e.getMessage()).build();
        }
    }

    public int partialUpdateInventory(long inventoryId, Map<String, Object> updates) {
    	StringBuilder sqlBuilder = new StringBuilder("UPDATE inventory SET ");
        boolean first = true;
        int affectedrows = 0;
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
            
            affectedrows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return affectedrows;
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

	public List<Inventory> getInventoryByType(String type) {

		
		String query = "SELECT * FROM INVENTORY where type =?";
        List<Inventory> inventories = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url,username,password);
        	PreparedStatement preparedStatement = connection.prepareStatement(query);){
        	preparedStatement.setString(1,type);
             ResultSet resultSet = preparedStatement.executeQuery();
        	
            while (resultSet.next()) {
                Inventory inventory = mapResultSetToInventory(resultSet);
                
                inventories.add(inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventories;
    
	
	}
}

