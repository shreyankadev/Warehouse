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