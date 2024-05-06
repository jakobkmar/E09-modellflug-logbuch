CREATE DATABASE Modellflugplatz;

CREATE TABLE Nutzer(
	NutzerID VARCHAR(3) not null, CONSTRAINT PK_NutzerID PRIMARY KEY (NutzerID),
	Name VARCHAR(100) not null,
	Registriernummer VARCHAR(16) not null,
	hashedPasswort VARCHAR(256),
	Adresse VARCHAR(100),
	Adminrechte BOOLEAN not null
)

CREATE TABLE Modell(
	ModellID VARCHAR(3) not null, CONSTRAINT PK_ModellID PRIMARY KEY (ModellID),
	Bezeichnung VARCHAR(100),
	Klasse VARCHAR (20) not Null,
	Besitzer VARCHAR (3) not null, CONSTRAINT FK_Besitzer FOREIGN KEY (Besitzer) REFERENCES NutzerID(Nutzer)
)

CREATE TABLE Protokoll(
	ProtokollID VARCHAR(3) not null, CONSTRAINT PK_ProtokollID PRIMARY KEY (ProtokollID),
	ErstellerID VARCHAR(3) not null, CONSTRAINT FK_Ersteller FOREIGN KEY (ErstellerID) REFERENCES NutzerID(Nutzer),
	Flugbeginn TIME,
	Flugende TIME,
	Unterschrift VARBINARY(),
	ErsteHilfe VARCHAR(20),
	Bemerkungen VARCHAR(250),
	Flugleiter BOOLEAN not null,
	Modell VARCHAR(3), CONSTRAINT FK_Modell FOREIGN KEY (Modell) REFERENCES ModellID(Modell)
)
