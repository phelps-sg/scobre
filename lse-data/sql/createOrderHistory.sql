USE lse;

CREATE TABLE tblOrderHistory (
	OrderCode varchar(10) NOT NULL,
	OrderActionType char(1) NOT NULL,
	MatchingOrderCode varchar(50) NULL,
	TradeSize decimal(8, 0) NULL,
	TradeCode varchar(50) NULL,
	TICode varchar(12) NOT NULL,
	CountryOfRegister varchar(2) NOT NULL,
	CurrencyCode varchar(3) NOT NULL,
	MarketSegmentCode varchar(4) NOT NULL,
	AggregateSize decimal(12, 0) NULL,
	BuySellInd char(1) NOT NULL,
	MarketMechanismType varchar(2) NOT NULL,
	MessageSequenceNumber int NOT NULL,
	Date varchar(8) NOT NULL,
	Time varchar (15) NOT NULL
); 
