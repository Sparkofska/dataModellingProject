CREATE TABLE IF NOT EXISTS Period (
	Date INTEGER NOT NULL,
	Mth INTEGER,
	Qtr INTEGER,
	Yr INTEGER,
	Fiscal_Yr INTEGER,
	PRIMARY KEY ( Date ));
	
CREATE TABLE IF NOT EXISTS ProductType (
	Prod_Type_Id INTEGER NOT NULL,
	Prod_Type_Name VARCHAR(255),
	PRIMARY KEY ( Prod_Type_Id ));
	
CREATE TABLE IF NOT EXISTS Product (
	Prod_Id INTEGER NOT NULL,
	Prod_Name VARCHAR(255),
	Prod_Type_Id INTEGER,
	PRIMARY KEY ( Prod_Id ),
	FOREIGN KEY ( Prod_Type_Id ) REFERENCES ProductType(Prod_Type_Id));
	
CREATE TABLE IF NOT EXISTS FeeType (
	Fee_Type_Id INTEGER NOT NULL,
	Fee_Type_Name VARCHAR(255),
	PRIMARY KEY ( Fee_Type_Id ));
	
CREATE TABLE IF NOT EXISTS State (
	State_Id INTEGER NOT NULL,
	State_Name VARCHAR(255),
	PRIMARY KEY ( State_Id ));
	
CREATE TABLE IF NOT EXISTS CustomerType (
	Cust_Type_Id INTEGER NOT NULL,
	Cust_Type_Name VARCHAR(255),
	PRIMARY KEY ( Cust_Type_Id ));
	
CREATE TABLE IF NOT EXISTS LocationType (
	Loc_Type_Id INTEGER NOT NULL,
	Loc_Type_Name VARCHAR(255),
	PRIMARY KEY ( Loc_Type_Id ));
	
CREATE TABLE IF NOT EXISTS Region (
	Regn_Id INTEGER NOT NULL,
	Regn_Name VARCHAR(255),
	State_Id INTEGER,
	PRIMARY KEY ( Regn_Id ),
	FOREIGN KEY ( State_Id ) REFERENCES State(State_Id));
	
CREATE TABLE IF NOT EXISTS Customer (
	Cust_Id INTEGER NOT NULL,
	Cust_Name VARCHAR(255),
	Cust_Type_Id INTEGER,
	Cust_Regn_Id INTEGER,
	PRIMARY KEY ( Cust_Id ),
	FOREIGN KEY ( Cust_Type_Id ) REFERENCES CustomerType(Cust_Type_Id),
	FOREIGN KEY ( Cust_Regn_Id ) REFERENCES Region(Regn_Id));
	
CREATE TABLE IF NOT EXISTS Location (
	Loc_Id INTEGER NOT NULL,
	Loc_Name VARCHAR(255),
	Loc_Regn_Id INTEGER,
	Loc_Type_Id INTEGER,
	PRIMARY KEY ( Loc_Id ),
	FOREIGN KEY ( Loc_Regn_Id ) REFERENCES Region(Regn_Id),
	FOREIGN KEY ( Loc_Type_Id ) REFERENCES LocationType(Loc_Type_Id));
	
CREATE TABLE IF NOT EXISTS Sale (
	Sale_Id INTEGER NOT NULL,
	Sale_Date INTEGER,
	Sale_Posted INTEGER,
	Cust_Id INTEGER,
	Loc_Id INTEGER,
	Discount_Amt FLOAT,
	PRIMARY KEY ( Sale_Id ),
	FOREIGN KEY ( Sale_Date ) REFERENCES Period(Date),
	FOREIGN KEY ( Sale_Posted ) REFERENCES Period(Date),
	FOREIGN KEY ( Cust_Id ) REFERENCES Customer(Cust_Id),
	FOREIGN KEY ( Loc_Id ) REFERENCES Location(Loc_Id));

CREATE TABLE IF NOT EXISTS SaleFee (
	Sale_Id INTEGER NOT NULL,
	Fee_Type_Id INTEGER NOT NULL,
	Fee FLOAT,
	PRIMARY KEY ( Sale_Id, Fee_Type_Id ),
	FOREIGN KEY ( Sale_Id ) REFERENCES Sale(Sale_Id),
	FOREIGN KEY ( Fee_Type_Id) REFERENCES FeeType(Fee_Type_Id));
		
CREATE TABLE IF NOT EXISTS SaleItem (
	Sale_Id INTEGER NOT NULL,
	Prod_Id INTEGER NOT NULL,
	Qty INTEGER,
	Unit_Price FLOAT,
	PRIMARY KEY ( Sale_Id, Prod_Id ),
	FOREIGN KEY ( Sale_Id ) REFERENCES Sale(Sale_Id),
	FOREIGN KEY ( Prod_id ) REFERENCES Product(Prod_Id));