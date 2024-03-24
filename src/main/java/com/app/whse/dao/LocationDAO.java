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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.app.whse.data.Location;
import com.app.whse.data.Result;

public class LocationDAO {

	private final String url;
    private final String username;
    private final String password;
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationDAO.class);
    
    public LocationDAO(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    public Result getAllLocations() {
        List<Location> locations = new ArrayList<>();
        Result result = new Result();
        try (Connection connection = DriverManager.getConnection(url,username,password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM LOCATION")) {

            while (resultSet.next()) {
                Location location = matchResultsetToLocation(resultSet);
                result.setSuccess(true);
                locations.add(location);
            }
            
			if(result.isSuccess())
            	result.setDataObject(locations);
			else {
				result.setMessage("NO DATA");
				result.setCode(404);
			}
        } catch (SQLException e) {
        	LOGGER.error("Exception occured"+e.getMessage());
            e.printStackTrace();
            result = new Result(false,404,"Getall Failure "+e.getMessage(),null);
        }
        return result;
    }

    public Result addLocation(Location location) {
		String query = "INSERT INTO LOCATION (LOCATIONNAME, LOCATIONTYPE, DIMENSION, ONHANDQTY, MAXQTY) " +
                       "VALUES (?, ?, ?, ?, ?)";
		Result result = null;
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, location.getLocationName());
            preparedStatement.setString(2, location.getLocationType());
            preparedStatement.setString(3, location.getDimension());
            preparedStatement.setInt(4, 0);
            preparedStatement.setInt(5, location.getMaxQty());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
            	result = new Result(false,200,"Creation failed",location);
            }
            if (affectedRows == 1) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int locationId = generatedKeys.getInt(1);
                        location.setLocationId(locationId);
                        location.setOnhandQty(0);
                        result = new Result(true,200,"Created successfully",location);
                    } else {
                    	result = new Result(false,200,"Creation failed",location);
                    }
                
            }
        } catch(NullPointerException e1) {
        	LOGGER.error("Exception occured"+e1.getMessage());           
            result = new Result(false,500,"Please populate other fileds of Location(locationname,dimension,locationtype,volume,maxqty,onhandqty)"+getConstraintMessage(e1.getMessage()),null);
        } catch (SQLException e) {
        	LOGGER.error("Exception occured "+e.getMessage());
            e.printStackTrace();      
            result = new Result(false,400,"Creation failed "+getConstraintMessage(e.getMessage()),null);
        }
        return result;
    }



	public Result updateLocation(int id, Location location) {

		String query ="UPDATE LOCATION SET LOCATIONNAME =?, LOCATIONTYPE=?, DIMENSION=?, ONHANDQTY=?, MAXQTY =? WHERE LOCATIONID=?";
		Result result = null;
		try(Connection connection = DriverManager.getConnection(url,username,password);
				PreparedStatement preparedStatement = connection.prepareStatement(query);){
			
			preparedStatement.setString(1,location.getLocationName());
			preparedStatement.setString(2,location.getLocationType());
			preparedStatement.setString(3,location.getDimension());
			preparedStatement.setInt(4,location.getOnhandQty());
			preparedStatement.setInt(5,location.getMaxQty());
			preparedStatement.setInt(6,id);
			if(location.getOnhandQty()>location.getMaxQty()) {
				return new Result(false,200,"onhand qty cannot be greater than max qty",location);
			}
			 int affectedRows = preparedStatement.executeUpdate();
	            if (affectedRows > 0) {
	            	location.setLocationId(id);
	            	   result = new Result(true,200,"Updated successfully",location);
	            } else {
	            	result = new Result(false,404,"location NOT FOUND",null);
	            }
	        } catch (SQLException e) {
	        	LOGGER.info("Couldn't update location , please check logs "+e.getMessage());
	        	e.printStackTrace();
	            result = new Result(false,400,"Update failed "+getConstraintMessage(e.getMessage()),null);
	        
	        }
	        
	        return result;
	}

	public Result deleteLocation(int id) {
		
		String query = "DELETE FROM LOCATION WHERE LOCATIONID =?";
		int affectedrows =0;
		Result result = null;
		try(Connection connection = DriverManager.getConnection(url,username,password);
		PreparedStatement statement = connection.prepareStatement(query);){
			
			statement.setInt(1,id);
			affectedrows = statement.executeUpdate();
			if (affectedrows > 0) {
            	
           	 result = new Result(true,200,"deletion successful ",id);
           } else {
           	result = new Result(false,200,"deletion failed location NOT FOUND",id);
           }
       } catch (SQLException e) {
       		LOGGER.info("Couldn't update Location , please check logs "+e.getMessage());
         e.printStackTrace();
           result = new Result(false,400,"Deletion failed "+getConstraintMessage(e.getMessage()),null);
       
       }
       
       return result;
	}
	
	public Result getLocationById(int id) {
		
		String query ="SELECT * FROM LOCATION WHERE LOCATIONID=?";
		Result result = null;
		Location location =null;
		try(Connection connection = DriverManager.getConnection(url,username,password);
				PreparedStatement preparedStatement = connection.prepareStatement(query);){
			
			preparedStatement.setInt(1,id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    location = matchResultsetToLocation(resultSet);
                    result = new Result(true,200,"Success ",location);
                }else {
                	result = new Result(false,404,"LOCATION NOT FOUND ",null);
                }
            }
        }catch (SQLException e) {
        	LOGGER.error("Exception occured "+e.getMessage());
            e.printStackTrace();      
            result = new Result(false,400,"NOT FOUND "+e.getMessage(),null);
        }

        return result;
	}
	
	 public Result partialUpdateLocation(long locationId, Map<String, Object> updates) {
		    StringBuilder sqlBuilder = new StringBuilder("UPDATE LOCATION SET ");
	        boolean first = true;
	        Result result = new Result();
	        int affectedrows = 0;
	        for (String column : updates.keySet()) {
	            if (!first) {
	                sqlBuilder.append(", ");
	            }
	            sqlBuilder.append(column).append(" = ?");
	            first = false;
	        }
	        sqlBuilder.append(" WHERE LOCATIONID = ?");
	        
	        try (Connection connection = DriverManager.getConnection(url,username,password);
	             PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString())) {
	            
	            int parameterIndex = 1;
	            for (Object value : updates.values()) {
	                preparedStatement.setObject(parameterIndex++, value);
	            }
	            preparedStatement.setLong(parameterIndex, locationId);
	            
	             affectedrows = preparedStatement.executeUpdate();
	             if (affectedrows > 0) {
	             	result= new Result(true,200,"Update succesful ",locationId);
	            } else {
	         	   result = new Result(false,404,"update failed lcaotion NOT FOUND ",locationId);
	            	
	            }
	             
	         } catch (SQLException e) {
	         	LOGGER.error("Exception occured "+e.getMessage());
	             e.printStackTrace();
	             result = new Result(false,404,"Update failed "+getConstraintMessage(e.getMessage()),null);
	         }
	         return result;
	    }

	public Result getLocationByType(String type) {
		
		String query = "SELECT * FROM LOCATION where LOCATIONTYPE =?";
        List<Location> locations = new ArrayList<>();
        Result result = new Result();
        try (Connection connection = DriverManager.getConnection(url,username,password);
        	PreparedStatement preparedStatement = connection.prepareStatement(query);){
        	preparedStatement.setString(1,type);
             ResultSet resultSet = preparedStatement.executeQuery();
        	
            while (resultSet.next()) {
            	result.setSuccess(true);
                Location location = matchResultsetToLocation(resultSet);
                locations.add(location);
            }
            result.setCode(200);
            if(result.isSuccess()) {
	            result.setDataObject(locations);
	            
            }else {
            	result.setMessage("Location with type not found "+type);
            	
            }

        } catch (SQLException e) {
        	LOGGER.error("Exception occured "+e.getMessage());
            e.printStackTrace();
            result.setMessage("Failed to retrieve "+getConstraintMessage(e.getMessage()));
            result.setCode(404);
        }
        return result;
    
	}
	
	   public Result findLocationIdByName(String locationName) {
	        Location location = null;
	        Result result = new Result();
	        try (Connection connection = DriverManager.getConnection(url, username, password);
	             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM LOCATION WHERE LOCATIONNAME = ?");
	        ) {
	            preparedStatement.setString(1, locationName);
	            ResultSet resultSet = preparedStatement.executeQuery();
	                if (resultSet.next()) {
	                	result.setSuccess(true);
	                    location = matchResultsetToLocation(resultSet);
	                }
	                result.setCode(200);
	                if(result.isSuccess()) {
	    	            result.setDataObject(location);
	    	            
	                }else {
	                	result.setMessage("Location not found ");
	                	
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
		if (constraintname.contains("Check constraint 'location_chk_1' is violated")) {
            errorMessage = "Name cannot be empty";
        } else if (constraintname.contains("Check constraint 'location_chk_2' is violated")) {
            errorMessage = "locationtype cannot be empty";
        } else if (constraintname.contains("Check constraint 'location_chk_3' is violated")) {
            errorMessage = "dimension cannot be empty";
        } else if (constraintname.contains("Check constraint 'location_chk_4' is violated")) {
            errorMessage = "onhandqty must be between 0 and 100";
        } else if (constraintname.contains("Check constraint 'location_chk_5' is violated")) {
            errorMessage = "maxqty must be between 1 and 100";
        }else {
        	return constraintname;
        }
		
		return errorMessage;
	}
	
    private Location matchResultsetToLocation(ResultSet resultSet) throws SQLException{
    	Location location = new Location();
    	location.setLocationId(resultSet.getInt("locationid"));
        location.setLocationName(resultSet.getString("locationname"));
        location.setLocationType(resultSet.getString("locationtype"));
        location.setDimension(resultSet.getString("dimension"));
        location.setOnhandQty(resultSet.getInt("onhandqty"));
        location.setMaxQty(resultSet.getInt("maxqty"));
		return location;
	}

	
}
