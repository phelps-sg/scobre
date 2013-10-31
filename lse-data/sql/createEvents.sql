USE lse_tickdata;

CREATE TABLE events (
	event_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,

-- type of event
	event_type ENUM('order_submitted',
	                    'order_revised', 'transaction',
	                    'order_deleted', 'order_expired', 'order_matched',
	                    'order_filled', 'transaction_limit') NOT NULL,

-- common to all events
	message_sequence_number BIGINT UNSIGNED NOT NULL,
	time_stamp BIGINT UNSIGNED NOT NULL,
	ti_code VARCHAR(12) NOT NULL,
	market_segment_code VARCHAR(4) NOT NULL,
--	country_of_register VARCHAR(2) NOT NULL,
	currency_code VARCHAR(3) NOT NULL,

-- common to order events
    market_mechanism_type VARCHAR(2) NULL,
    aggregate_size DECIMAL(12, 0) NULL,
    trade_direction ENUM('buy', 'sell') NULL,
    order_code VARCHAR(10) NULL,

-- common to transactions and revisions
	trade_size DECIMAL(12, 0) NULL,

-- common to submissions and transactions
    broadcast_update_action VARCHAR(1) NULL,
	price DECIMAL(18, 8) NULL,

-- order_submitted
    market_sector_code VARCHAR(4) NULL,
--    participant_code VARCHAR(11) NULL,
    market_mechanism_group VARCHAR(1) NULL,
--    price DECIMAL(18, 8) NULL,
    single_fill_ind VARCHAR(1) NULL,

-- order_matched
    matching_order_code VARCHAR(50) NULL,

-- order_filled
    resulting_trade_code VARCHAR(50) NULL,

-- transaction
    trade_code VARCHAR(50) NULL,
--	trade_type_ind VARCHAR(2) NULL,
	trade_time_ind VARCHAR(1) NULL,
--	bargain_conditions VARCHAR(1) NULL,
	converted_price_ind VARCHAR(1) NULL
--	publication_time_stamp BIGINT UNSIGNED NULL
);

CREATE INDEX id_events_time ON events(ti_code, time_stamp, message_sequence_number, event_type, order_code) USING BTREE;

CREATE INDEX id_events_trade_size ON events(buy_sell_ind, trade_size);

CREATE INDEX id_events_order_code ON events(order_code) USING HASH;
CREATE INDEX id_events_trade_code ON events(trade_code) USING HASH;

CREATE INDEX id_events_type_dir_price ON events (market_mechanism_type, buy_sell_ind, price);
