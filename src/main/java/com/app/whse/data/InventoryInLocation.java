package com.app.whse.data;

public class InventoryInLocation {
    private int id;
    private int inventoryId;
    private String inventoryName;
    private int locationId;
    private String locationName;
    private int count;
    
    public InventoryInLocation() {
    	
    }
    
	public InventoryInLocation(int inventoryId, String inventoryName, int locationId, String locationName,
			int count) {
		super();
		
		this.inventoryId = inventoryId;
		this.inventoryName = inventoryName;
		this.locationId = locationId;
		this.locationName = locationName;
		this.count = count;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getInventoryId() {
		return inventoryId;
	}
	public void setInventoryId(int inventoryId) {
		this.inventoryId = inventoryId;
	}
	public String getInventoryName() {
		return inventoryName;
	}
	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "WMInventory [id=" + id + ", inventoryId=" + inventoryId + ", inventoryName=" + inventoryName
				+ ", locationId=" + locationId + ", locationName=" + locationName + ", count=" + count + "]";
	}

    
}

