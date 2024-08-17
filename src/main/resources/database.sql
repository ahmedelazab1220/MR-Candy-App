-- Drop existing database if exists and create a new one
DROP DATABASE IF EXISTS mr_candy_app;
CREATE DATABASE mr_candy_app;
USE mr_candy_app;

-- Drop all existing tables if they exist
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS orders_items;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS companies;
DROP TABLE IF EXISTS otps;
DROP TABLE IF EXISTS refresh_token;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS addresses;

-- Create addresses table
CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    zip_code VARCHAR(20)
)ENGINE=InnoDB;

-- Create roles table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
)ENGINE=InnoDB;

INSERT INTO roles (role) VALUES ('USER'), ('ADMIN');

-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100),
    email VARCHAR(35) NOT NULL UNIQUE,
    phone_number VARCHAR(15) NOT NULL,
    password VARCHAR(400) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    image_url VARCHAR(1000) NOT NULL,
    address_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
)ENGINE=InnoDB;

-- Create refresh_token table
CREATE TABLE refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(1000) NOT NULL,
    expire_date DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
)ENGINE=InnoDB;

-- Create companies table
CREATE TABLE companies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    image_url VARCHAR(1000) NOT NULL
)ENGINE=InnoDB;

-- Create categories table
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    image_url VARCHAR(1000) NOT NULL
)ENGINE=InnoDB;

-- Create products table
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    size VARCHAR(255),
    type VARCHAR(255),
    discount VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sales_count BIGINT NOT NULL,
    image_url VARCHAR(1000) NOT NULL,
    category_id BIGINT,
    company_id BIGINT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
)ENGINE=InnoDB;

-- Create otps table
CREATE TABLE otps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    otp VARCHAR(255) NOT NULL,
    expiration_time DATETIME NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
)ENGINE=InnoDB;

-- Create carts table
CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
)ENGINE=InnoDB;

-- Create cart_items table
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT UNIQUE NOT NULL,
    product_id BIGINT,
    quantity INT NOT NULL,
    price DECIMAL(15, 2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL
)ENGINE=InnoDB;

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_price DECIMAL(30, 2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
)ENGINE=InnoDB;

-- Create order_items table
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT,
    quantity INT NOT NULL,
    price DECIMAL(15, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL
)ENGINE=InnoDB;

-- Create indexes
CREATE INDEX idx_product_id ON products(id);
CREATE INDEX idx_product_sales_count ON products(sales_count);
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_id ON users(id);
-- Add a composite index on otp and user_id columns in the otps table
CREATE INDEX idx_otps_otp ON otps (otp);
CREATE INDEX idx_address_id ON addresses(id);
CREATE INDEX idx_order_item_id ON order_items(id);
CREATE INDEX idx_cart_item_id ON cart_items(id);
CREATE INDEX idx_cart_id ON carts(id);
CREATE INDEX idx_order_id ON orders(id);
CREATE INDEX idx_order_item_cart_id ON order_items(order_id);
CREATE INDEX idx_cart_item_cart_id ON cart_items(cart_id);
CREATE INDEX idx_order_item_product_id ON order_items(product_id);
CREATE INDEX idx_products_company_id ON products(company_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_company_name ON companies(name);
CREATE INDEX idx_category_name ON categories(name);
CREATE INDEX idx_company_id ON companies(id);
CREATE INDEX idx_category_id ON categories(id);
CREATE INDEX idx_refresh_token_user_id ON refresh_token(user_id);
CREATE INDEX idx_products_sales_count ON products(sales_count);
CREATE INDEX idx_user_address_id ON users(address_id);
CREATE INDEX idx_user_role_id ON users(role_id);
CREATE INDEX idx_role_id ON roles(id);

-- Drop procedure if exists
DROP PROCEDURE IF EXISTS delete_expired_entries;

-- Create procedure to delete expired entries
DELIMITER //

CREATE PROCEDURE delete_expired_entries()
BEGIN
    -- Declare a variable to hold the cutoff time
    DECLARE cutoff_time DATETIME;

    -- Set the cutoff time to the current time minus 5 days
    SET cutoff_time = NOW() - INTERVAL 5 DAY;

    -- Delete records from the otps table where expiration_time is earlier than the cutoff time
    DELETE FROM otps WHERE expirationTime < cutoff_time;

    -- Delete records from the refresh_token table where expire_date is earlier than the cutoff time
    DELETE FROM refresh_token WHERE expire_date < cutoff_time;
END //

DELIMITER ;

-- Drop event if exists
DROP EVENT IF EXISTS delete_expired_entries_event;

-- Create event to call the delete_expired_entries procedure every 2 days
CREATE EVENT delete_expired_entries_event
    ON SCHEDULE EVERY 2 DAY
    DO CALL delete_expired_entries();
