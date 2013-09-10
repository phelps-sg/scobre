USE lse_tickdata;

CREATE TABLE trade_reports_raw (
	message_sequence_number BIGINT UNSIGNED NOT NULL,
	ti_code VARCHAR(12) NOT NULL,
	market_segment_code VARCHAR(4) NOT NULL,
	country_of_register VARCHAR(2) NOT NULL,
	currency_code VARCHAR(3) NOT NULL,
	trade_code VARCHAR(50) NOT NULL,
	trade_price DECIMAL(18, 8) NOT NULL,
	trade_size DECIMAL(12, 0) NOT NULL,
	trade_date VARCHAR(8) NOT NULL,
	trade_time VARCHAR(15) NOT NULL,
	broadcast_update_action VARCHAR(1) NOT NULL,
	trade_type_ind VARCHAR(2) NOT NULL,
	trade_time_ind VARCHAR(1) NOT NULL,
	bargain_conditions VARCHAR(1) NOT NULL,
	converted_price_ind VARCHAR(1) NOT NULL,
	publication_date VARCHAR(8) NOT NULL,
	publication_time VARCHAR(15) NOT NULL
);

CREATE TABLE transactions (
	transaction_id BIGINT UNSIGNED PRIMARY KEY,
--	message_sequence_number BIGINT UNSIGNED NOT NULL,
-- 	ti_code VARCHAR(12) NOT NULL,
-- 	market_segment_code VARCHAR(4) NOT NULL,
-- 	country_of_register VARCHAR(2) NOT NULL,
-- 	currency_code VARCHAR(3) NOT NULL,
	trade_code VARCHAR(50) NOT NULL,
	trade_price DECIMAL(18, 8) NOT NULL,
	trade_size DECIMAL(12, 0) NOT NULL,
--	trade_time_stamp BIGINT UNSIGNED NOT NULL,
	broadcast_update_action VARCHAR(1) NOT NULL,
	trade_type_ind VARCHAR(2) NOT NULL,
	trade_time_ind VARCHAR(1) NOT NULL,
	bargain_conditions VARCHAR(1) NOT NULL,
	converted_price_ind VARCHAR(1) NOT NULL,
	publication_time_stamp BIGINT UNSIGNED NOT NULL
);

CREATE INDEX id_transactions_publication ON transactions(publication_time_stamp);
CREATE INDEX id_transactions_trade_size ON transactions(trade_size);
CREATE INDEX id_transactions_trade_price ON transactions(trade_price);
