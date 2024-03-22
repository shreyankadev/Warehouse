package com.app.whse.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.app.whse.auth.BasicAuthenticationFilter;
import com.app.whse.cache.CacheService;
import com.app.whse.config.WMConfiguration;
import com.app.whse.dao.DatabaseHealthCheck;
import com.app.whse.dao.InventoryDAO;
import com.app.whse.dao.InventoryInLocationDAO;
import com.app.whse.dao.LocationDAO;
import com.app.whse.resource.HealthCheckResource;
import com.app.whse.resource.InventoryInLocationResource;
import com.app.whse.resource.InventoryResource;
import com.app.whse.resource.LocationResource;
import com.app.whse.service.InventoryService;
import com.app.whse.service.LocationService;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

public class App extends Application<WMConfiguration>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	public static void main(String[] args) throws Exception{
		LOGGER.info("Applcaition Started");
		new App().run(args);
		}

		@Override
		public void run(WMConfiguration configuration, Environment environment) throws Exception {
			 
			CacheService cacheService = new CacheService();
			
			//properties
			Properties properties = loadProperties("src/main/resources/application.properties");
			String url =properties.getProperty("database.url");
			String username =properties.getProperty("database.user");
			String password = properties.getProperty("database.password");
			
			//supporting xml format for special case
	        //environment.jersey().register(new MoxyXmlFeature());
	        
			//healthcheck
			DatabaseHealthCheck healthCheck =  new DatabaseHealthCheck(url,username,password);
			//DAOs
			InventoryInLocationDAO inventoryLocDao = new InventoryInLocationDAO(url,username,password);
			InventoryDAO InventoryDao =  new InventoryDAO(url,username,password);
			LocationDAO locationDao = new LocationDAO(url,username,password);
			
			//Services
			InventoryService service = new InventoryService(InventoryDao,cacheService);
			LocationService lservice = new LocationService(locationDao,cacheService);
			
			//Registeries 
			environment.jersey().register(new BasicAuthenticationFilter());
			environment.jersey().register(new HealthCheckResource(healthCheck));
			environment.jersey().register(new InventoryResource(service));
			environment.jersey().register(new InventoryInLocationResource(inventoryLocDao,InventoryDao,locationDao,cacheService));
			environment.jersey().register(new LocationResource(lservice));
			
		}
		
		@Override
	    public void initialize(Bootstrap<WMConfiguration> bootstrap) {
	        // Initialize your application
	    }
		
		private static Properties loadProperties(String filePath) throws IOException {
			Properties properties = new Properties();
	        try (FileInputStream fis = new FileInputStream(filePath)) {
	            properties.load(fis);
	        }
	        return properties;
	    }
}
