package com.app.whse.config;

import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class WMConfiguration extends Configuration{
	
	 @Valid
     @NotNull
	 private DataSourceFactory database = new DataSourceFactory();

	    public DataSourceFactory getDataSourceFactory() {
	        return database;
	    }
	    
	    public void setDataSourceFactory(DataSourceFactory database) {
	        this.database = database;
	    }
}
