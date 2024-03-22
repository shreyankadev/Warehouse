package com.app.whse.data;

public class Location {
    private Long locationId;
    private String locationName;
    private String locationType;
    private String dimension;
    private int onhandQty;
    private int maxQty;

    // Constructors
    public Location() {
    }

    public Location(String locationName, String locationType, String dimension, int onhandQty, int maxQty) {
        this.locationName = locationName;
        this.locationType = locationType;
        this.dimension = dimension;
        this.onhandQty = onhandQty;
        this.maxQty = maxQty;
    }

    // Getters and Setters
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

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public int getOnhandQty() {
        return onhandQty;
    }

    public void setOnhandQty(int onhandQty) {
        this.onhandQty = onhandQty;
    }

    public int getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(int maxQty) {
        this.maxQty = maxQty;
    }

    // toString method (optional)
    @Override
    public String toString() {
        return "Location{" +
                "locationId=" + locationId +
                ", locationName='" + locationName + '\'' +
                ", locationType='" + locationType + '\'' +
                ", dimension='" + dimension + '\'' +
                ", onhandQty=" + onhandQty +
                ", maxQty=" + maxQty +
                '}';
    }
}

