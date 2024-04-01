CREATE DATABASE Modellflugplatz;

CREATE TABLE Nutzer(
	NutzerID VARCHAR(3) not null, CONSTRAINT PK_NutzerID PRIMARY KEY CLUSTERED (NutzerID),
	Name VARCHAR(100) not null,
	Adresse VARCHAR(100) not null,
	Adminrechte VARCHAR(1))

CREATE TABLE Modell(
	ModellID VARCHAR(3) not null, CONSTRAINT PK_ModellID PRIMARY KEY CLUSTERED (ModellID),
	Bezeichnung VARCHAR(100),
	Klasse VARCHAR (20) not Null,
	Frequenz VARCHAR (20) not Null,
	Besitzer VARCHAR (3) not null, CONSTRAINT FK_Besitzer FOREIGN KEY (Besitzer) REFERENCES NutzerID(Nutzer))

CREATE TABLE Protokoll(
	ProtokollID VARCHAR(3) not null, CONSTRAINT PK_ProtokollID PRIMARY KEY CLUSTERED (ProtokollID),
	FlugID VARCHAR(3) not null,
	NutzerID VARCHAR(3) not null, CONSTRAINT FK_Besitzer FOREIGN KEY (NutzerID) REFERENCES NutzerID(Nutzer),
	Flugbeginn TIME,
	Flugende TIME,
	Unterschrift VARCHAR(10),				--Datentyp f�r Bilder?
	Erste_Hilfe VARCHAR(20),
	Bermekungen VARCHAR(250))

CREATE TABLE Flug(
	FlugID VARCHAR(3) not null, CONSTRAINT PK_FlugID PRIMARY KEY CLUSTERED (FlugID),
	TeilnehmerID VARCHAR(3) not null, CONSTRAINT FK_Besitzer FOREIGN KEY (TeilnehmerID) REFERENCES TeilnehmerID(Protokoll),
	Flugleiter VARCHAR(50) not null)



