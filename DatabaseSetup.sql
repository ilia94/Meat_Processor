DROP DATABASE if exists MeatDatabase;

CREATE DATABASE MeatDatabase;

Use MeatDatabase;

CREATE TABLE Users (
username varchar(30) PRIMARY KEY NOT NULL, 
password varchar(30) NOT NULL
);

CREATE TABLE Files (
fileID int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
filename varchar(30) NOT NULL,
owner varchar(30) NOT NULL,
FOREIGN KEY fk1(owner) REFERENCES Users(username)
);

CREATE TABLE FileShare (
fileID int(11) NOT NULL,
sharedUser varchar(30) NOT NULL,
FOREIGN KEY fk2(fileID) REFERENCES Files(fileID),
PRIMARY KEY(fileID, sharedUser)
);
