
DROP TABLE [Admin]
go

DROP TABLE [CourierRequest]
go

DROP TABLE [TransportOffer]
go

DROP TABLE [Package]
go

DROP TABLE [District]
go

DROP TABLE [City]
go

DROP TABLE [Courier]
go

DROP TABLE [User]
go

DROP TABLE [Vehicle]
go

CREATE TABLE [Admin]
( 
	[AdminUsername]      varchar(100)  NOT NULL 
)
go

CREATE TABLE [City]
( 
	[Name]               varchar(100)  NULL ,
	[IdCity]             integer  IDENTITY  NOT NULL ,
	[PostCode]           varchar(100)  NOT NULL 
)
go

CREATE TABLE [Courier]
( 
	[NumOfDeliveredPckgs] integer  NULL 
	CONSTRAINT [zero_205055169]
		 DEFAULT  0,
	[Profit]             integer  NULL ,
	[Status]             integer  NULL 
	CONSTRAINT [Status_0_or_1_1284597617]
		CHECK  ( Status BETWEEN 0 AND 1 ),
	[CourierUsername]    varchar(100)  NOT NULL ,
	[LicencePlateNum]    varchar(100)  NOT NULL 
)
go

CREATE TABLE [CourierRequest]
( 
	[Username]           varchar(100)  NOT NULL ,
	[LicencePlateNum]    varchar(100)  NOT NULL 
)
go

CREATE TABLE [District]
( 
	[IdDistr]            integer  IDENTITY  NOT NULL ,
	[Name]               varchar(100)  NULL ,
	[xCord]              integer  NULL ,
	[yCord]              integer  NULL ,
	[IdCity]             integer  NOT NULL 
)
go

CREATE TABLE [Package]
( 
	[IdPckg]             integer  IDENTITY  NOT NULL ,
	[Weight]             integer  NULL ,
	[Type]               integer  NULL 
	CONSTRAINT [Type_0_to_2_132330555]
		CHECK  ( Type BETWEEN 0 AND 2 ),
	[DistrictFrom]       integer  NOT NULL ,
	[DistrictTo]         integer  NOT NULL ,
	[UserUsername]       varchar(100)  NOT NULL ,
	[Status]             integer  NULL 
	CONSTRAINT [zero_1532844280]
		 DEFAULT  0
	CONSTRAINT [Status_0_to_3_1721459323]
		CHECK  ( Status BETWEEN 0 AND 3 ),
	[Price]              decimal(10,3)  NULL 
	CONSTRAINT [zero_1634230640]
		 DEFAULT  0,
	[AcceptanceTime]     datetime  NULL ,
	[CourierUsername]    varchar(100)  NULL 
)
go

CREATE TABLE [TransportOffer]
( 
	[PricePercentage]    decimal(10,3)  NULL ,
	[CourierUsername]    varchar(100)  NOT NULL ,
	[IdPckg]             integer  NOT NULL ,
	[IdOffer]            integer  IDENTITY  NOT NULL 
)
go

CREATE TABLE [User]
( 
	[Firstname]          varchar(100)  NULL ,
	[Lastname]           varchar(100)  NULL ,
	[Username]           varchar(100)  NOT NULL ,
	[Password]           char(18)  NULL ,
	[NumOfSentPckgs]     varchar(100)  NULL 
	CONSTRAINT [zero_369186927]
		 DEFAULT  0
)
go

CREATE TABLE [Vehicle]
( 
	[LicencePlateNum]    varchar(100)  NOT NULL ,
	[FuelType]           integer  NULL 
	CONSTRAINT [Type_0_to_2_2032644521]
		CHECK  ( FuelType BETWEEN 0 AND 2 ),
	[FuelConsumption]    decimal(10,3)  NULL 
)
go

ALTER TABLE [Admin]
	ADD CONSTRAINT [XPKAdmin] PRIMARY KEY  CLUSTERED ([AdminUsername] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([IdCity] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XAK1City] UNIQUE ([PostCode]  ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XAK2City] UNIQUE ([Name]  ASC)
go

ALTER TABLE [Courier]
	ADD CONSTRAINT [XPKCourier] PRIMARY KEY  CLUSTERED ([CourierUsername] ASC)
go

ALTER TABLE [CourierRequest]
	ADD CONSTRAINT [XPKCourierRequest] PRIMARY KEY  CLUSTERED ([Username] ASC,[LicencePlateNum] ASC)
go

ALTER TABLE [District]
	ADD CONSTRAINT [XPKDistrict] PRIMARY KEY  CLUSTERED ([IdDistr] ASC)
go

ALTER TABLE [Package]
	ADD CONSTRAINT [XPKPackage] PRIMARY KEY  CLUSTERED ([IdPckg] ASC)
go

ALTER TABLE [TransportOffer]
	ADD CONSTRAINT [XPKTransportOffer] PRIMARY KEY  CLUSTERED ([IdOffer] ASC)
go

ALTER TABLE [User]
	ADD CONSTRAINT [XPKUser] PRIMARY KEY  CLUSTERED ([Username] ASC)
go

ALTER TABLE [Vehicle]
	ADD CONSTRAINT [XPKVehicle] PRIMARY KEY  CLUSTERED ([LicencePlateNum] ASC)
go


ALTER TABLE [Admin]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([AdminUsername]) REFERENCES [User]([Username])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Courier]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([CourierUsername]) REFERENCES [User]([Username])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go

ALTER TABLE [Courier]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([LicencePlateNum]) REFERENCES [Vehicle]([LicencePlateNum])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [CourierRequest]
	ADD CONSTRAINT [R_5] FOREIGN KEY ([Username]) REFERENCES [User]([Username])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [CourierRequest]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([LicencePlateNum]) REFERENCES [Vehicle]([LicencePlateNum])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [District]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([IdCity]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Package]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([DistrictFrom]) REFERENCES [District]([IdDistr])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Package]
	ADD CONSTRAINT [R_9] FOREIGN KEY ([DistrictTo]) REFERENCES [District]([IdDistr])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Package]
	ADD CONSTRAINT [R_10] FOREIGN KEY ([UserUsername]) REFERENCES [User]([Username])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Package]
	ADD CONSTRAINT [R_19] FOREIGN KEY ([CourierUsername]) REFERENCES [Courier]([CourierUsername])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [TransportOffer]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([CourierUsername]) REFERENCES [Courier]([CourierUsername])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [TransportOffer]
	ADD CONSTRAINT [R_14] FOREIGN KEY ([IdPckg]) REFERENCES [Package]([IdPckg])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go
