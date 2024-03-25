-- Create the warehouse schema
CREATE SCHEMA IF NOT EXISTS warehouse;

-- Use the warehouse schema
USE warehouse;

-- Create tables and insert initial data
-- (Add your table creation and data insertion statements here)


CREATE TABLE Inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL CHECK (name != ''),
    dimensions VARCHAR(255) NOT NULL CHECK (dimensions != ''),
    volume DECIMAL(10, 2) NOT NULL CHECK (volume BETWEEN 1 AND 1000),
    type VARCHAR(50) NOT NULL,
    status ENUM('RECEIVED', 'PUTAWAY') DEFAULT 'RECEIVED',
    count INT NOT NULL CHECK (count BETWEEN 1 AND 100)
);

CREATE TABLE Location (
    locationid INT AUTO_INCREMENT PRIMARY KEY,
    locationname VARCHAR(255) NOT NULL CHECK (locationname != ''),
    locationtype VARCHAR(255) NOT NULL CHECK (locationtype != ''),
    dimension VARCHAR(255) NOT NULL CHECK (dimension != ''),
    onhandqty INT NOT NULL CHECK (onhandqty BETWEEN 0 AND 100),
    maxqty INT NOT NULL CHECK (maxqty BETWEEN 1 AND 100)
)AUTO_INCREMENT = 101;

CREATE TABLE inventorylocation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inventoryid INT,
    inventoryname VARCHAR(255),
    locationid INT,
    locationname VARCHAR(255),
    count INT,
    FOREIGN KEY (inventoryid) REFERENCES Inventory(id),
    FOREIGN KEY (locationid) REFERENCES Location(locationid)
);

-- Insert data into the "Inventory" table
INSERT INTO Inventory (id, name, dimensions, volume, type, status, count)
VALUES
    (1, 'Item 1', '10x20x5', 100, 'Type A', 'RECEIVED', 1),
    (2, 'Item 2', '15x15x15', 337, 'Type B', 'RECEIVED', 1),
    (3, 'Item 3', '8x8x8', 512, 'Type C', 'RECEIVED', 1);

-- Insert data into the "Location" table
INSERT INTO Location (location_id, location_name, location_type, dimension, onhand_qty, max_qty)
VALUES
    (101, 'Location A', 'Warehouse', '10x20x30', 50, 100),
    (102, 'Location B', 'Storage', '15x15x15', 100, 200),
    (103, 'Location C', 'Retail Store', '5x10x5', 20, 50);
