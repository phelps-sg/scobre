USE lse_tickdata;

CREATE TABLE events (
	event_id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
	event_type ENUM('order_submitted', 'order_revised', 'transaction') NOT NULL,
	order_history_event_id BIGINT UNSIGNED REFERENCES order_history(event_id),
	order_code VARCHAR(10) REFERENCES orders(order_code),
	transaction_id BIGINT UNSIGNED REFERENCES transactions(transaction_id)
	message_sequence_number UNSIGNED INT NOT NULL,
	time_stamp BIGINT UNSIGNED NOT NULL
);

CREATE INDEX id_events_time ON events(timestamp, message_sequence_number) USING BTREE;