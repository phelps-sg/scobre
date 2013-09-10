USE lse_tickdata;

CREATE TABLE order_detail_raw (
	order_code VARCHAR(10) NOT NULL,
	market_segment_code VARCHAR(4) NOT NULL,
	market_sector_code VARCHAR(4) NOT NULL,
	ti_code VARCHAR(12) NOT NULL,
	country_of_register VARCHAR(2) NOT NULL,
	currency_code VARCHAR(3) NOT NULL,
	participant_code VARCHAR(11) NULL,
	buy_sell_ind CHAR(1) NOT NULL,
	market_mechanism_group VARCHAR(1) NOT NULL,
	market_mechanism_type VARCHAR(2) NOT NULL,
	price DECIMAL(18, 8) NOT NULL,
	aggregate_size DECIMAL(12, 0) NOT NULL,
	single_fill_ind VARCHAR(1) NOT NULL,
	broadcast_update_action VARCHAR(1) NOT NULL,
	date VARCHAR(8) NOT NULL,
	time VARCHAR (15) NOT NULL,
	message_sequence_number int NOT NULL
);

CREATE TABLE orders (
	order_code VARCHAR(10) PRIMARY KEY NOT NULL,
	market_sector_code VARCHAR(4) NOT NULL,
	participant_code VARCHAR(11) NULL,
	buy_sell_ind CHAR(1) NOT NULL,
	market_mechanism_group VARCHAR(1) NOT NULL,
	market_mechanism_type VARCHAR(2) NOT NULL,
	price DECIMAL(18, 8) NOT NULL,
	aggregate_size DECIMAL(12, 0) NOT NULL,
	single_fill_ind VARCHAR(1) NOT NULL,
	broadcast_update_action VARCHAR(1) NOT NULL
);

CREATE INDEX id_orders_price ON orders (price);
