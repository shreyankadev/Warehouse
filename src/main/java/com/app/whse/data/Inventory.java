package com.app.whse.data;

public class Inventory {
    private int id;
    private String name;
    private String dimensions;
    private Float volume;
    private String type;
    private enum status{RECEIVED, PUTAWAY};
    private int count;
    private String status;

    // Constructors
    public Inventory() {
        // Default constructor
    }

    public Inventory(String name, String dimensions, Float volume, String type, int count,String status) {
        this.name = name;
        this.dimensions = dimensions;
        this.volume = volume;
        this.type = type;
        this.count = count;
        this.status =status;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    
    public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Inventory [id=" + id + ", name=" + name + ", dimensions=" + dimensions + ", volume=" + volume
				+ ", type=" + type + ", count=" + count + ", status=" + status + "]";
	}
}

