USE lse_tickdata;

CREATE TABLE order_detail_raw (
	OrderCode VARCHAR(10) NOT NULL,
	MarketSegmentCode VARCHAR(4) NOT NULL,
	MarketSectorCode VARCHAR(4) NOT NULL,
	TICode VARCHAR(12) NOT NULL,
	CountryOfRegister VARCHAR(2) NOT NULL,
	CurrencyCode VARCHAR(3) NOT NULL,
	ParticipantCode VARCHAR(11) NULL,
	BuySellInd CHAR(1) NOT NULL,
	MarketMechanismGroup VARCHAR(1) NOT NULL,
	MarketMechanismType VARCHAR(2) NOT NULL,
	Price DECIMAL(18, 8) NOT NULL,
	AggregateSize DECIMAL(12, 0) NOT NULL,
	SingleFillInd VARCHAR(1) NOT NULL,
	BroadcastUpdateAction VARCHAR(1) NOT NULL,
	Date VARCHAR(8) NOT NULL,
	Time VARCHAR (15) NOT NULL,
	MessageSequenceNumber int NOT NULL
);

CREATE TABLE orders (
	order_code VARCHAR(10) PRIMARY KEY NOT NULL,
	market_segment_code VARCHAR(4) NOT NULL,
	market_sector_code VARCHAR(4) NOT NULL,
	ti_code VARCHAR(12) NOT NULL,
	country_of_register VARCHAR(2) NOT NULL,
	currency_code VARCHAR(3) NOT NULL,
	participant_code VARCHAR(11) NULL,
	is_buy CHAR(1) NOT NULL,
	market_mechanism_group VARCHAR(1) NOT NULL,
	market_mechanism_type VARCHAR(2) NOT NULL,
	price DECIMAL(18, 8) NOT NULL,
	aggregate_size DECIMAL(12, 0) NOT NULL,
	is_single_fill VARCHAR(1) NOT NULL,
	broadcast_update_action VARCHAR(1) NOT NULL,
	time_stamp BIGINT UNSIGNED NOT NULL,
	message_sequence_number BIGINT UNSIGNED NOT NULL
);

CREATE INDEX id_orders_time ON orders (timestamp, message_sequence_number);
CREATE INDEX id_orders_price ON orders (price);
CREATE INDEX id_orders_ti_code ON orders (ti_code);

