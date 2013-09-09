USE lse_tickdata;

CREATE TABLE trade_reports_raw (
	message_sequence_number BIGINT UNSIGNED NOT NULL,
	ti_code VARCHAR(12) NOT NULL,
	market_segment_code varchar(4) NOT NULL,
	CountryOfRegister varchar(2) NOT NULL,
	CurrencyCode varchar(3) NOT NULL,
	TradeCode varchar(50) NOT NULL,
	TradePrice decimal(18, 8) NOT NULL,
	TradeSize decimal(12, 0) NOT NULL,
	TradeDate varchar(8) NOT NULL,
	TradeTime varchar(15) NOT NULL,
	BroadcastUpdateAction varchar(1) NOT NULL,
	TradeTypeInd varchar(2) NOT NULL,
	TradeTimeInd varchar(1) NOT NULL,
	BargainConditions varchar(1) NOT NULL,
	ConvertedPriceInd varchar(1) NOT NULL,
	PublicationDate varchar(8) NOT NULL,
	PublicationTime varchar(8) NOT NULL
);
