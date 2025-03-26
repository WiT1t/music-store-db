#!/bin/bash

DB_NAME="MusicStore"
DB_USER="root"
DB_PASS="root"


SQL_COMMANDS="
CREATE DATABASE IF NOT EXISTS $DB_NAME;
USE $DB_NAME;
CREATE TABLE IF NOT EXISTS Artist (
  artist_id int AUTO_INCREMENT PRIMARY KEY,
  name varchar(30) NOT NULL DEFAULT 'unknown',
  surname varchar(30) NOT NULL DEFAULT 'unknown',
  nickname varchar(30) DEFAULT null,
  country varchar(30) NOT NULL DEFAULT 'unknown'
);
CREATE TABLE IF NOT EXISTS Album (
  album_id int AUTO_INCREMENT PRIMARY KEY,
  artist int NOT NULL,
  name varchar(30) NOT NULL,
  release_date DATE NOT NULL,
  FOREIGN KEY(artist) REFERENCES Artist(artist_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS Product (
  product_id int AUTO_INCREMENT PRIMARY KEY,
  album int NOT NULL,
  type varchar(11) NOT NULL,
  price decimal(10,2) NOT NULL,
  FOREIGN KEY(album) REFERENCES Album(album_id) ON DELETE CASCADE,
  CHECK(price > 0 AND (type = 'CD' OR type = 'Vinyl'))
);
CREATE TABLE IF NOT EXISTS Store (
  product int NOT NULL PRIMARY KEY,
  quantity int NOT NULL,
  FOREIGN KEY(product) REFERENCES Product(product_id) ON DELETE CASCADE,
  CHECK(quantity >= 0)
);
CREATE TABLE IF NOT EXISTS Staff (
  id int AUTO_INCREMENT PRIMARY KEY,
  name varchar(30) NOT NULL,
  surname varchar(30) NOT NULL,
  position varchar(30) NOT NULL,
  phone_number varchar(9) NOT NULL,
  CHECK (position = 'manager' OR position = 'employee'),
  CHECK (phone_number REGEXP '^[0-9]{9}$')
);
CREATE TABLE IF NOT EXISTS Sales (
  date datetime NOT NULL,
  employee int NOT NULL,
  product int NOT NULL,
  quantity int NOT NULL,
  FOREIGN KEY(product) REFERENCES Product(product_id) ON DELETE CASCADE,
  FOREIGN KEY(employee) REFERENCES Staff(id) ON DELETE CASCADE,
  CHECK (quantity > 0)
);
CREATE TABLE IF NOT EXISTS Shipments (
  date datetime NOT NULL,
  employee int NOT NULL,
  product int NOT NULL,
  quantity int NOT NULL,
  FOREIGN KEY(product) REFERENCES Product(product_id) ON DELETE CASCADE,
  FOREIGN KEY(employee) REFERENCES Staff(id) ON DELETE CASCADE,
  CHECK (quantity > 0)
);

-- TRIGGERS
DELIMITER $$
CREATE OR REPLACE TRIGGER check_quantity 
BEFORE INSERT ON Sales 
FOR EACH ROW 
BEGIN 
  DECLARE store_quantity INT; 
  SELECT quantity INTO store_quantity FROM Store WHERE product = NEW.product; 
  IF NEW.quantity > store_quantity THEN 
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Quantity in Sales cannot be greater than quantity in Store'; 
  END IF; 
END$$
DELIMITER ;

DELIMITER $$ 
CREATE OR REPLACE TRIGGER after_sale_update 
AFTER INSERT ON Sales 
FOR EACH ROW 
BEGIN 
  DECLARE store_quantity INT; 
  SELECT quantity INTO store_quantity FROM Store WHERE product = NEW.product; 
  IF store_quantity IS NOT NULL THEN 
    UPDATE Store SET quantity = store_quantity - NEW.quantity WHERE product = NEW.product; 
  ELSE 
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Product does not exist in Store'; 
  END IF; 
END$$ 
DELIMITER ;

DELIMITER $$ 
CREATE OR REPLACE TRIGGER after_shipment_update 
AFTER INSERT ON Shipments 
FOR EACH ROW 
BEGIN 
  DECLARE store_quantity INT; 
  SELECT quantity INTO store_quantity FROM Store WHERE product = NEW.product; 
  IF store_quantity IS NOT NULL THEN 
    UPDATE Store SET quantity = store_quantity + NEW.quantity WHERE product = NEW.product; 
  ELSE 
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Product does not exist in Store'; 
  END IF; 
END$$ 
DELIMITER ;

DELIMITER $$ 
CREATE OR REPLACE TRIGGER new_product 
AFTER INSERT ON Product 
FOR EACH ROW 
BEGIN 
 INSERT INTO STORE(product, quantity) 
 VALUES(NEW.product_id, 0); 
END$$ 
DELIMITER ;

-- PROCEDURES
DELIMITER $$ 
CREATE OR REPLACE PROCEDURE AddArtist(
    IN artist_name VARCHAR(30),
    IN artist_surname VARCHAR(30),
    IN artist_nickname VARCHAR(30),
    IN artist_country VARCHAR(30)
)
BEGIN
    INSERT INTO Artist (name, surname, nickname, country) VALUES (artist_name, artist_surname, artist_nickname, artist_country);
END$$

CREATE OR REPLACE PROCEDURE AddAlbum(
    IN album_artist INT,
    IN album_name VARCHAR(30),
    IN album_release DATE
)
BEGIN
    INSERT INTO Album (artist, name, release_date) VALUES (album_artist, album_name, album_release);
END$$

CREATE OR REPLACE PROCEDURE AddProduct(
    IN album_id INT,
    IN prod_type VARCHAR(11),
    IN prod_price DECIMAL(10, 2)
)
BEGIN
    INSERT INTO Product (album, type, price) VALUES (album_id, prod_type, prod_price);
END$$

CREATE OR REPLACE PROCEDURE UpdateProductPrice(
    IN prod_id INT,
    IN new_price DECIMAL(10, 2)
)
BEGIN
    UPDATE Product SET price = new_price WHERE product_id = prod_id;
END$$

CREATE OR REPLACE PROCEDURE AddShipment(
    IN shipment_date DATETIME,
    IN shipment_employee INT,
    IN shipment_product INT,
    IN shipment_quantity INT
)
BEGIN
    INSERT INTO Shipments (date, employee, product, quantity) VALUES (shipment_date, shipment_employee, shipment_product, shipment_quantity);
END$$

CREATE OR REPLACE PROCEDURE AddSale(
    IN sale_date DATETIME,
    IN sale_employee INT,
    IN sale_product INT,
    IN sale_quantity INT
)
BEGIN
    INSERT INTO Sales (date, employee, product, quantity) VALUES (sale_date, sale_employee, sale_product, sale_quantity);
END$$

CREATE OR REPLACE PROCEDURE AddStaff(
    IN staff_name VARCHAR(30),
    IN staff_surname VARCHAR(30),
    IN staff_position VARCHAR(30),
    IN staff_phone VARCHAR(9)
)
BEGIN
    INSERT INTO Staff (name, surname, position, phone_number) VALUES (staff_name, staff_surname, staff_position, staff_phone);
END$$

CREATE OR REPLACE PROCEDURE DropStaff (
    IN employee_id INT
)
BEGIN
    DELETE FROM Staff WHERE id = employee_id;
END$$

CREATE OR REPLACE PROCEDURE LookupProductByArtistName (
    IN artist_name VARCHAR(30),
    IN artist_surname VARCHAR(30),
    IN artist_nickname VARCHAR(30)
)
BEGIN
    SELECT p.product_id, al.name, p.type, p.price, s.quantity  
    FROM Artist a INNER JOIN Album al ON a.artist_id = al.artist 
    INNER JOIN Product p ON al.album_id = p.album 
    INNER JOIN Store s ON p.product_id = s.product 
    WHERE a.nickname = artist_nickname OR (a.name = artist_name AND a.surname = artist_surname );
END$$

CREATE OR REPLACE PROCEDURE LookupProductByAlbumName (
    IN album_name VARCHAR(30)
)
BEGIN
    SELECT p.product_id, a.name, a.surname, a.nickname, al.name, p.type, p.price, s.quantity  
    FROM Artist a INNER JOIN Album al ON a.artist_id = al.artist 
    INNER JOIN Product p ON al.album_id = p.album 
    INNER JOIN Store s ON p.product_id = s.product 
    WHERE al.name = album_name;
END$$

CREATE OR REPLACE PROCEDURE LookupProductByType (
    IN product_type VARCHAR(30)
)
BEGIN
    SELECT p.product_id, a.name, a.surname, a.nickname, al.name, p.price, s.quantity  
    FROM Artist a INNER JOIN Album al ON a.artist_id = al.artist 
    INNER JOIN Product p ON al.album_id = p.album 
    INNER JOIN Store s ON p.product_id = s.product 
    WHERE p.type = product_type;
END$$

CREATE OR REPLACE PROCEDURE ViewAvailable()
BEGIN
    SELECT p.product_id, a.name, a.surname, a.nickname, al.name, p.price, s.quantity  
    FROM Artist a INNER JOIN Album al ON a.artist_id = al.artist 
    INNER JOIN Product p ON al.album_id = p.album 
    INNER JOIN Store s ON p.product_id = s.product 
    WHERE s.quantity > 0;
END$$

CREATE OR REPLACE PROCEDURE ViewBestsellers()
BEGIN
    SELECT p.product_id, a.name, a.surname, a.nickname, al.name, p.type, SUM(s.quantity) 
    FROM Artist a INNER JOIN Album al ON a.artist_id = al.artist 
    INNER JOIN Product p ON al.album_id = p.album 
    INNER JOIN Sales s ON p.product_id = s.product 
    GROUP BY p.product_id, a.name, a.surname, a.nickname, al.name, p.type 
    ORDER BY SUM(s.quantity) 
    LIMIT 3;
END$$
DELIMITER ;

-- ADD USERS AND GRANTS
CREATE USER 'manager'@'localhost' IDENTIFIED BY 'mng123'; 
GRANT INSERT ON MusicStore.Store TO 'manager'@'localhost'; 
GRANT INSERT,  UPDATE ON MusicStore.Product TO 'manager'@'localhost'; 
GRANT INSERT ON MusicStore.Artist TO 'manager'@'localhost'; 
GRANT INSERT ON MusicStore.Album TO 'manager'@'localhost'; 
GRANT INSERT, DELETE, UPDATE ON MusicStore.Staff TO 'manager'@'localhost'; 
GRANT SELECT, EXECUTE ON PROCEDURE MusicStore.AddArtist TO 'manager'@'localhost'; 
GRANT SELECT, EXECUTE ON PROCEDURE MusicStore.AddAlbum TO 'manager'@'localhost'; 
GRANT SELECT, EXECUTE ON PROCEDURE MusicStore.AddProduct TO 'manager'@'localhost'; 
GRANT SELECT, EXECUTE ON PROCEDURE MusicStore.UpdateProductPrice TO 'manager'@'localhost'; 
GRANT EXECUTE ON PROCEDURE MusicStore.LookupProductByArtistName TO 'manager'@'localhost'; 
GRANT EXECUTE ON PROCEDURE MusicStore.LookupProductByAlbumName TO 'manager'@'localhost'; 
GRANT EXECUTE ON PROCEDURE MusicStore.LookupProductByType TO 'manager'@'localhost'; 
GRANT EXECUTE ON PROCEDURE MusicStore.ViewAvailable TO 'manager'@'localhost'; 
GRANT EXECUTE ON PROCEDURE MusicStore.ViewBestsellers TO 'manager'@'localhost'; 

CREATE USER 'employee'@'localhost' IDENTIFIED BY 'emp123'; 
GRANT INSERT ON MusicStore.Shipments TO 'employee'@'localhost'; 
GRANT INSERT ON MusicStore.Sales TO 'employee'@'localhost'; 
GRANT SELECT ON MusicStore.* TO 'employee'@'localhost'; 
GRANT EXECUTE ON PROCEDURE MusicStore.AddShipment TO 'employee'@'localhost'; 
GRANT EXECUTE ON PROCEDURE MusicStore.AddSale TO 'employee'@'localhost'; 
GRANT EXECUTE ON PROCEDURE MusicStore.LookupProductByArtistName TO 'employee'@'localhost'; 
GRANT EXECUTE ON PROCEDURE MusicStore.LookupProductByAlbumName TO 'employee'@'localhost'; 
GRANT EXECUTE ON PROCEDURE MusicStore.LookupProductByType TO 'employee'@'localhost'; 
GRANT EXECUTE ON PROCEDURE MusicStore.ViewAvailable TO 'employee'@'localhost'; 
GRANT EXECUTE ON PROCEDURE MusicStore.ViewBestsellers TO 'employee'@'localhost'; 

FLUSH PRIVILEGES;
"


echo "Initializing database..."
echo "$SQL_COMMANDS" | mariadb -u$DB_USER -p$DB_PASS && echo "Database initialized successfully." || echo "Failed to initialize database."

exit 0
