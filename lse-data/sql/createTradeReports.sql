USE lse;

CREATE TABLE tblTradeReports(
	MessageSequenceNumber int NOT NULL,
	TICode varchar(12) NOT NULL,
	MarketSeqmentCode varchar(4) NOT NULL,
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
