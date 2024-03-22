package com.app.whse.data;

public class InventoryInLocation {
    private Long id;
    private Long inventoryId;
    private String inventoryName;
    private Long locationId;
    private String locationName;
    private int count;
    
    public InventoryInLocation() {
    	
    }
    
	public InventoryInLocation(Long inventoryId, String inventoryName, Long locationId, String locationName,
			int count) {
		super();
		
		this.inventoryId = inventoryId;
		this.inventoryName = inventoryName;
		this.locationId = locationId;
		this.locationName = locationName;
		this.count = count;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getInventoryId() {
		return inventoryId;
	}
	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
	}
	public String getInventoryName() {
		return inventoryName;
	}
	public void setInventoryName(String inventoryName) {
		this.inventoryName = inventoryName;
	}
	public Long getLocationId() {
		return locationId;
	}
	public void setLocationId(Long locationId) {
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

