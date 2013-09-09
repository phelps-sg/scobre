USE lse_tickdata;

CREATE TABLE events (
	event_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	event_type ENUM('order_submitted', 'order_revised', 'transaction') NOT NULL,
	order_history_event_id BIGINT UNSIGNED REFERENCES order_history(event_id),
	order_code VARCHAR(10) REFERENCES orders(order_code),
	transaction_id BIGINT UNSIGNED REFERENCES transactions(transaction_id),
	message_sequence_number INT UNSIGNED NOT NULL,
	time_stamp BIGINT UNSIGNED NOT NULL,
	ti_code VARCHAR(12) NOT NULL,
	market_segment_code VARCHAR(4) NOT NULL,
	country_of_register VARCHAR(2) NOT NULL,
	currency_code VARCHAR(3) NOT NULL
);

CREATE INDEX id_events_time ON events(time_stamp, message_sequence_number) USING BTREE;
CREATE INDEX id_events_ti_code ON events(ti_code) USING HASH;
