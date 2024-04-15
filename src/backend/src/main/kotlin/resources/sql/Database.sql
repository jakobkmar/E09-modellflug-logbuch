CREATE DATABASE Modellflugplatz;

CREATE TABLE Nutzer(
	NutzerID VARCHAR(3) not null, CONSTRAINT PK_NutzerID PRIMARY KEY CLUSTERED (NutzerID),
	Name VARCHAR(100) not null,
	Adresse VARCHAR(100),
	Adminrechte BOOLEAN not null)

CREATE TABLE Modell(
	ModellID VARCHAR(3) not null, CONSTRAINT PK_ModellID PRIMARY KEY CLUSTERED (ModellID),
	Bezeichnung VARCHAR(100),
	Klasse VARCHAR (20) not Null,
	Besitzer VARCHAR (3) not null, CONSTRAINT FK_Besitzer FOREIGN KEY (Besitzer) REFERENCES NutzerID(Nutzer))

CREATE TABLE Protokoll(
	ProtokollID VARCHAR(3) not null, CONSTRAINT PK_ProtokollID PRIMARY KEY CLUSTERED (ProtokollID),
	ErstellerID VARCHAR(3) not null, CONSTRAINT FK_Ersteller FOREIGN KEY (ErstellerID) REFERENCES NutzerID(Nutzer),
	Flugbeginn TIME,
	Flugende TIME,
	Unterschrift VARBINARY(),
	Erste_Hilfe VARCHAR(20),
	Bermekungen VARCHAR(250))

CREATE TABLE Flug(
	FlugID VARCHAR(3) not null, CONSTRAINT PK_FlugID PRIMARY KEY CLUSTERED (FlugID),
	ProtokollID VARCHAR(3) not null, CONSTRAINT FK_ProtokollID FOREIGN KEY (ProtokollID) REFERENCES ProtokollID(Protokoll),
	TeilnehmerID VARCHAR(3) not null,
	Flugleiter BOOLEAN not null,
	Modell VARCHAR(3), CONSTRAINT FK_Modell FOREIGN KEY (Modell) REFERENCES (ModellID))




