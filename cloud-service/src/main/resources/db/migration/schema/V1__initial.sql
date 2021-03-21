CREATE TABLE categories (
	id bigint NOT NULL AUTO_INCREMENT, 
	code varchar(255) NOT NULL, 
	name varchar(255), 
	PRIMARY KEY (id)
);	
ALTER TABLE categories ADD CONSTRAINT UK_categories_code UNIQUE (code);

CREATE TABLE products (
	id bigint NOT NULL AUTO_INCREMENT, 
	code varchar(255) NOT NULL, 
	name varchar(255), 
	price decimal(19,2) NOT NULL, 
	PRIMARY KEY (id)
);
ALTER TABLE products ADD CONSTRAINT UK_products_code UNIQUE (code);

	
CREATE TABLE categories2products (
	category_code varchar(255) not null, 
	product_code varchar(255) not null, 
	PRIMARY KEY (category_code, product_code),
	FOREIGN KEY (product_code) REFERENCES products(code),
  	FOREIGN KEY (category_code) REFERENCES categories(code))

