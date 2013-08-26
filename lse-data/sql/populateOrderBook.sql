USE lse;

CREATE TABLE tblOrderBook (
	EventId int NOT NULL,
	Price decimal(18, 8) NULL,
	AggregateSize decimal(12, 0) NULL,
	OrderActionType char(1) NOT NULL,
	OrderCode varchar(10) NOT NULL,
	BuySellInd char(1) NULL,
	Date varchar(8) NOT NULL,
	Time varchar (15) NOT NULL,
	MessageSequenceNumber int NOT NULL,
	MarketMechanismType varchar(2) NOT NULL,
	TradeSize decimal(8, 0) NULL,
	MatchingOrderCode varchar(50) NULL,
	MatchingPrice decimal(18, 8) NULL,
	OrderDetailSize decimal(12, 0) NULL,
	IORealSize decimal(12, 0) NULL,
	TradeCode varchar(50) NULL,
	
	PRIMARY KEY (EventId)
); 
