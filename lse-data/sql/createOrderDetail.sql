USE lse;
CREATE TABLE tblOrderDetail (
	OrderCode varchar(10) NOT NULL,
	MarketSegmentCode varchar(4) NOT NULL,
	MarketSectorCode varchar(4) NOT NULL,
	TICode varchar(12) NOT NULL,
	CountryOfRegister varchar(2) NOT NULL,
	CurrencyCode varchar(3) NOT NULL,
	ParticipantCode varchar(11) NULL,
	BuySellInd char(1) NOT NULL,
	MarketMechanismGroup varchar(1) NOT NULL,
	MarketMechanismType varchar(2) NOT NULL,
	Price decimal(18, 8) NOT NULL,
	AggregateSize decimal(12, 0) NOT NULL,
	SingleFillInd varchar(1) NOT NULL,
	BroadcastUpdateAction varchar(1) NOT NULL,
	Date varchar(8) NOT NULL,
	Time varchar (15) NOT NULL,
	MessageSequenceNumber int NOT NULL
);