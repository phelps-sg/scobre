USE lse_tickdata;

CREATE TABLE order_history_raw (
	order_code VARCHAR(10) NOT NULL,
	order_action_type CHAR(1) NOT NULL,
	matching_order_code VARCHAR(50) NULL,
	trade_size DECIMAL (8, 0) NULL,
	trade_code VARCHAR(50) NULL,
	ti_code VARCHAR(12) NOT NULL,
	country_of_register VARCHAR(2) NOT NULL,
	currency_code VARCHAR(3) NOT NULL,
	market_segment_code VARCHAR(4) NOT NULL,
	aggregate_size DECIMAL(12, 0) NULL,
	buy_sell_ind CHAR(1) NOT NULL,
	market_mechanism_type VARCHAR(2) NOT NULL,
	message_sequence_number INT NOT NULL,
	date VARCHAR(8) NOT NULL,
	time VARCHAR(15) NOT NULL
); 

CREATE TABLE order_history (
	event_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	order_code VARCHAR(10) NOT NULL,
	FOREIGN KEY fk_order_code(order_code) REFERENCES orders(order_code),
	order_action_type CHAR(1) NOT NULL,
	matching_order_code VARCHAR(50) NULL,
	trade_size DECIMAL (8, 0) NULL,
	trade_code VARCHAR(50) NULL,
	ti_code VARCHAR(12) NOT NULL,
	country_of_register VARCHAR(2) NOT NULL,
	currency_code VARCHAR(3) NOT NULL,
	market_segment_code VARCHAR(4) NOT NULL,
	aggregate_size DECIMAL(12, 0) NULL,
	buy_sell_ind CHAR(1) NOT NULL,
	market_mechanism_type VARCHAR(2) NOT NULL,
	message_sequence_number INT NOT NULL,
	time_stamp BIGINT UNSIGNED NOT NULL
); 

CREATE INDEX id_order_history_time ON order_history(timestamp, message_sequence_number);
CREATE INDEX id_order_history_order_code ON order_history(order_code) USING HASH;
CREATE INDEX id_order_history_trade_size ON order_history(trade_size);
CREATE INDEX id_order_history_ti_code ON order_history(ti_code) USING HASH;



